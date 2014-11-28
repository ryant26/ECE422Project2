package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import common.Encryption;
import common.SocketReadException;


public class RequestHandler implements Runnable
{
	private Socket socket;
	private Encryption encryption = new Encryption();
	private CredentialCache cache = new CredentialCache();
	
	public RequestHandler(Socket socket){
		this.socket = socket;
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}
	
	private List<String> parseRequest(String input){
		return Arrays.asList(input.split(""));
	}
	
	private String read() throws SocketReadException{
		try{
			ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
			is.
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return reader.readLine();
		} catch (IOException e){
			throw new SocketReadException();
		} 
	}
	private void sendForbidden(){}
	private void sendFileNotFound(){}
	private void sendOK(){}
	private void sendResponse(){}
	private void sendFile(){};
}
