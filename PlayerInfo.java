
public class PlayerInfo {

    private String IP, userName;
    private int port;
    
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
}
