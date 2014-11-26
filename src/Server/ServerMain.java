package Server;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain
{
	private static final Integer port = 16000;
	private ServerSocket socket;
	
	public void start (){
		try{
			initialize();
			listen();
			
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Server Unexpectedly Quit");
		}
	}
	
	private void initialize(){
		try
		{
			socket = new ServerSocket(port);
			
		} catch (IOException e)
		{
			System.out.println("Error opening server socket");
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	private void listen(){
		try{
			ExecutorService executor = Executors.newCachedThreadPool();
			while(true){
				Socket connection = socket.accept();
				executor.submit(new RequestHandler(connection));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
 