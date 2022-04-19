/**
 * ServerHandler.java
 * 
 * @version 4.17.22
 * @author  Justin Jahlas, 
 * 			Brennan Luttrel, 
 * 			Prakash Lingden, 
 * 			Cole Blunt, 
 * 			Noah Meyers
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import surroundpack.Surround4Game;

/********************************************************************
 * Dispatched class to handle each client's communication with the
 * server. receives requests from clients and broadcasts to clients
 *******************************************************************/
public class ServerHandler extends Thread {
	
	private Socket connectionSocket;
	protected static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
	
	// list of all players currently connected and in lobby/game
	protected static Vector<PlayerInfo> currentPlayers = new Vector<PlayerInfo>(0);

	protected static Vector handlers = new Vector<>();

	// track current valid player id
	protected static int playerIDGen;

	protected static int boardSize;
	protected static int numPlayers;

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
	private boolean isHost;

	// there is already a host
	public static Boolean hosting;

	// total number of players in lobby
	protected static int maxPlayers;

	// current player's turn
	protected static int currentPlayer;
	protected static Surround4Game gameInstance;

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
		isHost = false;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		try {
		    while(running) {
		        if (welcome) {
		            connectUser(inFromClient.readUTF());
					handlers.addElement(this);
		        }
		        else {
		            waitForRequest();
		        }
		    }
		}
		catch (Exception e) {
			printDate();
			System.out.println("User lost connection " + clientAddress.toString());

			if (isHost) {
				isHost = false;
				hosting = null;
				removePlayer(p);
			}

			removePlayer(p);
			System.out.println(" ");
        }
		finally {
			handlers.removeElement(this);

			try {
				outToClient.close();
				inFromClient.close();
				connectionSocket.close();
			}
			catch (IOException e) {
				System.out.println("No socket to close");
			}

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
		// System.out.print("Assigning playerID " + myPlayerID);
		p = new PlayerInfo(hostName, port, userName, myPlayerID);

		inFromClient = new DataInputStream(new BufferedInputStream(this.connectionSocket.getInputStream()));
		outToClient = new DataOutputStream(this.connectionSocket.getOutputStream());
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
	 * @param clientCommand Command provided by user 
	 * 
	 * @throws Exception
	 ***************************************************************/
	private void processRequest(String clientCommand) throws Exception {

		Socket dataSocket;
		String firstLine;
		printDate();
		System.out.print("[" + userName + "] sent command: " + clientCommand);
		StringTokenizer tokens = new StringTokenizer(clientCommand);

		firstLine = tokens.nextToken();

		try {
			port = Integer.parseInt(firstLine);
		}
		catch (Exception e) {
			printDate();
			System.out.println("\nERROR Could not parse \'" + port + "\' as int");
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
			dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

			try {
				numPlayers = Integer.parseInt(tokens.nextToken());
				boardSize = Integer.parseInt(tokens.nextToken());
				
				maxPlayers = numPlayers;
			}
			catch (NumberFormatException e) {
				printDate();
				System.out.println("\nERROR Unable to convert numPlayers or boardSize to int");
				return;
			}

			if (!hosting) {
				isHost = true;
				hosting = true;

				p.setPlayerNumber(0);
				addPlayer(p);
				dataOutToClient.writeUTF("SUCCESS " + p.getPlayerNumber());

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
					dataOutToClient.writeUTF("SUCCESS " + maxPlayers + " "  + boardSize + " " + p.getPlayerNumber());
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
			if (clientCommand.equals("end-host") && isHost) {
				hosting = false;
			}

			dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

			boolean rmSuccess = false;

			for (PlayerInfo p: currentPlayers) {
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

		// Client host starts game
		// broadcast to all clients to go to game panel and deliver first player
		else if (clientCommand.equals("start-game")) {
			dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

			// send message to all clients and tell player 0 goes first
			if (isHost) {
				int[] rmnPlayers = new int[currentPlayers.size()];
				for (int i = 0; i < currentPlayers.size(); i++) {
					rmnPlayers[i] = currentPlayers.get(i).getPlayerNumber();
				}
				// create board on server side
				gameInstance = new Surround4Game(boardSize, currentPlayer, rmnPlayers);
				dataOutToClient.writeUTF("SUCCESS");
				broadcast("start-game");
				broadcast("start " + (currentPlayer = 0));
			}
			else {
				dataOutToClient.writeUTF("INVALID_HOST");
				System.out.print(" DENIED");
			}

			
			dataOutToClient.close();
			dataSocket.close();
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

			if (isHost) {
				isHost = false;
				hosting = false;
			}

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

			System.out.println("");
			printDate();
			System.out.print("\nUser disconnected " + userName + " " + clientAddress.getHostAddress());
		}
		
		else if (clientCommand.equals("set-tile")) {

			dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			dataOutToClient= new DataOutputStream(dataSocket.getOutputStream());

			// 'set-tile x y'
			int row = Integer.parseInt(tokens.nextToken());
			int col = Integer.parseInt(tokens.nextToken());

			if (gameInstance.select(row, col)) {
				dataOutToClient.writeUTF("SUCCESS");

				int winner = gameInstance.getWinner();

				if (winner > -1) {
					// winner
					// broken here. something wrong in Surround4Game logic
					printDate();
					System.out.println("[" + currentPlayer + "] won the game");
					broadcast(currentPlayer + " " + row + " " + col + " " + winner + " winner");
				}
				else if (winner == -1) {
					// no winner
					
					broadcast(currentPlayer + " " + row + " " + col + " " + (currentPlayer = gameInstance.nextPlayer()));
				}
				else {
					printDate();
					System.out.println("[" + currentPlayer + "] won the game");
					broadcast(currentPlayer + " " + row + " " + col + " " + (currentPlayer = 0) + " winner");
					gameInstance.reset();
					// tie
				}
			}
			else {
				dataOutToClient.writeUTF("INVALID_MOVE");
			}

			dataOutToClient.close();
			dataSocket.close();
		}

		System.out.println("");

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

	/****************************************************************
	 * Send a message to all connected clients via control socket IO 
	 * stream
	 * 
	 * @param message message to give to all clients
	 ***************************************************************/
	private static void broadcast(String message) {
		synchronized(handlers) {
			Enumeration e = handlers.elements();

			while (e.hasMoreElements()) {
				ServerHandler sh = (ServerHandler) e.nextElement();
				
				try {
					sh.outToClient.flush();
					sh.outToClient.writeUTF(message);
				}
				catch (Exception er) {
					System.err.println("\nBroadcast failure\n");
					er.printStackTrace();
				}
			}
		}
	}

	/**
	 * Print the date and time in format [YYYY-MM-DD HH:MM:SS UTF]
	 */
	private void printDate() {
		Date date = new Date(System.currentTimeMillis());
		System.out.print("[" + formatter.format(date) + "] ");
	}

}	
