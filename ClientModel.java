import java.io.*; 
import java.net.*;
import java.util.*;

public class ClientModel {

	private final int controlPort = 1370;
	private int port;

	private boolean isHosting;
	private boolean connectedToServer;
	private boolean inGame;

	private String serverHostIP, userName;
	private Socket ControlSocket;

	private DataOutputStream toServer;
	private DataInputStream inFromServer;

	private GUI gui;

	public ClientModel(GUI gui) {
		this.gui = gui;
		gui.getHostStartButton().addActionListener(e -> hostGame());
		gui.getJoinStartButton().addActionListener(e -> joinLobby());
		gui.getLobbyBackButton().addActionListener(e -> leaveLobby());
        gui.getMenuQuitButton().addActionListener(e -> quitGame());
		gui.getRefreshButton().addActionListener(e -> updatePlayerList());
		gui.getLobbyPlayButton().addActionListener(e -> play());
		connectedToServer = false;
	}

	private boolean verifyConnectionInputs() {
		serverHostIP = gui.getServerHostIPField().getText();

        // Check that is IP format
        if (!serverHostIP.matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
            System.out.println("Invalid IP");
            return false;
        }

		userName = gui.getUserNameField().getText();
		if (userName.equals("") || userName.contains(" ")) {
			System.out.println("Invalid username, cannot contain spaces and cannot be empty");		
			return false;
		}
		return true;
	}

    /** establish connection with server */
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
				System.out.println("Unable to connect to host: " + serverHostIP + " on port " + controlPort);
				e.printStackTrace();
				return false;
			}

		}
		gui.swapPanel(hc == 'h' ? "host" : "join");

		return true;
    }

	/** 
	 * Join lobby in session
	 * 
	 * wait for server to broadcast start-game
	 */
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

			if (response.equals("SUCCESS")) {
				gui.rmStartButtonFromNonHost();
				gui.swapPanel("lobby");
				
				// await for server response in subthread
				// lets user still interact with GUI, doesn't freeze
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							// while(!inGame && connectedToServer)
							waitForUpdate();
						}
						catch (Exception e) {
							
							e.printStackTrace();
						}

						System.out.println("CLOSING SUBTHREAD");
					}
				}).start();
							
			}
			else if (response.equals("LOBBY_LIMIT_REACHED")) {
				System.out.println("Max players exceeded. Cannot join");
			}
			else if (response.equals("USERNAME_IN_USE")) {
				System.out.println("Username in use. try another");
				// disconnect user from server and let 
				// them reconnect with diff uName
				dc = true;
			}
			else if (response.equals("NO_HOST_AVAILABLE")) {
				System.out.println("No one currently hosting");
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

	// call server to get updated playerList
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

	/** establish connection with server, tell it to make a new game, connect client to that game */
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
			String numPlayers = gui.getNumPlayersBox().getSelectedItem().toString();

			ServerSocket welcomeData = new ServerSocket(port);
			String dataToServer = port + " " + command + " " + numPlayers + " " + boardSize;

			toServer.writeUTF(dataToServer);
			System.out.println("Sending \'" + dataToServer + "\' to server");

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			String response = inData.readUTF();
			if (response.equals("SUCCESS")) {
				isHosting = true;
				gui.swapPanel("lobby");
			}
			else {
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

	/** 
	 * Tell server to remove self from player list, if hosting
	 * Stop hosting, swap panel to menu, disconnect from server
	 * 
	 */
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

	/** 
	 * Client host starts game
	 * 
	 * Send message to server to start game
	 */
	public void play() {
		try {
			String command = "play";
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

				// while(inGame) {
				// 	waitForUpdate();
				// }
			}
			else if (response.equals("INVALID_HOST")) {
				System.out.println("Must be host to start the game");
			}
			else {
				System.out.println("Could not start game\nError code from server: " + response);
			}
			inData.close();
			dataSocket.close();
			welcomeData.close();
		}
		catch (Exception e) {
			System.out.println("Could not connect to server");
			disconnectFromServer();
			e.printStackTrace();
		}
	}

	private void waitForUpdate() throws Exception {
		System.out.println("Awaiting broadcast...");
		String fromServer = inFromServer.readUTF();
		System.out.println("processing BROADCAST: " + fromServer);
		processUpdate(fromServer);
	}

	private void processUpdate(String serverCommand) {
		String firstLine;
		StringTokenizer tokens = new StringTokenizer(serverCommand);

		firstLine = tokens.nextToken();

		if (firstLine.equals("start-game")) {
			gui.swapPanel("game");
		}
	}

	/****************************************************************
	 * Send a request to the server to place a tile at 
	 * coordinates X Y
	 * 
	 * Open a data port, send new port and command to server, 
	 * and wait for server to send a response
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
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

    /** close all IO streams and sockets, disconnect from server */
    public void disconnectFromServer() {
		try {
			String sentence = "disconnect";
			port += 2; 
			ServerSocket welcomeData = new ServerSocket(port);

			toServer.writeUTF(port + " " + sentence);

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			try {
				String file = inData.readUTF();
				System.out.println("Disconnect from server: " + file);
				
			}
			catch (Exception e) {
				System.out.println("Could not close server");
				e.printStackTrace();
			}
			connectedToServer = false;
			inData.close();
			toServer.close();
			dataSocket.close();
			welcomeData.close();
			ControlSocket.close();
		}
		catch (Exception e) {
			
		}
 	}
	
	 public void quitGame() {
		disconnectFromServer();
		System.exit(0);
	}

    public void setUserName(String name) {
        userName = name;
    }

	public void setServerHostIP(String ip) {
		serverHostIP = ip;
	}

	public void setServerPort(int port) {
		this.port = port;
	}

	public static void main(String[] args) {
		GUI gui = new GUI("Test");
		ClientModel cm = new ClientModel(gui);
	}

}   
