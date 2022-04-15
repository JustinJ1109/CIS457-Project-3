
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
        String temp = gui.getUserNameField().getText();

        if (temp.equals("")) {
            System.out.println("Must input a username");
            return;
        }
        if (temp.contains(" ")) {
            System.out.println("Username cannot contain spaces");
            return;
        }

        if (gui.getPortNumField().getText().equals("")) {
            System.out.println("Must input a port number");
            return;
        }
        
        model.setUserName(gui.getUserNameField().getText());

        try {
            // TODO: better port error checking
            int port = Integer.parseInt(gui.getPortNumField().getText());
            model.setPort(port);
        }
        catch (Exception e) {
            System.out.println("Invalid number used as port");
            return;
        }

        try {
            model.hostGame();
        }
        catch (Exception e) {
            System.out.println("Could not Host game on server:");
            e.printStackTrace();
        }

        System.out.println("Hosting");
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
