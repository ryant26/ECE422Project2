package Server;

import java.net.Socket;
import java.util.List;

import common.Encryption;


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
	
	private List<String> parseRequest(){
		return null;
	}
	
	private void read(){}
	private void sendForbidden(){}
	private void sendFileNotFound(){}
	private void sendOK(){}
	private void sendResponse(){}
	private void sendFile(){};
}
