package common;


public class Encryption
{
	static{
		//System.loadLibrary("cencryption");
		System.load("/home/cmput296/Desktop/ECE422/Proj2/src/libcencryption.so");
	}
	
	private long [] plainText;
	private long [] cipherText;
	
	private native void decrypt(long [] cipher, long [] key);
	private native void encrypt(long [] plain, long [] key);
	
	public void encryption (long [] plainText, long [] key){
		this.plainText = plainText;
		encrypt(plainText, key);
		cipherText = this.plainText;
	}
	
	public void decryption(long [] cipherText, long [] key){
		this.cipherText = cipherText;
		decrypt(cipherText, key);
		this.plainText = cipherText;
	}
}
