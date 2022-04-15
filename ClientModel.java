



public class ClientModel {

    String userName;
    int port;
    /** establish connection with server, tell it to make a new game, connect client to that game */
    public void hostGame() {

    }

    /** establish connection with server */
    public void connectToServer() throws Exception {

    }

    /** establish connection with server, join selected game */
    public void joinGame() {
        // TODO: 
        //possibly unnecessary
    }

    /** close all IO streams and sockets, disconnect from server */
    public void disconnectFromServer() {

    }

    public void setUserName(String name) {
        userName = name;
    }

    public void setPort(int port) {
        this.port = port;
    }
}   
