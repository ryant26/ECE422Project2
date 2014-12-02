package Server;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain
{
	public static final Integer port = 16000;
	public ServerSocket socket;
	
	public void start (){
		try{
			initialize();
			System.out.println("Server Up and listening on 16000");
			listen();
			
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Server Unexpectedly Quit");
		}
	}
	
	public void initialize(){
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
	
	public void listen(){
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
 