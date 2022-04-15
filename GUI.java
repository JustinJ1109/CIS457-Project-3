
import java.awt.*;
import javax.swing.*;
import surroundpack.Surround4Panel;

public class GUI {

    private final static Dimension SCREEN_SIZE = new Dimension(500, 600);

    /** Frame that holds everything */
    private JFrame frame;

    /** JButtons */
    private JButton 
        menuHostGameButton,
        menuConnectGameButton,
        menuQuitButton,

        startButton,
        hostBackButton,
        joinBackButton,

        joinButton;

    /** JPanels */
    private JPanel
        conentPanel,

        menuPanel,
        joinPanel,
        hostPanel,
        gamePanel;

    /** Text Fields (user input) */
    private JTextField
        lobbyNameField,
        hostUserNameField,
        joinUserNameField,
        serverHostIPField,
        serverHostPortField;

    /**************************************************************
     * GUI constructor
     * 
     *  Creates all panels for each GUI display and formats them
     *  for the menu, host screen, join screen, and game screen
     *************************************************************/
    public GUI(String title) {

        frame = new JFrame(title);

        menuPanel = new JPanel();
        joinPanel = new JPanel();
        hostPanel = new JPanel();
        gamePanel = new Surround4Panel();

    /** MENU LAYOUT */
        GroupLayout menuLayout = new GroupLayout(menuPanel);
        menuLayout.setAutoCreateGaps(true);
        menuLayout.setAutoCreateContainerGaps(true);
        menuPanel.setLayout(menuLayout);

        JLabel menuTitleLabel = new JLabel("Surround Game Online!");
        JLabel menuServerHostIPLabel = new JLabel("Server IP");
        JLabel menuServerPortLabel = new JLabel("Server Port");

        menuTitleLabel.setFont(new Font(menuTitleLabel.getFont().getName(), Font.PLAIN, 40));

        menuHostGameButton = new JButton("Host");
        menuConnectGameButton = new JButton("Join");
        menuQuitButton = new JButton("Quit");

        // Text field
        serverHostIPField = new JTextField();
        serverHostPortField = new JTextField();
        serverHostIPField.setColumns(22);
        serverHostPortField.setColumns(22);

        JPanel serverConnectionIPPanel = new JPanel();
        serverConnectionIPPanel.add(menuServerHostIPLabel);
        serverConnectionIPPanel.add(serverHostIPField);

        JPanel serverConnectionPortPanel = new JPanel();
        serverConnectionPortPanel.add(menuServerPortLabel);
        serverConnectionPortPanel.add(serverHostPortField);

        menuPanel.add(menuTitleLabel);
        menuPanel.add(menuHostGameButton);
        menuPanel.add(menuConnectGameButton);
        menuPanel.add(menuQuitButton);
        menuPanel.add(serverConnectionIPPanel);
        menuPanel.add(serverConnectionPortPanel);

        menuLayout.setHorizontalGroup(
            menuLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(menuTitleLabel)
                .addComponent(menuHostGameButton)
                .addComponent(menuConnectGameButton)
                .addComponent(menuQuitButton)

                .addComponent(serverConnectionIPPanel)
                .addComponent(serverConnectionPortPanel)
        );
        menuLayout.setVerticalGroup(
            menuLayout.createSequentialGroup()
                .addComponent(menuTitleLabel)
                .addComponent(menuHostGameButton)
                .addComponent(menuConnectGameButton)
                .addComponent(menuQuitButton)

                .addComponent(serverConnectionIPPanel)
                .addComponent(serverConnectionPortPanel)
        );

    /** HOST LAYOUT */
        GroupLayout hostLayout = new GroupLayout(hostPanel);
        hostLayout.setAutoCreateGaps(true);
        hostLayout.setAutoCreateContainerGaps(true);
        hostPanel.setLayout(hostLayout);

        JLabel hostTitleLabel = new JLabel("Host a Game");
        JLabel userNameLabel = new JLabel("User Name");
        JLabel lobbyNameLabel = new JLabel("Lobby Name");

        hostUserNameField = new JTextField();
        lobbyNameField = new JTextField();

        hostUserNameField.setColumns(22);
        lobbyNameField.setColumns(22);

        hostTitleLabel.setFont(new Font(hostTitleLabel.getFont().getName(), Font.PLAIN, 40));

        // add text fields and start button
        startButton = new JButton("Start Game");
        hostBackButton = new JButton("Back");


        JPanel hostUNPanel = new JPanel();
        hostUNPanel.add(userNameLabel);
        hostUNPanel.add(hostUserNameField);

        JPanel hostLobbyNamePanel = new JPanel();
        hostLobbyNamePanel.add(lobbyNameLabel);
        hostLobbyNamePanel.add(lobbyNameField);

        JPanel hostInputPanels = new JPanel();
        hostInputPanels.setLayout(new BoxLayout(hostInputPanels, BoxLayout.Y_AXIS));
        hostInputPanels.add(hostUNPanel);
        hostInputPanels.add(hostLobbyNamePanel);

        // add fields and button to panel
        hostPanel.add(hostTitleLabel);
        hostPanel.add(hostInputPanels);
        hostPanel.add(startButton);

        hostLayout.setHorizontalGroup(
            hostLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(hostTitleLabel)
                .addComponent(hostInputPanels)
                .addComponent(hostBackButton)
                .addComponent(startButton)
        );
        hostLayout.setVerticalGroup(
            hostLayout.createSequentialGroup()
                .addComponent(hostTitleLabel)
                .addComponent(hostInputPanels)
                .addComponent(hostBackButton)
                .addComponent(startButton)
        );

    /** JOIN LAYOUT */

        GroupLayout joinLayout = new GroupLayout(joinPanel);
        joinLayout.setAutoCreateGaps(true);
        joinLayout.setAutoCreateContainerGaps(true);
        joinPanel.setLayout(joinLayout);

        JLabel joinTitleLabel = new JLabel("Join a Game");
        JLabel joiningUserNameLabel = new JLabel("User Name");

        joinUserNameField = new JTextField();
        joinUserNameField.setColumns(22);

        joinTitleLabel.setFont(new Font(joinTitleLabel.getFont().getName(), Font.PLAIN, 40));

        // add text fields and start button
        joinButton = new JButton("Join Game");
        joinBackButton = new JButton("Back");


        JPanel joinUNPanel = new JPanel();
        joinUNPanel.add(joiningUserNameLabel);
        joinUNPanel.add(joinUserNameField);

        JPanel joinInputPanels = new JPanel();
        joinInputPanels.setLayout(new BoxLayout(joinInputPanels, BoxLayout.Y_AXIS));
        joinInputPanels.add(joinUNPanel);

        // add fields and button to panel
        joinPanel.add(joinTitleLabel);
        joinPanel.add(joinInputPanels);
        joinPanel.add(joinButton);

        joinLayout.setHorizontalGroup(
            joinLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(joinTitleLabel)
                .addComponent(joinInputPanels)
                .addComponent(joinBackButton)
                .addComponent(joinButton)
        );
        joinLayout.setVerticalGroup(
            joinLayout.createSequentialGroup()
                .addComponent(joinTitleLabel)
                .addComponent(joinInputPanels)
                .addComponent(joinBackButton)
                .addComponent(joinButton)
        );
        

    /** INITIAL STATE SETUP DO NOT TOUCH */

        hostBackButton.addActionListener(e -> swapPanel("menu"));
        joinBackButton.addActionListener(e -> swapPanel("menu"));

        frame.pack();
        frame.setPreferredSize(SCREEN_SIZE);
        frame.setMaximumSize(SCREEN_SIZE);
        frame.setMinimumSize(SCREEN_SIZE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        swapPanel("menu");

        frame.setVisible(true);
    }

    public JButton getStartButton() {
        return startButton;
    }

    public JButton getJoinButton() {
        return joinButton;
    }

    public JButton getMenuQuitButton() {
        return menuQuitButton;
    }

    public JButton getMenuHostGameButton() {
        return menuHostGameButton;
    }

    public JButton getMenuJoinGameButton() {
        return menuConnectGameButton;
    }

    public JTextField getUserNameField(char hc) {
        return hc == 'h' ? hostUserNameField : joinUserNameField;
    }

    public JTextField getLobbyNameField() {
        return lobbyNameField;
    }

    public JTextField getServerHostIPField() {
        return serverHostIPField;
    }

    public JTextField getServerHostPortField() {
        return serverHostPortField;
    }

    public void swapPanel(String newPanel) {
        System.out.println("Swapping to " + newPanel);
        frame.getContentPane().removeAll();

        if (newPanel.equals("menu")) {
            frame.getContentPane().add(menuPanel);

        }
        else if (newPanel.equals("host")) {
            frame.getContentPane().add(hostPanel);
        }
        else if (newPanel.equals("join")) {
            frame.getContentPane().add(joinPanel);

        }
        else if (newPanel.equals("game")) {
            frame.getContentPane().add(gamePanel);

        }
        else {
            System.out.println("Unknown panel '" + newPanel + "'");
        }
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();
        frame.setVisible(true);

    }
}
