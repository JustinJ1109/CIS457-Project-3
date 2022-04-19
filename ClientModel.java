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

import javax.swing.JButton;
import surroundpack.BoardPiece;
import java.awt.event.*;

/********************************************************************
 * Main Client class responsible for communicating with the server,
 * relaying info to GUI, and handling updates on client side.
 *******************************************************************/
public class ClientModel {

	private final int controlPort = 1370;
	private int port;

	protected static boolean renderingOpponent;

	private boolean isHosting;
	private boolean connectedToServer;

	protected static boolean inGame;
	protected static boolean myTurn;
	protected static int playerNumber;
	protected static int currentPlayer;
	protected static int[] selectedCoords = {-1, -1};
	protected static boolean selectedTile;

	private String serverHostIP, userName;
	private Socket ControlSocket;

	private DataOutputStream toServer;
	private DataInputStream inFromServer;

	private ButtonListener boardListener;

	private GUI gui;

	/****************************************************************
	 * Initialize GUI button listeners
	 * 
	 * @param gui client gui class
	 ***************************************************************/
	public ClientModel(GUI gui) {
		this.gui = gui;
		boardListener = new ButtonListener();
		gui.getHostStartButton().addActionListener(e -> hostGame());
		gui.getJoinStartButton().addActionListener(e -> joinLobby());
		gui.getLobbyBackButton().addActionListener(e -> leaveLobby());
        gui.getMenuQuitButton().addActionListener(e -> quitGame());
		gui.getRefreshButton().addActionListener(e -> updatePlayerList());
		
		gui.getLobbyPlayButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame();
				if (isHosting)
					new Thread(() -> play()).start();
					System.out.println("HOST THREAD CLOSED");
			}
		});		
	}

    /****************************************************************
	 * establish connection with server 
	 * 
	 * Open IO control sockets and send to server
	 * @param hc char character of values 'h' or 'c' - whether the user
	 * calls as host or client
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
	 * Join lobby in session and dispatch server listener thread
	 * until server starts game
	 * 
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

						System.out.println("CLOSING JOINTHREAD");
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
	 * Tell server client is leaving the lobby. Calls 
	 * disconnectFromServer()
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
				inFromServer.readUTF(); // eat broadcast
				//TODO: set currentPlayer
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

	/****************************************************************
	 * Non-hosts sit here until host clicks start game
	 * 
	 * ONLY NON-HOST CALLS THIS
	 * 
	 * @return true when client receives message from server
	 * saying that game has started
	 * @throws Exception
	 ***************************************************************/
	private boolean waitForGameStart() throws Exception {
		String fromServer = inFromServer.readUTF();

		StringTokenizer tokenizer = new StringTokenizer(fromServer);
		if (tokenizer.nextToken().equals("start-game")) {
			//TODO: setCurrentPlayer
			return true;
		}
		return false;
	}

	/****************************************************************	 
	 * Once host starts game, all clients are send here
	 * 
	 * Dispatches a thread to listen to server for game state updates
	 * as well as tell server updates
	 * 
	 * GOTO waitForUpdate()
	 ***************************************************************/
	private void play() {
		gui.swapPanel("game");

		// listen for updates from server
		new Thread(new Runnable() {
			@Override
			public void run() {
				waitForUpdate();
				
			}
		}).start();
		System.out.println("GAME THEAD ENDED");
	}

	/****************************************************************
	 * Wait for server message and then send to processUpdate()
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
				// System.out.println("Awaiting info from server");
				String fromServer = inFromServer.readUTF();

				System.out.println("receieved " + fromServer);

				processUpdate(fromServer);
			}
			catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}

	/****************************************************************
	 * Process the update received from the server. 
	 * 
	 * @param serverCommand command and args received by server
	 ***************************************************************/
	private void processUpdate(String serverCommand) {
		//TODO: 
		// receive data in format: 'playerNum row col nextPlayerNum'
								// 'winner winningPlayerNum'
								// 'start firstPlayerNum'
								
								// 'reset game'
		// update gui accordinly and update current player turn to nextPlayerNum		

		int row, col, playerThatWent;
		StringTokenizer tokens = new StringTokenizer(serverCommand);
		String firstTok = tokens.nextToken();
		if (firstTok.equals("reset")) {
			// do reset game code here
			return;
		}
		else if (firstTok.equals("start")) {
			currentPlayer = Integer.parseInt(tokens.nextToken());
			if (currentPlayer == playerNumber) {
				System.out.println("My turn");
				// my turn, allow modifying
				setBoardClickable(true);
				myTurn = true;
			}
			return;
		}
		// receiving player move data
		else {
			playerThatWent = Integer.parseInt(firstTok);
			row = Integer.parseInt(tokens.nextToken());
			col = Integer.parseInt(tokens.nextToken());
			currentPlayer = Integer.parseInt(tokens.nextToken());
		}

		renderingOpponent = true;
		gui.getGamePanel().setTile(row, col, playerThatWent);

		try {
			if (tokens.nextToken().equals("winner")) {
				
				gui.generateDialog("Player " + playerThatWent + " won!", "Game Over");
				disconnectFromServer();
				gui.swapPanel("menu");
				gui.getGamePanel().resetBoard();
				
				return;
			}
		}
		catch (Exception e) {
			System.out.println("NO WINNER");
		}

		if (currentPlayer == playerNumber) {
			System.out.println("My turn");
			// my turn, allow modifying
			setBoardClickable(true);
			myTurn = true;
		}
			// other turn was updated
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

	/****************************************************************
	 * Listen for game board button presses
	 * 
	 * On press, get the coordinates of the button
	 * set tile as pressed
	 * 
	 * end turn, disable button actions
	 ***************************************************************/
	private class ButtonListener implements ActionListener {
		@Override
        public void actionPerformed(ActionEvent e) {
			System.out.println("Clicked");
			BoardPiece clicked = (BoardPiece) e.getSource();
			selectedCoords[0] = clicked.getYVal();
			selectedCoords[1] = clicked.getXVal();

			// tell server where you went
			try {
				String command = "set-tile";
				port += 2;
	
				ServerSocket welcomeData = new ServerSocket(port);
				String dataToServer = port + " " + command + " " + selectedCoords[0] + " " + selectedCoords[1];
	
				toServer.writeUTF(dataToServer);
				System.out.println("Sending \'" + dataToServer + "\' to server");
	
				Socket dataSocket = welcomeData.accept();
				DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
				String response = inData.readUTF();
				if (response.equals("SUCCESS")) {
					inGame = true;
					renderingOpponent = false;
					gui.getGamePanel().setTile(selectedCoords[0], selectedCoords[1], playerNumber);
					selectedTile = true;

					setBoardClickable(false);

					//TODO: set currentPlayer
				}
				else if (response.equals("INVALID_MOVE")) {
					gui.generateDialog("Invalid Move", "Invalid Move");
					System.out.println("Could not move at specified location");
				}
				
				inData.close();
				dataSocket.close();
				welcomeData.close();
			}
			catch (Exception er) {
				gui.generateDialog("Something went wrong", "idk");
				er.printStackTrace();
			}
		}
	}

	/****************************************************************
	 * Add/Remove button listeners from board
	 * 
	 * @param yes true if adding listeners, false if removing
	 ***************************************************************/
	private void setBoardClickable(boolean yes) {

		for (int i = 0; i < gui.getGamePanel().getBoard().length; i++) {
			for (int j = 0; j < gui.getGamePanel().getBoard().length; j++) {
				JButton button = gui.getGamePanel().getBoard()[i][j];
				if (yes) {
					button.addActionListener(boardListener);
				}
				else {
					button.removeActionListener(button.getActionListeners()[0]);
				}
			}
		}
	}

	public static void main(String[] args) {
		GUI gui = new GUI("Test");
		ClientModel cm = new ClientModel(gui);
	}

}   
