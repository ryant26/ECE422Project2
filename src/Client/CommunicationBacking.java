package Client;

import java.io.IOError;
import java.io.IOException;
import java.net.Socket;

import common.CommunicationHandler;
import common.CommunicationMessage;
import common.ObjectConstructionException;
import common.Status;


public class CommunicationBacking
{
	private CommunicationHandler comm = new CommunicationHandler(new Socket("localhost", 16000));
	
	public CommunicationBacking() throws IOException {}
	
	public Boolean Authenticate(int ID){
		CommunicationMessage msg = new CommunicationMessage(Status.Authenticate, 0, null, 0);
		CommunicationMessage received = null;
		comm.setID(ID);
		
		try{
			comm.sendCommunication(msg);
		} catch (Exception e){
			return false;
		}
		
		try{
			received = comm.receiveCommunication(new long [1]);
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
		CommunicationMessage msg = new CommunicationMessage(Status.FileRequest, 0, filename, 0);
		Status stat = Status.OK
		
		try{
			comm.sendCommunication(msg);
		} catch (Exception e) {
			throw new IOException("Couldn't send file request");
		}
		
		try{
			comm.receiveCommunication(new log []){
				
			}
		}
		
	}
	
	
}
