package Client;


public class Main
{
	public static void main(String[] args)
	{
		try{
			UserInterface ui = new UserInterface();
			ui.run();
			System.out.println("exiting goodbye");
		} catch(Exception e) {
			System.out.println("Could not connect to server! (Is it running?)");
			return;
		}
		

	}

}
