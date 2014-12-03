package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;

import sun.org.mozilla.javascript.EcmaError;
import common.CommunicationHandler;
import common.CommunicationMessage;
import common.Encryption;
import common.ObjectConstructionException;
import common.SocketReadException;
import common.Status;


public class RequestHandler implements Runnable
{
	public Socket socket;
	public CommunicationHandler comm;
	public int ID = -1;
	public Boolean authenticated = false;
	
	public RequestHandler(Socket socket){
		this.socket = socket;
		comm = new CommunicationHandler(this.socket);
	}

	@Override
	public void run()
	{
		while(!socket.isClosed()){
			if (!authenticated){
				try {
					System.out.println("authenticating");
					authenticated = comm.authenticate();
					sendOK();
				} catch (Exception e){
					closeConnection();
					return;
				}
			} else {
				try{
					CommunicationMessage fileReq = read();
					sendFile(fileReq.data);
				} catch (SocketReadException e){
					closeConnection();
				} catch (ObjectConstructionException ee){
					sendFileNotFound();
				}
			}
		}
	}
	
	public CommunicationMessage read() throws SocketReadException, ObjectConstructionException{
		try{
			return comm.receiveCommunication();
		} catch (IOException e){
			throw new SocketReadException();
		}
	}
	public void sendForbidden(){
		try {
			comm.sendCommunication(new CommunicationMessage(Status.PD, 0, null, 0));
		} catch (Exception e) {}
	}
	public void sendFileNotFound(){
		try {
			comm.sendCommunication(new CommunicationMessage(Status.FNF, 0, null, 0));
		} catch (Exception e) {}
	}
	public void sendOK(){
		try {
			comm.sendCommunication(new CommunicationMessage(Status.OK, 0, null, 0));
		} catch (Exception e) {}
	}
	
	public void sendFileOK(long filesize){
		try {
			comm.sendCommunication(new CommunicationMessage(Status.OK, 0, null, filesize));
		} catch (Exception e) {}
	}
	
	public void sendFile(String filename){
		try{
			FileReader os = findFile(filename);
			comm.sendFile(os);
			
		} catch (AccessDeniedException e){
			sendForbidden();
		} catch (IOException e) {
			sendFileNotFound();
		} catch (Exception e){
			e.printStackTrace();
		}
	};
	
	public FileReader findFile(String filename) throws IOException, AccessDeniedException{
		String RequestedFileName = Helpers.buildFilePathString(filename);
		
		if (Helpers.validateFile(filename)){
			return new FileReader(new File(RequestedFileName));
		} else {
			return null;
		}
	}
	
	public long getFileSize(String filename) throws AccessDeniedException, IOException{
		File req = null;
		if (Helpers.validateFile(filename)){
			req = new File(Helpers.buildFilePathString(filename));
		}
		
		return req.length();
	}
	
	private void closeConnection(){
		try{
			socket.close();
		} catch (Exception ee){}
		return;
	}

}
