package Client;

import java.util.Scanner;



public class UserInterface
{
	private Scanner scanner = new Scanner(System.in);
	int inputID = -1;
	Boolean authenticated = false;
	Boolean exit = false;
	CommunicationBacking backing = new CommunicationBacking();
	
	public UserInterface() throws Exception{}
	
	
	public void run(){
		System.out.println("Welcome to Ryan's File Transfer, type exit at any time to quit!");
		authenticateLoop();
		
		fileTransferLoop();
	}
	
	private void checkExit(String input){
		if (input.equalsIgnoreCase("exit")){
			exit = true;
			backing.closeConnection();
		}
	}
	
	private void displayNotAuthenticated(){
		System.out.println("Not athenticated please try again");
	}
	
	private void authenticateLoop(){
		while (!authenticated && !exit){
			System.out.print("Please Enter your ID: ");
			String input = scanner.next();
			System.out.println();
			
			checkExit(input);
			if (exit) return;
			try {
				inputID = Integer.parseInt(input);
			} catch (Exception e){
				displayNotAuthenticated();
				continue;
			}
			
			
			authenticated = backing.Authenticate(inputID);
			if (!authenticated){
				displayNotAuthenticated();
			}
		}
	}
	
	private void fileTransferLoop(){
		while (authenticated && !exit) {
			System.out.println("Enter a filename to transfer");
			String filename = "";
			do{
				filename = scanner.nextLine();
			}while (filename.length() == 0);
			
			checkExit(filename);
			if (exit)return;
			try {
				backing.requestFile(inputID, filename);
			} catch (Exception e){
				System.out.println("Error getting file");
				continue;
			}
		}
	}
	
	
}
