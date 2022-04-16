import java.io.Serializable;

public class PlayerInfo implements Serializable {

    private String IP, userName;
    private int port, playerNumber;
    
    public PlayerInfo(String IP, int port, String userName) {
        this.IP = IP;
        this.port = port;
        this.userName = userName;
    }

    public String getIP() {
        return IP;
    }

    public String getUserName() {
        return userName;
    }

    public int getPort() {
        return port;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int val) {
        playerNumber = val;
    }
}
