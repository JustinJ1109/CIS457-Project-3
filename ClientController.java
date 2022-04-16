// import java.util.regex.*;

public class ClientController {
    
    private GUI gui;
    private ClientModel model;
    private String serverHostIP;

    public ClientController(ClientModel model, GUI gui) {
        this.gui = gui;
        this.model = model;
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

    /****************************************************************
     * Receive data from user GUI input. Validate info received 
     * and send to model. Tell model to send request to server
     ***************************************************************/
    private void hostGame() {
        // check if info is correct first
        String userName = gui.getUserNameField('h').getText();
        String boardSize = gui.getBoardSizeBox().getSelectedItem().toString();
        String numPlayers = gui.getNumPlayersBox().getSelectedItem().toString();

        if (userName.equals("")) {
            System.out.println("Must input a username");
            return;
        }
        if (userName.contains(" ")) {
            System.out.println("Username cannot contain spaces");
            return;
        }
        
        model.setUserName(userName);

        try {
            model.hostGame();
        }
        catch (Exception e) {
            System.out.println("Could not Host game on server:");
            e.printStackTrace();
        }

        gui.getUserNameField('h').setText("");
        

        System.out.println("Hosting a game of " + numPlayers + " players on board size " + boardSize + " for " + userName);


        //gui.swapPanel("game");
    }

    /****************************************************************
     * Pretty much the same as the hostGame, minus the boardSize,
     * numPlayers fields.
     ***************************************************************/
    private void joinGame() {
        // check if info is correct first
        System.out.println("Joining");

    }

    /****************************************************************
     * Tell model to request disconnect, cleanup, then disconnect this
     ***************************************************************/
    private void quitGame() {
        // check that all is cleaned up before quitting
        System.out.println("Quitting");
        // FIXME: clean up

        System.exit(0);
    }

    public static void main(String[] args) {
        GUI gui = new GUI("test");
        ClientModel cm = new ClientModel();
        ClientController cc = new ClientController(cm, gui);

        cc.initController();
    }
}
