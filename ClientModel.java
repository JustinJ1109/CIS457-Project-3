/**
 * ClientModel.java
 * 
 * @version 4.17.22
 * @author  Justin Jahlas, 
 * 			Brennan Luttrel, 
 * 			Munu Bhai, 
 * 			Cole Blunt, 
 * 			Noah Meyers
 */

import java.io.*; 
import java.net.*;
import java.util.*;
import java.awt.event.*;

/********************************************************************
 * Main Client class responsible for communicating with the server,
 * relaying info to GUI, and handling updates on client side.
 *******************************************************************/
public class ClientModel {

	private final int controlPort = 1370;
	private int port;


	private boolean isHosting;
	private boolean connectedToServer;

	protected static boolean inGame;
	protected static boolean myTurn;
	protected static int playerNumber;
	protected static int currentPlayer;

	private String serverHostIP, userName;
	private Socket ControlSocket;

	private DataOutputStream toServer;
	private DataInputStream inFromServer;

	private GUI gui;

	/****************************************************************
	 * Initialize GUI button listeners
	 * 
	 * @param gui client gui class
	 ***************************************************************/
	public ClientModel(GUI gui) {
		this.gui = gui;
		gui.getHostStartButton().addActionListener(e -> hostGame());
		gui.getJoinStartButton().addActionListener(e -> joinLobby());
		gui.getLobbyBackButton().addActionListener(e -> leaveLobby());
        gui.getMenuQuitButton().addActionListener(e -> quitGame());
		gui.getRefreshButton().addActionListener(e -> updatePlayerList());
		
		gui.getLobbyPlayButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame();
				new Thread(() -> play()).start();
			}
		});		
	}

    /****************************************************************
	 * establish connection with server 
	 * 
	 * Open IO control sockets and send to server
	 ***************************************************************/
    private boolean connectToServer(char hc) {
		if (!connectedToServer) {
			if (!verifyConnectionInputs()) {
				return false;
			}
			port = controlPort;

			// TODO: need error checking to make sure hostname and port are valid?
			try {
				System.out.println("Attemping to connect to server: " + serverHostIP + " at port " + controlPort);
				ControlSocket = new Socket(serverHostIP, controlPort);
				System.out.println("You are connected to " + serverHostIP);

				toServer = new DataOutputStream(ControlSocket.getOutputStream());
				inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));

				String dataToServer = serverHostIP + " " + port + " " + userName;

				toServer.writeUTF(dataToServer) ;
				System.out.println("Sent " + dataToServer + " to server");
				connectedToServer = true;			
				
			}
			catch (Exception e) {
				gui.generateDialog("Could not connect to host", "Error Connecting");
				System.out.println("Unable to connect to host: " + serverHostIP + " on port " + controlPort);
				e.printStackTrace();
				return false;
			}

		}
		gui.swapPanel(hc == 'h' ? "host" : "join");

		return true;
    }

		/****************************************************************
	 * Request server to host a new game. 
	 * 
	 * If there is already a host,
	 * will be be denied. Else it will establish a newly hosted game
	 * with server
	 * 
	 ****************************************************************/
    private void hostGame() {
		try {
			if (!connectToServer('h')) {
				return;
			}

			String command = "host";
			port += 2;
			String boardSizeDims = gui.getBoardSizeBox().getSelectedItem().toString();
			// convert 10x10 to for server side parsing
			int boardSize = Integer.parseInt(boardSizeDims.substring(0, boardSizeDims.indexOf("x")));
			int numPlayers = Integer.parseInt(gui.getNumPlayersBox().getSelectedItem().toString());

			ServerSocket welcomeData = new ServerSocket(port);
			String dataToServer = port + " " + command + " " + numPlayers + " " + boardSize;

			toServer.writeUTF(dataToServer);
			System.out.println("Sending \'" + dataToServer + "\' to server");

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			String response = inData.readUTF();

			StringTokenizer tokenizer = new StringTokenizer(response);
			String resCode = tokenizer.nextToken();
			if (resCode.equals("SUCCESS")) {
				isHosting = true;

				playerNumber = Integer.parseInt(tokenizer.nextToken());
				gui.getGamePanel().setBoardSize(boardSize);
				gui.getGamePanel().setPlayers(numPlayers);
				gui.getGamePanel().initBoard();

				gui.swapPanel("lobby");
			}
			else if (response.equals("ERROR_HOST_IN_SESSION")) {
				gui.generateDialog("Someone is already hosting a game", "Could not host game");
			}
			else {
				gui.generateDialog("Could not host game", "Could not host game");
				System.out.println("Could not host game\nError code from server: " + response);
			}
			inData.close();
			dataSocket.close();
			welcomeData.close();
		}
		catch (Exception e) {
			System.out.println("Could not close streams");
			disconnectFromServer();
			e.printStackTrace();
		}
	}

	/****************************************************************
	 * Join lobby in session
	 * set state to inGame once reaches
	 * 
	 * wait for server to broadcast start-game
	 ***************************************************************/
	public void joinLobby() {

		try {
			if (!connectToServer('c')) {
				return;
			}
			String command = "join";
			port += 2;

			ServerSocket welcomeData = new ServerSocket(port);
			String dataToServer = port + " " + command;

			toServer.writeUTF(dataToServer);
			System.out.println("Sending \'" + dataToServer + "\' to server");

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			String response = inData.readUTF();
			boolean dc = false;

			StringTokenizer tokenizer = new StringTokenizer(response);

			String resCode = tokenizer.nextToken();
			if (resCode.equals("SUCCESS")) {
				gui.getGamePanel().setPlayers(Integer.parseInt(tokenizer.nextToken()));
				gui.getGamePanel().setBoardSize(Integer.parseInt(tokenizer.nextToken()));
				playerNumber = Integer.parseInt(tokenizer.nextToken());
				gui.getGamePanel().initBoard();
				gui.swapPanel("lobby");
				
				// await for server response in subthread
				// lets user still interact with GUI, doesn't freeze
				Thread responseListener = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							if (!waitForGameStart()) {
								gui.generateDialog("Error Starting Game", "Error");
							}
							else {
								inGame = true;
								play();
							}
						}
						catch (Exception e) {
							
							e.printStackTrace();
						}

						System.out.println("CLOSING SUBTHREAD");
					}
				});
				responseListener.start();
			}
			else if (response.equals("LOBBY_LIMIT_REACHED")) {
				gui.generateDialog("Max players Exceeded", "Could not join");
				System.out.println(response);
			}
			else if (response.equals("USERNAME_IN_USE")) {
				gui.generateDialog("Username already in use", "Could not join");
				System.out.println(response);
				// disconnect user from server and let 
				// them reconnect with diff uName
				dc = true;
			}
			else if (response.equals("NO_HOST_AVAILABLE")) {
				gui.generateDialog("No games to join", "Could not join");
				System.out.println(response);
			}

			inData.close();
			dataSocket.close();
			welcomeData.close();
			if (dc) {
				disconnectFromServer();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/****************************************************************
	 * Tell server client is leaving the lobby.
	 * 
	 ***************************************************************/
	private void leaveLobby() {
		try {
			String command;
			if (isHosting) {
				command = "end-host";
			}
			else {
				command = "leave";
			}
			port += 2;

			ServerSocket welcomeData = new ServerSocket(port);
			String dataToServer = port + " " + command;

			toServer.writeUTF(dataToServer);
			System.out.println("Sending \'" + dataToServer + "\' to server");

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			String response = inData.readUTF();
			if (response.equals("SUCCESS")) {
				if (isHosting) {
					isHosting = false;
				}
				disconnectFromServer();
				gui.swapPanel("menu");
			}
			else {
				gui.generateDialog("Could not host game", "Could not host game");
				System.out.println("Could not end host game\nError code from server: " + response);
			}
			inData.close();
			dataSocket.close();
			welcomeData.close();
		}
		catch (Exception e) {
			System.out.println("Disconnected from server");
			disconnectFromServer();
			gui.swapPanel("menu");
			e.printStackTrace();
		}
	}

	/****************************************************************
	 * Tell server host started the game for broadcast to all others
	 * Set state to inGame
	 * 
	 * ONLY HOST CALLS THIS
	 * 
	 ***************************************************************/
	public void startGame() {
		try {
			String command = "start-game";
			port += 2;

			ServerSocket welcomeData = new ServerSocket(port);
			String dataToServer = port + " " + command;

			toServer.writeUTF(dataToServer);
			System.out.println("Sending \'" + dataToServer + "\' to server");

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			String response = inData.readUTF();
			if (response.equals("SUCCESS")) {
				inGame = true;
				
				currentPlayer = Integer.parseInt(inFromServer.readUTF().split(" ")[1]);
			}
			else if (response.equals("INVALID_HOST")) {
				gui.generateDialog("Must be a host to start the game", "Could not start game");
				System.out.println("Must be host to start the game");
			}
			else {
				gui.generateDialog("Could not start game", response);
				System.out.println("Could not start game\nError code from server: " + response);
			}
			inData.close();
			dataSocket.close();
			welcomeData.close();
		}
		catch (Exception e) {
			System.out.println("Could not connect to server");
			gui.generateDialog("Could not connect to server", "Error connecting");
			disconnectFromServer();
			e.printStackTrace();
		}
	}


	private void play() {
		System.out.println("Playing game");
		gui.swapPanel("game");

		// listen for updates
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Listening...");
				waitForUpdate();
				
			}
		}).start();

		while (inGame) {
			if (currentPlayer == playerNumber) {
				myTurn = true;
				System.out.println("My turn");

				//TODO:
				// enable board button listeners

				try {
					String command = "place";
					port += 2;
					//TODO:
					// get row/col from gui somehow
					int row = 0, col = 0;

					ServerSocket welcomeData = new ServerSocket(port);
					String dataToServer = port + " " + command + " " + row + " " + col;

					toServer.writeUTF(dataToServer);
					System.out.println("Sending \'" + dataToServer + "\' to server");

					Socket dataSocket = welcomeData.accept();
					DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
					String response = inData.readUTF();

				}
				catch (Exception e) {

				}

			}

			myTurn = false;
			//TODO: 
			// disable board button listeners
		}
		
	}


	private boolean waitForGameStart() throws Exception {
		String fromServer = inFromServer.readUTF();

		StringTokenizer tokenizer = new StringTokenizer(fromServer);
		if (tokenizer.nextToken().equals("start-game")) {
			currentPlayer = Integer.parseInt(tokenizer.nextToken());
			return true;
		}

		return false;
	}

	/****************************************************************
	 * Wait for server message
	 * 
	 * @throws Exception input stream was closed abruptly
	 * 
	 * 'playerNum move x y'
	 * 'playerNum lose'
	 * 'playerNum win'
	 ***************************************************************/
	private void waitForUpdate() {
		while(inGame) {
			try {
				System.out.println("Awaiting info from server");
				String fromServer = inFromServer.readUTF();
				StringTokenizer tokenizer = new StringTokenizer(fromServer);

				String command = tokenizer.nextToken();

				System.out.println("receieved " + command);

				processUpdate(command);
			}
			catch (Exception e) {

			}
		}

	}

	/****************************************************************
	 * Process the update received from the server. 
	 * 	valid codes expected include:
	 * 
	 * @param serverCommand command and args received by server
	 ***************************************************************/
	private void processUpdate(String serverCommand) {
		//TODO: 
		// receive data in format: 'playerNum row col nextPlayerNum'
		// update gui accordinly and update current player turn to nextPlayerNum
	}

	/****************************************************************
	 * Request server to send updated player list to update 
	 * lobbyTable
	 * 
	 ***************************************************************/
	@SuppressWarnings("unchecked")
	private void updatePlayerList() {
		System.out.println("refreshing...");

		try {

			String command = "get-players";
			port += 2;

			ServerSocket welcomeData = new ServerSocket(port);
			String dataToServer = port + " " + command;
			toServer.writeUTF(dataToServer);
			System.out.println("Writing \'" + dataToServer + "\' to server");

			Socket dataSocket = welcomeData.accept();
			ObjectInputStream ois = new ObjectInputStream(dataSocket.getInputStream());

			Vector<PlayerInfo> currentPlayers = (Vector<PlayerInfo>) ois.readObject();

			String[] playerUserNames = new String[currentPlayers.size()];
			int[] playerNums = new int[currentPlayers.size()];

			System.out.println("Receieved:");

			for (int i = 0; i < currentPlayers.size(); i++) {
				playerUserNames[i] = currentPlayers.get(i).getUserName();
				playerNums[i] = currentPlayers.get(i).getPlayerNumber();
				System.out.println("\t" + playerUserNames[i] + " " + playerNums[i]);
			}

			gui.updateLobbyTable(playerUserNames, playerNums);
			
			ois.close();
			dataSocket.close();
			welcomeData.close();
		}
		catch (Exception e) {
			System.err.println("Could not get player list");
			e.printStackTrace();
		}
	}

	/****************************************************************
	 * NOT IN USE YET
	 ***************************************************************/
	public void placeTile(int x, int y) {
		String command = "place";
		port += 2;

		try {
			ServerSocket welcomeData = new ServerSocket(port);
			toServer.writeUTF(port + " " + command + " " + x + " " + y);

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

			System.out.println("Received " + inData.readUTF());
			
			inData.close();
			dataSocket.close();
			welcomeData.close();
		}
		catch (Exception e) {
			System.err.println("Could not write command");
			e.printStackTrace();
		}
	}

    /****************************************************************
	 * Tell server client is disconnecting. Close all sockets and 
	 * IO streams
	 * 
	 ***************************************************************/
    public void disconnectFromServer() {
		try {
			String sentence = "disconnect";
			port += 2; 
			ServerSocket welcomeData = new ServerSocket(port);

			toServer.writeUTF(port + " " + sentence);

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			
			connectedToServer = false;
			inGame = false;
			isHosting = false;

			inData.close();
			toServer.close();
			dataSocket.close();
			welcomeData.close();
			ControlSocket.close();
		}
		catch (Exception e) {
			
		}
 	}
	
	 /***************************************************************
	  * User presses close button

	  **************************************************************/
	 public void quitGame() {
		disconnectFromServer();
		System.exit(0);
	}

	/****************************************************************
	 * Check that IP and username is valid before allowing
	 * 
	 * @return true if valid, false otherwise
	 ***************************************************************/
	private boolean verifyConnectionInputs() {
		serverHostIP = gui.getServerHostIPField().getText();

        // Check that is IP format
        if (!serverHostIP.matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
			gui.generateDialog("Invalid IP", "Invalid IP");
            System.out.println("Invalid IP");
            return false;
        }

		// username cannot contain spaces or be empty
		userName = gui.getUserNameField().getText();
		if (userName.equals("") || userName.contains(" ")) {
			gui.generateDialog("Invalid Username, try one without spaces", "Invalid Username");
			System.out.println("Invalid username, cannot contain spaces and cannot be empty");		
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		GUI gui = new GUI("Test");
		ClientModel cm = new ClientModel(gui);
	}

}   
