#include <jni.h>
#include "common_Encryption.h"

JNIEXPORT void JNICALL Java_common_Encryption_decrypt(JNIEnv *env, jobject obj, jlongArray text, jlongArray key){

	/* JNI Stuff */
	jsize keyLen = (*env)->GetArrayLength(env, key);
	jsize textLen = (*env)->GetArrayLength(env, text);

	long * v = (*env)->GetLongArrayElements(env, text, 0);
	long * k = (*env)->GetLongArrayElements(env, key, 0);

	/* TEA decryption routine */
	unsigned long n=32, sum, y=v[0], z=v[1];
	unsigned long delta=0x9e3779b9l;

		sum = delta<<5;
		while (n-- > 0){
			z -= (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
			y -= (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
			sum -= delta;
		}
	v[0] = y;
	v[1] = z;

	(*env)->ReleaseLongArrayElements(env, text, v, 0);
	(*env)->ReleaseLongArrayElements(env, key, k, 0);
}

JNIEXPORT void JNICALL Java_common_Encryption_encrypt(JNIEnv *env, jobject obj, jlongArray text, jlongArray key){

  	/* JNI Stuff */
	jsize keyLen = (*env)->GetArrayLength(env, key);
	jsize textLen = (*env)->GetArrayLength(env, text);

	long * v = (*env)->GetLongArrayElements(env, text, 0);
	long * k = (*env)->GetLongArrayElements(env, key, 0);

	/* TEA encryption algorithm */
	unsigned long y = v[2], z=v[3], sum = 0;
	unsigned long delta = 0x9e3779b9, n=32;

	while (n-- > 0){
		sum += delta;
		y += (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
		z += (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
	}

	v[1] = y;
	v[0] = z;
	

	(*env)->ReleaseLongArrayElements(env, text, v, 0);
	(*env)->ReleaseLongArrayElements(env, key, k, 0);
}
