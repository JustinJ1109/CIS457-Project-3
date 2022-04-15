
public class ClientController {
    
    private GUI gui;
    private ClientModel model;

    public ClientController(ClientModel model, GUI gui) {
        this.gui = gui;
        this.model = model;
    }

    public void initController() {
        gui.getStartButton().addActionListener(e -> hostGame());
        gui.getMenuQuitButton().addActionListener(e -> quitGame());
    }

    /** tell client to open a new game with client's input info */
    private void hostGame() {
        // check if info is correct first
        String userName = gui.getUserNameField().getText();
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
        
        model.setUserName(gui.getUserNameField().getText());

        try {
            model.hostGame(lobbyName);
        }
        catch (Exception e) {
            System.out.println("Could not Host game on server:");
            e.printStackTrace();
        }

        gui.getUserNameField().setText("");
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
