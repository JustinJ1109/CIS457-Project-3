// import java.util.regex.*;

public class ClientController {
    
    private GUI gui;
    private ClientModel model;
    private String serverHostIP;
    private boolean connectedToServer;

    public ClientController(ClientModel model, GUI gui) {
        this.gui = gui;
        this.model = model;

        connectedToServer = false;
    }

    public void initController() {
        gui.getStartButton().addActionListener(e -> hostGame());
        gui.getMenuQuitButton().addActionListener(e -> quitGame());
        gui.getMenuHostGameButton().addActionListener(e -> connectToServer('h'));
        gui.getMenuJoinGameButton().addActionListener(e -> connectToServer('c'));
    }

    private void connectToServer(char hostOrClient) {

        serverHostIP = gui.getServerHostIPField().getText();
        int port;

        // Check that is IP format
        if (!serverHostIP.matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
            System.out.println("Invalid IP");
            return;
        }

        try {
            port = Integer.parseInt(gui.getServerHostPortField().getText());
        }
        catch(Exception e) {
            System.out.println("Invalid port");
            return;
        }
        
        //TODO: add connection logic here
        model.setServerHostIP(serverHostIP);
        model.setServerPort(port);

        System.out.println("retrieved IP " + serverHostIP + " port " + port);

        
        gui.swapPanel(hostOrClient == 'h' ? "host" : "join");

    }

    private void disconnectFromServer() {
        connectedToServer = false;
    }

    /** tell client to open a new game with client's input info */
    private void hostGame() {
        // check if info is correct first
        String userName = gui.getUserNameField('h').getText();
        String lobbyName = gui.getLobbyNameField().getText();

        if (userName.equals("")) {
            System.out.println("Must input a username");
            return;
        }
        if (userName.contains(" ")) {
            System.out.println("Username cannot contain spaces");
            return;
        }

        if (lobbyName.equals("")) {
            System.out.println("Must input a lobby name");
            return;
        }
        if (lobbyName.contains(" ")) {
            System.out.println("Lobby name cannot contain spaces");
        }
        
        model.setUserName(userName);

        try {
            model.hostGame(lobbyName);
        }
        catch (Exception e) {
            System.out.println("Could not Host game on server:");
            e.printStackTrace();
        }

        gui.getUserNameField('h').setText("");
        gui.getLobbyNameField().setText("");

        System.out.println("Hosting");
        gui.swapPanel("game");
    }

    /** tell server to join selected game */
    private void joinGame() {
        // check if info is correct first
        System.out.println("Joining");

    }

    /** cleanup and close the client **/
    private void quitGame() {
        // check that all is cleaned up before quitting
        System.out.println("Quitting");
        // FIXME: clean up

        System.exit(0);
    }

    /** Send a command to the server to update the list of available games to join **/
    private void refreshLobby() {

    }


    public static void main(String[] args) {
        GUI gui = new GUI("test");
        ClientModel cm = new ClientModel();
        ClientController cc = new ClientController(cm, gui);

        cc.initController();
    }
}
