package common;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import Server.CredentialCache;

public class CommunicationHandler {
	
	private Socket socket;
	private Encryption encryption = new Encryption();
	private CredentialCache cache = new CredentialCache();
	private int ID =  -1;
	private DataInputStream dataBuffer = null;
	
	public CommunicationHandler(Socket socket) {
		this.socket = socket;
	}
	
	public void sendCommunication(CommunicationMessage msg)throws IOException, ObjectConstructionException{
		byte [] msgArray;
		try {
			msgArray = toByteArray(msg);
			
		} catch (Exception e){
			throw  new ObjectConstructionException();
		}
		
		sendRaw(msgArray);
	}
	
	public CommunicationMessage receiveCommunication() throws ObjectConstructionException, IOException{
		long [] plain = recieveRaw();
		long [] plainNoHeader = Arrays.copyOfRange(plain, 1, plain.length);
		long size = plain[0];
		
		byte [] commObj = Arrays.copyOf(LongArraytoByteArray(plainNoHeader), (int)size);
		CommunicationMessage commMessage = null;
		try {
			commMessage = (CommunicationMessage) toObject(commObj);
		} catch (Exception e){
			throw new ObjectConstructionException();
		}
		
		return commMessage;
	}
	
	public void sendRaw(byte [] msgArray) throws IOException{

		long msgLength = msgArray.length;
		byte [] lenArray = ByteBuffer.allocate(8).putLong(msgLength).array();
		byte [] sendMsg = new byte [msgArray.length + lenArray.length];
		for (int i=0; i < msgArray.length; i++){
			sendMsg[i+lenArray.length] = msgArray[i];
		}
		for (int i = 0; i < lenArray.length; i++){
			sendMsg[i] = lenArray[i];
		}

		long [] encryptedMessag  = encryptMsg(sendMsg, this.ID);

		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		for (long l : encryptedMessag){
			dos.writeLong(l);
		}
		dos.flush();
	}
	
	public long [] recieveRawEncrypted() throws IOException{
		ArrayList<Long> read = new ArrayList<Long>();
		DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

		do{
			read.add(dis.readLong());
		}while (dis.available() > 0);
		
		long [] encrypted = new long [read.size()];
		for (int i = 0; i < encrypted.length; i++){
			encrypted[i] = read.get(i);
		}
		return encrypted;
	}
	
	public long [] recieveRaw() throws IOException{
				long [] encrypted = recieveRawEncrypted();
				
				long [] plain = decryptMsg(encrypted, this.ID);
				return plain;
	}
	
	private byte [] toByteArray(Object obj) throws Exception{
		try {
			byte[] bytes = null;
	        ByteArrayOutputStream bos = null;
	        ObjectOutputStream oos = null;
	        try {
	            bos = new ByteArrayOutputStream();
	            oos = new ObjectOutputStream(bos);
	            oos.writeObject(obj);
	            oos.flush();
	            bytes = bos.toByteArray();
	        } finally {
	            if (oos != null) {
	                oos.close();
	            }
	            if (bos != null) {
	                bos.close();
	            }
	        }
	        return bytes;
		}catch  (Exception e){
			System.out.println("error parsing object");
			throw e;
		}
    }
	
	private long [] toLongArray(byte [] array){
		byte [] forLongConversion = padMessage(array);
		long [] longArray = new long [forLongConversion.length / 8];
		ByteBuffer bb = ByteBuffer.wrap(forLongConversion);
		for (int i = 0; i< longArray.length; i++){
			longArray[i] = bb.getLong();
		}
		return longArray;
	}
	
	private byte [] LongArraytoByteArray(long [] array){
		ArrayList<byte []> al = new ArrayList<byte[]>();
		for (long l : array){
			al.add(ByteBuffer.allocate(8).putLong(l).array());
		}
		byte [] ret = new byte [al.size() * 8];
		for (int i=0; i < al.size(); i++){
			for (int j = 0; j < al.get(i).length ; j++){
				ret[(i*8)+j] = al.get(i)[j];
			}
		}
		
		return ret;
	}
	
