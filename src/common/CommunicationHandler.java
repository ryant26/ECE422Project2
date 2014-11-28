package common;

import java.net.Socket;

public class CommunicationHandler {
	private Socket socket;
	
	public CommunicationHandler(Socket socket) {
		this.socket = socket;
	}
	
	public void sendCommunication(CommunicationMessage msg)throws Exception{
		
	}
	
	public CommunicationMessage receiveCommunication(){
		return new CommunicationMessage();
	}
}
