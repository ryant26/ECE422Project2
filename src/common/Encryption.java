package common;


public class Encryption
{
	static{
		System.loadLibrary("cencryption");
	}
	
	public long [] plainText;
	public long [] cipherText;
	
	public native void decrypt(long [] cipher, long [] key);
	public native void encrypt(long [] plain, long [] key);
	
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