	private Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (ois != null) {
                ois.close();
            }
        }
        return obj;
    }
	
	private byte [] padMessage(byte [] array){
		byte [] padded = array;
		if (array.length % 8 != 0){
			padded = new byte [array.length + (8-(array.length % 8))];
			for (int i = 0; i < array.length; i++){
				padded[i] = array[i];
			}
			for (int i = array.length; i < padded.length; i++){
				padded[i] = 0;
			}
		}
		
		return padded;
		
	}
	
	private long [] padMessage(long [] array){
		long [] padded = array;
		
		if (array.length % 2 != 0){
			padded = new long [array.length + 1];
			for (int i = 0; i < array.length; i++){
				padded [i] = array[i];
			}
			
			padded[padded.length - 1] = 0;
		}
		
		return padded;
	}
	
	private long [] encryptMsg(byte []  array, int ID){
		long []  partial = toLongArray(array);
		long [] longMsg =  padMessage(toLongArray(array));
		
		for (int i=0; i < longMsg.length; i += 2){
			long [] encryptArray = new long [] {longMsg[i], longMsg[i+1]};
		 	if (cache.checkID(ID)){
		 		encryption.encryption(encryptArray, cache.getEncryptionKey(ID));
		 	} else {
		 		encryption.encryption(encryptArray, cache.getRandomKey());
		 	}
		 	for (int j= 0; j < encryptArray.length; j++){
		 		longMsg[i + j] = encryptArray[j]; 
		 	}
		}

		return longMsg;
	}
	
	private long [] decryptMsg(long [] array, int ID){
		long [] longMsg = new long [array.length];
		for (int i=0; i < array.length; i +=2){
			long [] decryptArray = new long [] {array[i], array[i+1]};
		 	if (cache.checkID(ID)){
		 		encryption.decryption(decryptArray, cache.getEncryptionKey(ID));
		 	} else {
		 		encryption.decryption(decryptArray, cache.getRandomKey());
		 	}
		 	for (int j= 0; j < decryptArray.length; j++){
		 		longMsg[i + j] = decryptArray[j]; 
		 	}
		}
		return longMsg;
	}
	
	public Boolean authenticate() throws IOException{
		long [] encrypted = recieveRawEncrypted();
		
		for (Integer ID : cache.getIterableIDs()){
			try{
				long [] plain = decryptMsg(encrypted, ID);
				long [] plainNoHeader = Arrays.copyOfRange(plain, 1, plain.length);
				Long size = plain[0];
				if (size.intValue() > 0 && size.intValue() <= plainNoHeader.length * 8){
					byte [] commObj = Arrays.copyOf(LongArraytoByteArray(plainNoHeader), size.intValue());
					CommunicationMessage msg = (CommunicationMessage )toObject(commObj);
					System.out.println("passed");
					this.ID = msg.ID;
					return true;
				} else {
					throw new Exception();
				}
			} catch (Exception e){
				continue;
			}
		}
		
		return false;
	}
	
	public void sendFile (FileReader file){
		BufferedReader br = new BufferedReader(file);
		String line;
		try{
			while((line = br.readLine()) != null){
				CommunicationMessage msg = null;
				if (!br.ready()){
					msg = new CommunicationMessage(Status.EOF, this.ID, line, 0);
				} else {
					msg = new CommunicationMessage(Status.OK, this.ID, line, 0);
				}
				sendCommunication(msg);
			}
		} catch (IOException e){
			System.out.println("Error sending file");
		} catch (ObjectConstructionException ee){
			System.out.println("Error building packet for file transfer");
		}
	}
	
	public void setID(int id){
		this.ID = id;
	}
	
	public void close(){
		try{
			socket.close();
		} catch (Exception e){}
		
	}
	
	private long [] getRawEncryptedLength(int size, DataInputStream dis) throws IOException{
		long [] read = new long [size];
		for (int i = 0; i < size; i++){
			read[i]=(dis.readLong());
		}
		return read;
	}
	
	private long [] getRawLength(int size, DataInputStream dis) throws IOException{
		long [] encrypted = getRawEncryptedLength(size, dis);
		return decryptMsg(encrypted, this.ID);
	}
	
	
	
	public CommunicationMessage popCommunicationMessage () throws ClassNotFoundException, IOException{
			if (dataBuffer == null) {
				dataBuffer = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			}
			long [] firstTwo = getRawLength(2, dataBuffer);
			long byteLen = firstTwo[0];
			long paddedByteLen = byteLen + (8 - (byteLen % 8));
			int longLen = (int)(paddedByteLen/8);
			longLen += (longLen+1) % 2;
			long [] fullMsg = new long [longLen];
			fullMsg[0] = firstTwo[1];
			
			long [] otherPart = getRawLength(longLen-1, dataBuffer);
			for (int i=0; i<otherPart.length; i++){
				fullMsg[i+1] = otherPart[i];
			}
			byte [] commObj = LongArraytoByteArray(fullMsg);
			byte [] finalObj = Arrays.copyOf(commObj, (int)byteLen);;
			return (CommunicationMessage)toObject(finalObj);
		
	}
	
	public void clearDataBuffer(){
		dataBuffer = null;
	}
	
}
