import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

public class ServerHandler extends Thread {
	
	private Socket connectionSocket;

	protected static Vector<PlayerInfo> currentPlayers = new Vector<PlayerInfo>();
	
	private int port;
	private InetAddress clientAddress;

	private DataOutputStream outToClient;
	private DataInputStream inFromClient;

	private DataOutputStream dataOutToClient;
	
	private boolean welcome;
	private boolean running;

	// info receieved from host
	private int maxPlayers;

	// info maintained in server
	private int currentPlayer;	//  <------------------------------------------ potential error HERE

	/****************************************************************
	 * Constructor, Initialize IO streams, set initial vars, 
	 * create control connection
	 * 
	 * @param connectionSocket
	 * @throws Exception
	 ****************************************************************/
	public ServerHandler(Socket connectionSocket) throws Exception {
		this.connectionSocket = connectionSocket;
		outToClient = new DataOutputStream(this.connectionSocket.getOutputStream());
		inFromClient = new DataInputStream(this.connectionSocket.getInputStream());

		welcome = true;
		running = true;
		System.out.println("Connection created " + connectionSocket.getInetAddress() + " on port " + 
			connectionSocket.getLocalPort() + " to port " + connectionSocket.getPort());
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
	
	/****************************************************************
	 * Opens the Control socket between a client and server
	 * Receives data from user, adds them as a current player, and 
	 * initializes input control stream from client
	 * 
	 * @param userInfo User info of format: "hostName port"
	 * @throws Exception
	 ***************************************************************/
	private void connectUser(String userInfo) throws Exception {
		
		welcome = false;
		StringTokenizer tokenizer = new StringTokenizer(userInfo);
		 
		String hostName = tokenizer.nextToken();
		int port = Integer.parseInt(tokenizer.nextToken());
		String userName = tokenizer.nextToken();

		PlayerInfo p = new PlayerInfo(hostName, port, userName);
		addPlayer(p);

		System.out.println("Data receieved: " + hostName + " " + port + " " + userName);
		inFromClient = new DataInputStream(new BufferedInputStream(this.connectionSocket.getInputStream()));

		System.out.println("Done connecting");
	}
	
	/****************************************************************
	 * Awaits a request from the user. Halts when none in control
	 * stream
	 * 
	 * Goes to processRequest upon receiving command
	 * 
	 * @throws Exception
	 ***************************************************************/
	private void waitForRequest() throws Exception {
	//TODO
		String fromClient = inFromClient.readUTF();
		processRequest(fromClient);
	}

	/****************************************************************
	 * Processes a command provided by user.
	 * All commands are preceeded with data port to send response 
	 * back to.
	 * 
	 * @param clientCommand Command provided by user Accepted 
	 * commands include: 
	 * 	"place x y" - request to place a tile at coordinates
	 * 	"newgame" - request to restart board and start new game
	 * 	"disconnect" - request to disconnect from server
	 * 
	 * @throws Exception
	 ***************************************************************/
	private void processRequest(String clientCommand) throws Exception {
		String firstLine;
		System.out.println("Processing " + clientCommand + " from client");
		StringTokenizer tokens = new StringTokenizer(clientCommand);

		firstLine = tokens.nextToken();
		port = Integer.parseInt(firstLine);
		clientCommand = tokens.nextToken();

		if (clientCommand.equals("place")) {
			// port command x y
			int x, y;

			Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

			try {
				x = Integer.parseInt(tokens.nextToken());
				y = Integer.parseInt(tokens.nextToken());
			}
			catch (Exception e) {
				System.err.println("\nReceived invalid x or y coord x\n");
				e.printStackTrace();
				dataOutToClient.writeUTF("INVALID_COORD");
				return;
			}

		}
		else if (clientCommand.equals("newgame")) {

		}
		else if (clientCommand.equals("disconnect")) {

		}
	}

	/****************************************************************
	 * Add a player to the master list of currently connected players
	 * 
	 * @param newPlayer PlayerInfo Player to add
	 ***************************************************************/
	private void addPlayer(PlayerInfo newPlayer) {
		synchronized (currentPlayers) {
			currentPlayers.add(newPlayer);
		}
	}

	// FIXME: potential issue here. Threads may not share data with eachother (p1 instance of this serverhandler
	// doesnt have same next player and p2)

	/****************************************************************
	 * Set current player to next valid player. Reset turn once 
	 * all players have gone
	 ***************************************************************/
	private void nextPlayer() {
		currentPlayer = currentPlayer < maxPlayers ? currentPlayer + 1 : 0;
	}

	/****************************************************************
	 * Set current player to player specified.
	 * 
	 * @param currentPlayer player number to set current player to
	 ***************************************************************/
	private void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
}	
