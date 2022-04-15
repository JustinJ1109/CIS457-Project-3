import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;

public class GameServer extends Thread {
	private Socket connectionSocket;
	
	int port;
	InetAddress clientName;
	private static DataOutputStream outToClient;
	private DataInputStream inFromClient;
	
	boolean welcome;
	private boolean running;
	
	public GameServer(Socket connectionSocket) throws Exception {
		this.connectionSocket = connectionSocket;
		outToClient = new DataOutputStream(this.connectionSocket.getOutputStream());
		inFromClient = new DataInputStream(this.connectionSocket.getInputStream());

		welcome = true;
		running = true;
		System.out.println("Connection created " + connectionSocket.getInetAddress() + " on 		port " + connectionSocket.getLocalPort() + " to port " + connectionSocket.getPort());
	}
	
	public void run() {
		try {
		    while(running) {
		        if (welcome) {
		            System.out.println("welcoming user");
		            connectUser(inFromClient.readUTF());
		        }
		        else {
		            System.out.println("processing request");
		            waitForRequest();
		        }       
		    }
		} 
		catch (Exception e) {
		    e.printStackTrace();
        	}
	}
	
	private void connectUser(String userInfo) throws Exception {
		welcome = false;
		// get username, host, and speed from user stream
		// add user to list of users and get their files
		StringTokenizer tokenizer = new StringTokenizer(userInfo);

		String hostName = tokenizer.nextToken();
		int port = Integer.parseInt(tokenizer.nextToken());
		String userName = tokenizer.nextToken();
		String speed = tokenizer.nextToken();

		//UserData user = new UserData(userInfo, hostName, speed);

		System.out.println("Data receieved: " + hostName + " " + port + " " + userName + " " + speed);
		//addUser(user);

		inFromClient = new DataInputStream(new BufferedInputStream(this.connectionSocket.getInputStream()));

		//File file = getFile();
		//files = parseData(file, user);
		//addContent(files);

		System.out.println("Done connecting");
	}
	
	//wait for input from user for game
	private void waitForRequest(){
	//TODO
	}
}	
