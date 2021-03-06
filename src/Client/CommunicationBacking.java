package Client;

import java.io.IOException;
import java.net.Socket;

import common.CommunicationHandler;
import common.CommunicationMessage;
import common.Status;


public class CommunicationBacking
{
	private CommunicationHandler comm = new CommunicationHandler(new Socket("localhost", 16000));
	
	public CommunicationBacking() throws IOException {}
	
	public Boolean Authenticate(int ID){
		CommunicationMessage msg = new CommunicationMessage(Status.Auth, ID, null, 0);
		CommunicationMessage received = null;
		comm.setID(ID);
		
		try{
			comm.sendCommunication(msg);
		} catch (Exception e){
			return false;
		}
		
		try{
			received = comm.receiveCommunication();
		} catch (Exception e){
			return false;
		}
		
		if (received != null){
			return true;
		} else {
			return false;
		}	
	}
	
	public void requestFile(int ID, String filename) throws IOException{
		CommunicationMessage msg = new CommunicationMessage(Status.FQ, 0, filename, 0);
		CommunicationMessage received = null;
		System.out.println();
		try{
			comm.sendCommunication(msg);
		} catch (Exception e) {
			throw new IOException("Couldn't send file request");
		}
		do {
			try{
				received = comm.popCommunicationMessage();
				handleReply(received);
			} catch (Exception e){
				throw new IOException("Something went wrong while receiving file");
			}
		} while(received.status == Status.OK);
		System.out.println();
			
		
	}
	
	private void handleReply(CommunicationMessage msg){
		if (msg.status == Status.OK || msg.status == Status.EOF){
			System.out.println(msg.data);
			if (msg.status == Status.EOF){
				comm.clearDataBuffer();
				System.out.println(" ==== END OF FILE ====");
			}
		} else if (msg.status == Status.FNF){
			System.out.println("File does not exist on server");
			System.out.println();
			comm.clearDataBuffer();
		} else if (msg.status == Status.PD){
			System.out.println("You do not have permission to view this file");
			System.out.println();
			comm.clearDataBuffer();
		}
	}
	
	public void closeConnection(){
		comm.close();
	}
	
}
