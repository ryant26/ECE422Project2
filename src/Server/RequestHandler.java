package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.AccessDeniedException;
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
			comm.sendCommunication(new CommunicationMessage(Status.PermissionDenied, 0, null, 0));
		} catch (Exception e) {}
	}
	private void sendFileNotFound(){
		try {
			comm.sendCommunication(new CommunicationMessage(Status.FileNotFound, 0, null, 0));
		} catch (Exception e) {}
	}
	private void sendOK(){
		try {
			comm.sendCommunication(new CommunicationMessage(Status.OK, 0, null, 0));
		} catch (Exception e) {}
	}
	
	private void sendFileOK(long filesize){
		try {
			comm.sendCommunication(new CommunicationMessage(Status.OK, 0, null, filesize));
		} catch (Exception e) {}
	}
	
	private void sendFile(String filename){
		try{
			FileOutputStream os = findFile(filename);
			sendFileOK(getFileSize(filename));
			
			//TODO code the sending procedure
		} catch (AccessDeniedException e){
			sendForbidden();
		} catch (IOException e) {
			sendFileNotFound();
		} catch (Exception e){
			e.printStackTrace();
		}
	};
	
	private FileOutputStream findFile(String filename) throws IOException, AccessDeniedException{
		String RequestedFileName = Helpers.buildFilePathString(filename);
		
		if (Helpers.validateFile(filename)){
			return new FileOutputStream(new File(RequestedFileName));
		} else {
			return null;
		}
	}
	
	private long getFileSize(String filename) throws AccessDeniedException, IOException{
		File req = null;
		if (Helpers.validateFile(filename)){
			req = new File(Helpers.buildFilePathString(filename));
		}
		
		return req.length();
	}
	
	private boolean authenticate(int ID){
		return true;
	}
}
