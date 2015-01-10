# ECE 422 Project 2 
## Encrypted File Server with JNI

This is my implementation of the 2nd (final) project in ECE 422 at Ualberta. It is a very simple encrypted file server with "user IDs"

## Notes:
* Valid Client ID = 1993
* Realitively Secure, should only serve files from www dir
* CommunicationHandler.java is a **MESS** there was a night-before-due-date fix to allow the sending of multiline files
	* Original design could only send single lines ... who designed that ....