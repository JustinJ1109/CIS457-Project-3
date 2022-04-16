import java.io.IOException;
import java.net.*;


public class GameServer {

    private static final int LISTENING_PORT = 44545;
    
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
                System.out.println("Opening new Server Handler thread");
                Thread t = new Thread(sh);
                t.start();
            }
            catch (Exception e) {
                System.out.println("Could not start thread");
                e.printStackTrace();
            }
        }
    }
}
