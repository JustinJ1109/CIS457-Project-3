import java.io.*; 
import java.net.*;
import java.util.*;
import java.util.ArrayList;

public class ClientModel {

	private final int controlPort = 1370;

	private String serverHostIP, userName;
	private int port;
	private Socket ControlSocket;
	private DataOutputStream toServer;
	private ArrayList<List<Integer>> moves = new ArrayList<List<Integer>>();

    /** establish connection with server */
    public boolean connectToServer() {
		port = controlPort;

		// TODO: need error checking to make sure hostname and port are valid?
		try {
			System.out.println("Attemping to connect to host: " + serverHostIP + " at port " + controlPort);
			ControlSocket = new Socket(serverHostIP, controlPort);
			System.out.println("You are connected to " + serverHostIP);

			toServer = new DataOutputStream(ControlSocket.getOutputStream());
			toServer.writeUTF(serverHostIP + " " + port) ;
			System.out.println("Sending " + serverHostIP + " " + port + " " + " to server");			

			toServer.flush();
		}
		catch (Exception e) {
			System.out.println("Unable to connect to host: " + serverHostIP + " on port " + controlPort);
			e.printStackTrace();
			return false;
		}

		return true;
    }

	/** establish connection with server, tell it to make a new game, connect client to that game */
    public void hostGame() {
		connectToServer();
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

    public void setUserName(String name) {
        userName = name;
    }

	public void setServerHostIP(String ip) {
		serverHostIP = ip;
	}

	public void setServerPort(int port) {
		this.port = port;
	}


}   
