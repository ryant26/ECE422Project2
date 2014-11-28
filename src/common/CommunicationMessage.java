package common;

import java.io.Serializable;

public class CommunicationMessage implements Serializable{
	
	private static final long serialVersionUID = -3612434157713565391L;
	public Status status;
	public int ID;
	public String data;
	
	public CommunicationMessage(){};
	public CommunicationMessage(Status stat, int id, String data){
		status = stat;
		ID = id;
		this.data = data;
	}

}
