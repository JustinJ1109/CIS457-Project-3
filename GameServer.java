/**
 * GameServer.java
 * 
 * @version 4.17.22
 * @author  Justin Jahlas, 
 * 			Brennan Luttrel, 
 * 			Munu Bhai, 
 * 			Cole Blunt, 
 * 			Noah Meyers
 */

import java.io.IOException;
import java.net.*;

/********************************************************************
 * Main Game Server that dispatches threads to handle every unique
 * connection
 *******************************************************************/
public class GameServer {

    private static final int LISTENING_PORT = 1370;
    
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;
        ServerHandler sh;

        try {
            serverSocket = new ServerSocket(LISTENING_PORT);
        }
        catch (IOException e) {
            System.err.println("Could not listen on port: " + LISTENING_PORT);
            e.printStackTrace();
            System.exit(-1);
        }

        while(listening) {
            try {
                sh = new ServerHandler(serverSocket.accept());
                Thread t = new Thread(sh);
                t.start();
            }
            catch (Exception e) {
                System.out.println("Could not open thread");
                e.printStackTrace();
            }
        }
    }
}
