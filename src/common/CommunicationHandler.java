package common;

import java.awt.RadialGradientPaint;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import sun.security.util.Length;
import Server.CredentialCache;

public class CommunicationHandler {
	
	private Socket socket;
	private Encryption encryption = new Encryption();
	private CredentialCache cache = new CredentialCache();
	private int ID =  -1;
	
	public CommunicationHandler(Socket socket) {
		this.socket = socket;
	}
	
	public long [] sendCommunication(CommunicationMessage msg)throws IOException, ObjectConstructionException{
		byte [] msgArray;
		try {
			msgArray = toByteArray(msg);
		} catch (Exception e){
			throw  new ObjectConstructionException();
		}
		return sendRaw(msgArray);
		
	}
	
	public CommunicationMessage receiveCommunication(long[] recievedMsg) throws ObjectConstructionException, IOException{
		long [] plain = recieveRaw(recievedMsg);
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
	
	public long [] sendRaw(byte [] msgArray){
		//TODO change to return void and use socket
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
		return encryptedMessag;
//		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//		for (long l : encryptedMessag){
//			dos.writeLong(l);
//		}
	}
	
	public long [] recieveRaw(long [] recievedMsg){
		//TODO change to use socket and take no args
				ArrayList<Long> read = new ArrayList<Long>();
//				DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				
//				while (dis.available() > 0){
//					read.add(dis.readLong());
//				}
				
				for (int i=0; i<recievedMsg.length; i++){
					read.add(recievedMsg[i]);
				}
				
				
				long [] encrypted = new long [read.size()];
				for (int i = 0; i < encrypted.length; i++){
					encrypted[i] = read.get(i);
				}
				
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
			padded = new byte [array.length + (array.length % 8)];
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
	
	public void setID(int id){
		this.ID = id;
	}
	
}
