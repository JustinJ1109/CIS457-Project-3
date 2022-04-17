import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;

public class ServerHandler extends Thread {
	
	private Socket connectionSocket;
	protected static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	
	// list of all players currently connected and in lobby/game
	protected static Vector<PlayerInfo> currentPlayers = new Vector<PlayerInfo>(0);

	// track current valid player id
	protected static int playerIDGen;

	// Thread-Specific
	private int myPlayerID;
	private int port;
	private InetAddress clientAddress;
	private String userName;

	private DataOutputStream outToClient;
	private DataInputStream inFromClient;
	private DataOutputStream dataOutToClient;

	private boolean welcome;
	private boolean running;

	private PlayerInfo p;

	// this thread is the host
	private boolean ishost;

	// there is already a host
	public static Boolean hosting;

	// total number of players in lobby
	protected static int maxPlayers;

	// current player's turn
	protected static int currentPlayer;

	//TODO: if host leaves, make new player host
	//TODO: disconnect player when at menu

	// FIXME: playerID not unique, when player is hosting and another tries to join
	// it deletes the host from list of players, adds the attempting player, but doesnt let
	// player join, host cannot leave

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

		// only initialze once, if not here, each new thread will reset this
		if (hosting == null) {
			hosting = false;
		}
		ishost = false;
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
			System.out.println("User lost connection " + clientAddress.toString());

			removePlayer(p);
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
		this.userName = userName;

		myPlayerID = playerIDGen;
		playerIDGen++;
		System.out.print("Assigning playerID " + myPlayerID);
		p = new PlayerInfo(hostName, port, userName, myPlayerID);

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
				maxPlayers = numPlayers;
			}
			catch (NumberFormatException e) {
				System.out.println("\tUnable to convert numPlayers or boardSize to int");
				return;
			}

			if (!hosting) {
				ishost = true;
				hosting = true;

				p.setPlayerNumber(0);
				addPlayer(p);
				dataOutToClient.writeUTF("SUCCESS");

			}
			else {
				dataOutToClient.writeUTF("ERROR_HOST_IN_SESSION");
			}
			dataOutToClient.close();
			dataSocket.close();
		}
		
		else if (clientCommand.equals("join")) {
			dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

			boolean jump = false;
			for (PlayerInfo pl: currentPlayers) {
				if (pl.getUserName().equals(userName)) {
					dataOutToClient.writeUTF("USERNAME_IN_USE");
					jump = true;
				}
			}

			if (!jump) {
				if (currentPlayers.size() == 0) {
					dataOutToClient.writeUTF("NO_HOST_AVAILABLE");
				}
				if (currentPlayers.size() < maxPlayers) {
					p.setPlayerNumber(currentPlayers.size());
					addPlayer(p);
					dataOutToClient.writeUTF("SUCCESS");
				}
				else {
					dataOutToClient.writeUTF("LOBBY_LIMIT_REACHED");
				}
			}

			dataOutToClient.close();
			dataSocket.close();
		}

		// player left lobby
		else if (clientCommand.equals("leave") || clientCommand.equals("end-host")) {
			//remove player from list
			if (clientCommand.equals("end-host") && ishost) {
				hosting = false;
			}

			dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

			boolean rmSuccess = false;
			System.out.println("looking for " + clientAddress.getHostAddress());

			for (PlayerInfo p: currentPlayers) {
				System.out.println("comparing to " + p.getIP());
				if (p.getPlayerID() == myPlayerID) {
					removePlayer(p);
					rmSuccess = true;
					break;
				}
			}

			if (rmSuccess) {
				dataOutToClient.writeUTF("SUCCESS");
			}
			else {
				dataOutToClient.writeUTF("USER_NOT_FOUND");
			}

			dataOutToClient.close();
			dataSocket.close();
		}

		else if (clientCommand.equals("start-game")) {

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
			dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

			dataOutToClient.writeUTF("SUCCESS");

			for (PlayerInfo pl: currentPlayers) {
				if (pl.getPlayerID() == myPlayerID) {
					removePlayer(pl);
					break;
				}
			}

			dataOutToClient.close();
			dataSocket.close();
			inFromClient.close();
			outToClient.close();
			connectionSocket.close();
			running = false;

			printDate();
			System.out.println("User disconnected " + clientAddress.getHostAddress());
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

	private void removePlayer(PlayerInfo player) {
		synchronized (currentPlayers) {
			try {
			currentPlayers.remove(player);
			}
			catch(Exception e) {
			}
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
