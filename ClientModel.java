import java.io.*; 
import java.net.*;
import java.util.*;
import java.util.ArrayList;

public class ClientModel {

	private final int controlPort = 1370;

	private int playerNumber;
	private boolean connectedToServer;
	private boolean isHosting;

	private String serverHostIP, userName;
	private int port;
	private Socket ControlSocket;
	private DataOutputStream toServer;
	private ArrayList<List<Integer>> moves = new ArrayList<List<Integer>>();

	private GUI gui;

	public ClientModel(GUI gui) {
		this.gui = gui;
		gui.getStartButton().addActionListener(e -> hostGame());
        gui.getMenuQuitButton().addActionListener(e -> quitGame());
        gui.getMenuHostGameButton().addActionListener(e -> connectToServer('h'));
        gui.getMenuJoinGameButton().addActionListener(e -> connectToServer('c'));
		gui.getRefreshButton().addActionListener(e -> updatePlayerList());

		connectedToServer = false;
	}

	private boolean verifyConnectionInputs() {
		serverHostIP = gui.getServerHostIPField().getText();

        // Check that is IP format
        if (!serverHostIP.matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
            System.out.println("Invalid IP");
            return false;
        }

        try {
            port = Integer.parseInt(gui.getServerHostPortField().getText());
        }
        catch(Exception e) {
            System.out.println("Invalid port");
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
    public void connectToServer(char hc) {

		if (!connectedToServer) {
			if (!verifyConnectionInputs()) {
				return;
			}
			port = controlPort;

			// TODO: need error checking to make sure hostname and port are valid?
			try {
				System.out.println("Attemping to connect to host: " + serverHostIP + " at port " + controlPort);
				ControlSocket = new Socket(serverHostIP, controlPort);
				System.out.println("You are connected to " + serverHostIP);

				toServer = new DataOutputStream(ControlSocket.getOutputStream());

				String dataToServer = serverHostIP + " " + port + " " + userName;

				toServer.writeUTF(dataToServer) ;
				System.out.println("Sent " + dataToServer + " to server");			
				connectedToServer = true;
				
			}
			catch (Exception e) {
				System.out.println("Unable to connect to host: " + serverHostIP + " on port " + controlPort);
				e.printStackTrace();
				return;
			}
		}

		isHosting = hc == 'h' ? true : false;
		gui.swapPanel(hc == 'h' ? "host" : "join");
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
			
			
		}
		catch (Exception e) {
			System.err.println("Could not get player list");
			e.printStackTrace();
		}
	}

	/** establish connection with server, tell it to make a new game, connect client to that game */
    public void hostGame() {
		try {
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
			System.out.println("Could close streams");
			e.printStackTrace();
		}
	}


	public void play() {
		gui.swapPanel("game");
	}

    /** establish connection with server, join selected game */
    public void joinGame() {
        // TODO: 
        //possibly unnecessary
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
			String sentence = "close:";
			port += 2; 
			ServerSocket welcomeData = new ServerSocket(port);

			toServer.writeUTF(port + " " + sentence + " " + serverHostIP);

			Socket dataSocket = welcomeData.accept();
			DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
			try {
				String file = inData.readUTF();
				System.out.println("Closing server. Code: " + file);
				
			}
			catch (Exception e) {
				System.out.println("Could not close server");
				e.printStackTrace();
			}
			inData.close();
			toServer.close();
			dataSocket.close();
			welcomeData.close();
			ControlSocket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
 	}
	public void quitGame() {
		
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
