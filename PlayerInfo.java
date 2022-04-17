import java.io.Serializable;

public class PlayerInfo implements Serializable {

    private String IP, userName;
    private int port, playerNumber, playerID;
    
    public PlayerInfo(String IP, int port, String userName, int playerID) {
        this.IP = IP;
        this.port = port;
        this.userName = userName;
        this.playerID = playerID;
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

    public int getPlayerID() {
        return playerID;
    }
}
