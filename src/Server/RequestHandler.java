package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import common.CommunicationHandler;
import common.CommunicationMessage;
import common.Encryption;
import common.SocketReadException;
import common.Status;


public class RequestHandler implements Runnable
{
	private Socket socket;
	private Encryption encryption = new Encryption();
	private CredentialCache cache = new CredentialCache();
	private CommunicationHandler comm;
	
	public RequestHandler(Socket socket){
		this.socket = socket;
		comm = new CommunicationHandler(this.socket);
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}
	
	private CommunicationMessage read() throws SocketReadException{
		try{		
			return comm.receiveCommunication();
		} catch (Exception e){
			throw new SocketReadException();
		} 
	}
	private void sendForbidden(){
		try {
			comm.sendCommunication(new CommunicationMessage(Status.PermissionDenied, 0, null));
		} catch (Exception e) {}
	}
	private void sendFileNotFound(){
		try {
			comm.sendCommunication(new CommunicationMessage(Status.FileNotFound, 0, null));
		} catch (Exception e) {}
	}
	private void sendOK(){
		try {
			comm.sendCommunication(new CommunicationMessage(Status.OK, 0, null));
		} catch (Exception e) {}
	}
	
	private void sendFile(String filename){
		
	};
	
	private File findFile(String filename){
		File directory = new File("www");
		File [] files = directory.listFiles();
		for (File file : files){
			if ()
		}
	}
}
