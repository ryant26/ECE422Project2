package tests;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

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
		
		
	}

}
