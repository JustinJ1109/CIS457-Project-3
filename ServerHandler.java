import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.Vector;

public class ServerHandler extends Thread {
	
	private Socket connectionSocket;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");


	protected static Vector<PlayerInfo> currentPlayers = new Vector<PlayerInfo>();
	
	private int port;
	private InetAddress clientAddress;

	private DataOutputStream outToClient;
	private DataInputStream inFromClient;

	private DataOutputStream dataOutToClient;
	
	private boolean welcome;
	private boolean running;
	private boolean hosting;

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
		clientAddress = connectionSocket.getInetAddress();
	}
	
	public void run() {
		try {
		    while(running) {
		        if (welcome) {
		            connectUser(inFromClient.readUTF());
		        }
		        else {
		            waitForRequest();
		        }       
		    }

		} 
		catch (Exception e) {
			printDate();
			System.out.println("User disconnected " + clientAddress.toString());
		    // e.printStackTrace();
        }
	}
	
	/****************************************************************
	 * Opens the Control socket between a client and server
	 * Receives data from user, adds them as a current player, and 
	 * initializes input control stream from client
	 * 
	 * @param userInfo User info of format: "hostName port userName"
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
		inFromClient = new DataInputStream(new BufferedInputStream(this.connectionSocket.getInputStream()));

		printDate();
		System.out.println("User " + hostName + ":" + port + " connected");
		
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

		Socket dataSocket;
		String firstLine;
		printDate();
		System.out.println("Processing " + clientCommand + " from client");
		StringTokenizer tokens = new StringTokenizer(clientCommand);

		firstLine = tokens.nextToken();

		try {
			port = Integer.parseInt(firstLine);
		}
		catch (Exception e) {
			System.out.println("ERROR Could not parse " + port + " as int");
			return;
		}

		clientCommand = tokens.nextToken();

		if (clientCommand.equals("place")) {
			// port command x y
			int x, y;

			dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

			try {
				x = Integer.parseInt(tokens.nextToken());
				y = Integer.parseInt(tokens.nextToken());
			}
			catch (Exception e) {
				System.err.println("\tReceived invalid x or y coord x");
				dataOutToClient.writeUTF("INVALID_COORD");
				return;
			}

		}
		else if (clientCommand.equals("host")) {

			
			// do something with this
			int boardSize, numPlayers;

			dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

			try {
				numPlayers = Integer.parseInt(tokens.nextToken());
				boardSize = Integer.parseInt(tokens.nextToken());
			}
			catch (NumberFormatException e) {
				System.out.println("\tUnable to convert numPlayers or boardSize to int");
				return;
			}

			
			dataOutToClient.writeUTF("SUCCESS");
			

			dataOutToClient.close();
			dataSocket.close();


		}

		else if (clientCommand.equals("join")) {

		}

		// player left lobby
		else if (clientCommand.equals("leave") || clientCommand.equals("end-host")) {
			//remove player from list
			if (clientCommand.equals("end-host")) {
				hosting = false;
			}
		}

		else if (clientCommand.equals("start-game")) {
			hosting = true;

		}

		else if (clientCommand.equals("get-players")) {

			dataSocket = new Socket(connectionSocket.getInetAddress(), port);

			ObjectOutputStream ois = new ObjectOutputStream(dataSocket.getOutputStream());
			try {
				ois.writeObject(currentPlayers);
			}
			catch (Exception e) {
				System.err.println("Could not write current players to stream");
			}

			ois.close();
			dataSocket.close();
			
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

	private void printDate() {
		Date date = new Date(System.currentTimeMillis());
		System.out.print("[" + formatter.format(date) + "] ");
	}
}	
