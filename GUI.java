
import java.awt.*;
import javax.swing.*;

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
        backButton,

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
        userNameField,
        serverHostIPField;

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
        gamePanel = new JPanel();

    /** MENU LAYOUT */
        GroupLayout menuLayout = new GroupLayout(menuPanel);
        menuLayout.setAutoCreateGaps(true);
        menuLayout.setAutoCreateContainerGaps(true);
        menuPanel.setLayout(menuLayout);

        JLabel menuTitleLabel = new JLabel("Surround Game Online!");
        JLabel menuServerHostIPLabel = new JLabel("Server IP");

        menuTitleLabel.setFont(new Font(menuTitleLabel.getFont().getName(), Font.PLAIN, 40));

        menuHostGameButton = new JButton("Host");
        menuConnectGameButton = new JButton("Join");
        menuQuitButton = new JButton("Quit");

        // Text field
        serverHostIPField = new JTextField();
        serverHostIPField.setColumns(22);

        JPanel serverConnectionInfoPanel = new JPanel();
        serverConnectionInfoPanel.setLayout(new BoxLayout(serverConnectionInfoPanel, BoxLayout.Y_AXIS));
        serverConnectionInfoPanel.add(menuServerHostIPLabel);
        serverConnectionInfoPanel.add(serverHostIPField);

        menuPanel.add(menuTitleLabel);
        menuPanel.add(menuHostGameButton);
        menuPanel.add(menuConnectGameButton);
        menuPanel.add(menuQuitButton);
        menuPanel.add(serverConnectionInfoPanel);

        menuLayout.setHorizontalGroup(
            menuLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(menuTitleLabel)
                .addComponent(menuHostGameButton)
                .addComponent(menuConnectGameButton)
                .addComponent(menuQuitButton)

                .addComponent(serverConnectionInfoPanel)
        );
        menuLayout.setVerticalGroup(
            menuLayout.createSequentialGroup()
                .addComponent(menuTitleLabel)
                .addComponent(menuHostGameButton)
                .addComponent(menuConnectGameButton)
                .addComponent(menuQuitButton)

                .addComponent(serverConnectionInfoPanel)
        
        );

        menuHostGameButton.addActionListener(e -> swapPanel("host"));
        menuConnectGameButton.addActionListener(e -> swapPanel("join"));

    /** HOST LAYOUT */
        GroupLayout hostLayout = new GroupLayout(hostPanel);
        hostLayout.setAutoCreateGaps(true);
        hostLayout.setAutoCreateContainerGaps(true);
        hostPanel.setLayout(hostLayout);

        JLabel hostTitleLabel = new JLabel("Host a Game");
        JLabel userNameLabel = new JLabel("User Name");
        JLabel lobbyNameLabel = new JLabel("Lobby Name");

        userNameField = new JTextField();
        lobbyNameField = new JTextField();

        userNameField.setColumns(22);
        lobbyNameField.setColumns(22);

        hostTitleLabel.setFont(new Font(hostTitleLabel.getFont().getName(), Font.PLAIN, 40));

        // add text fields and start button
        startButton = new JButton("Start Game");
        backButton = new JButton("Back");


        JPanel hostUNPanel = new JPanel();
        hostUNPanel.add(userNameLabel);
        hostUNPanel.add(userNameField);

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
                .addComponent(backButton)
                .addComponent(startButton)
        );
        hostLayout.setVerticalGroup(
            hostLayout.createSequentialGroup()
                .addComponent(hostTitleLabel)
                .addComponent(hostInputPanels)
                .addComponent(backButton)
                .addComponent(startButton)
        );

    /** JOIN LAYOUT */

        GroupLayout joinLayout = new GroupLayout(joinPanel);
        joinLayout.setAutoCreateGaps(true);
        joinLayout.setAutoCreateContainerGaps(true);
        joinPanel.setLayout(joinLayout);

        JLabel joinTitleLabel = new JLabel("Join a Game");
        JLabel joiningUserNameLabel = new JLabel("User Name");

        userNameField = new JTextField();
        userNameField.setColumns(22);

        joinTitleLabel.setFont(new Font(joinTitleLabel.getFont().getName(), Font.PLAIN, 40));

        // add text fields and start button
        joinButton = new JButton("Join Game");


        JPanel joinUNPanel = new JPanel();
        joinUNPanel.add(joiningUserNameLabel);
        joinUNPanel.add(userNameField);

        // JPanel joinLobbyNamePanel = new JPanel();
        // joinLobbyNamePanel.add(lobbyNameLabel);
        // joinLobbyNamePanel.add(lobbyNameField);

        JPanel joinInputPanels = new JPanel();
        joinInputPanels.setLayout(new BoxLayout(joinInputPanels, BoxLayout.Y_AXIS));
        joinInputPanels.add(joinUNPanel);
        // joinInputPanels.add(joinLobbyNamePanel);

        // add fields and button to panel
        joinPanel.add(joinTitleLabel);
        joinPanel.add(joinInputPanels);
        joinPanel.add(joinButton);

        joinLayout.setHorizontalGroup(
            joinLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(joinTitleLabel)
                .addComponent(joinInputPanels)
                .addComponent(backButton)
                .addComponent(joinButton)
        );
        joinLayout.setVerticalGroup(
            joinLayout.createSequentialGroup()
                .addComponent(joinTitleLabel)
                .addComponent(joinInputPanels)
                .addComponent(backButton)
                .addComponent(joinButton)
        );
        

    /** INITIAL STATE SETUP DO NOT TOUCH */

        backButton.addActionListener(e -> swapPanel("menu"));

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

    public JTextField getUserNameField() {
        return userNameField;
    }

    public JTextField getLobbyNameField() {
        return lobbyNameField;
    }

    public JTextField getServerHostIPField() {
        return serverHostIPField;
    }

    public void swapPanel(String newPanel) {
        if (newPanel.equals("menu")) {
            // frame.setVisible(false);
            // frame.setContentPane(menuPanel);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(menuPanel);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
            frame.setVisible(true);

        }
        else if (newPanel.equals("host")) {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(hostPanel);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
            frame.setVisible(true);
        }
        else if (newPanel.equals("join")) {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(hostPanel);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
            frame.setVisible(true);

        }
        else if (newPanel.equals("game")) {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(hostPanel);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
            frame.setVisible(true);

        }
        else {
            System.out.println("Unknown panel '" + newPanel + "'");
        }
    }
}
