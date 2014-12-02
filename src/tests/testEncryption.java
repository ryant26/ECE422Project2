package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import sun.org.mozilla.javascript.Token.CommentType;
import common.*;
import Server.*;

public class testEncryption {

	@Test
	public void test() {
		CredentialCache cache = new CredentialCache();
		Encryption encryption = new Encryption();
		
		String plainText = "Hello Worldeeeee";
		byte [] plainArray = plainText.getBytes();
		
		long  []  key = cache.getEncryptionKey(1993);
		System.out.println("======");
		for (long l : key){
			System.out.println(l);
		}
		
		ByteBuffer bb = ByteBuffer.wrap(plainArray);
		long [] mylong = new long [] {bb.getLong(), bb.getLong()};
		encryption.encryption(mylong, key);
		byte [] encryptedBytes = ByteBuffer.allocate(16).putLong(mylong[0]).putLong(mylong[1]).array();
		System.out.println("encrypted string:");
		System.out.println(new String(encryptedBytes));
		
		encryption.decryption(mylong, key);
		byte [] decryptedBytes = ByteBuffer.allocate(16).putLong(mylong[0]).putLong(mylong[1]).array();
		System.out.println("decrypted string:");
		System.out.println(new String(decryptedBytes));
		assertTrue(Arrays.equals(plainArray, decryptedBytes));
	}
	
	@Test
	public void testSendRecieve() throws  IOException, ObjectConstructionException{
		CommunicationHandler client = new CommunicationHandler(new Socket());
		
		
		CommunicationMessage msg = new CommunicationMessage(Status.OK, 10, null, 20);
		
		long [] paddedEncrypted = client.sendCommunication(msg);
		CommunicationMessage recieved = client.receiveCommunication(paddedEncrypted);
		
	}
	
	/*
	@Test
	public void tommHandlerToByte() {
		CommunicationHandler handler = new CommunicationHandler(new  Socket());
		CommunicationMessage msg = new CommunicationMessage(Status.OK, 10, "stirng", 1930L);
		try{
			byte [] original = handler.toByteArray(msg);
			byte [] originalCopy = original.clone();
			assertTrue(msg.status == ((CommunicationMessage)handler.toObject(original)).status);
			
			long [] plainLong = handler.toLongArray(original);
			long [] plainCopy = plainLong.clone();
			byte [] convertedBack = handler.LongArraytoByteArray(plainLong);
			
			for (int i = 0; i < original.length; i++){
				assertEquals(convertedBack[i], original[i]);
			}
			
			
			long [] after = handler.decryptMsg(handler.encryptMsg(originalCopy, 1993), 1993);
			
			for (int i = 0; i < plainCopy.length; i++){
				assertEquals(after[i], plainCopy[i]);
			}
			
		} catch (Exception  e){
			fail();
		}
	}*/

}
