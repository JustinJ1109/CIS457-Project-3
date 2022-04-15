import java.io.*; 
import java.net.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.util.ArrayList;

public class ClientModel {
	private String hostName, playerName;
	private int port;
	private final int controlPort = 1370;
	private Socket ControlSocket;
	private DataOutputStream toServer;
	private ArrayList<List<Integer>> moves = new ArrayList<List<Integer>>();
    	String userName;
    /** establish connection with server, tell it to make a new game, connect client to that game */
    public void hostGame(String lobbyName) {
	doConnection();
    }

    /** establish connection with server */
    public boolean doConnection() {
	port = controlPort;

		// TODO: need error checking to make sure hostname and port are valid?
		try {
			System.out.println("Attemping to connect to host: " + 		ControlSocket.getInetAddress() + " at port " + controlPort);
			ControlSocket = new Socket(serverHostName, controlPort);
			System.out.println("You are connected to " + serverHostName);

			toServer = new DataOutputStream(ControlSocket.getOutputStream());
			toServer.writeUTF(hostName + " " + port + " " + hostName) ;
			System.out.println("Sending " + hostName + " " + port + " " + " to server");
			FileInputStream file = new FileInputStream("filelist.xml");
			byte[] buffer = new byte[1024];
			int bytes = 0;
			while ((bytes = file.read(buffer)) != -1) {
				System.out.println(bytes + " bytes sent");
				toServer.write(buffer, 0, bytes);
			}
			toServer.flush();
			System.out.println("File sent");
		}
		catch (Exception e) {
			System.out.println("Unable to connect to host: " + serverHostName + " on port 				" + controlPort);
			e.printStackTrace();
			return false;
		}

		return true;
    }

    /** establish connection with server, join selected game */
    public void joinGame() {
        // TODO: 
        //possibly unnecessary
    }

    /** close all IO streams and sockets, disconnect from server */
    public void disconnectFromServer() {
	try {
		String sentence = "close:";
		port += 2; 
		ServerSocket welcomeData = new ServerSocket(port);

		toServer.writeUTF(port + " " + sentence + " " + hostName);

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
        playerName = name;
    }


}   
