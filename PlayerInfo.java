/**
 * PlayerInfo.java
 * 
 * @version 4.17.22
 * @author  Justin Jahlas, 
 * 			Brennan Luttrel, 
 * 			Munu Bhai, 
 * 			Cole Blunt, 
 * 			Noah Meyers
 */

import java.io.Serializable;

/********************************************************************
 * Struct-like class that just holds info about a player
 * 
 * includes: IP, userName, control port, player number, 
 * and unique playerID
 *******************************************************************/
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
