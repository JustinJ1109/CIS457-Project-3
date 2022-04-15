
import java.awt.*;
import javax.swing.*;

public class GUI {

    private final Dimension SCREEN_SIZE = new Dimension(500, 600);

    private JFrame frame;

    private JButton 
        menuHostGameButton,
        menuConnectGameButton,
        menuQuitButton,

        startButton,
        backButton;

    private JPanel
        menuPanel,
        joinPanel,
        hostPanel,
        gamePanel;

    private JTextField
        lobbyNameField,
        userNameField;

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
        menuTitleLabel.setFont(new Font(menuTitleLabel.getFont().getName(), Font.PLAIN, 40));

        menuHostGameButton = new JButton("Host");
        menuConnectGameButton = new JButton("Join");
        menuQuitButton = new JButton("Quit");

        menuPanel.add(menuTitleLabel);
        menuPanel.add(menuHostGameButton);
        menuPanel.add(menuConnectGameButton);
        menuPanel.add(menuQuitButton);

        menuLayout.setHorizontalGroup(
            menuLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(menuTitleLabel)
                .addComponent(menuHostGameButton)
                .addComponent(menuConnectGameButton)
                .addComponent(menuQuitButton)
        );
        menuLayout.setVerticalGroup(
            menuLayout.createSequentialGroup()
                .addComponent(menuTitleLabel)
                .addComponent(menuHostGameButton)
                .addComponent(menuConnectGameButton)
                .addComponent(menuQuitButton)
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

        backButton.addActionListener(e -> swapPanel("menu"));

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
        

    /** INITIAL STATE SETUP DO NOT TOUCH */
        frame.getContentPane().add(menuPanel);

        frame.setPreferredSize(SCREEN_SIZE);
        frame.setMaximumSize(SCREEN_SIZE);
        frame.setMinimumSize(SCREEN_SIZE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public JButton getStartButton() {
        return startButton;
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

    public void swapPanel(String newPanel) {
        if (newPanel.equals("menu")) {
            frame.setVisible(false);
            frame.setContentPane(menuPanel);
            frame.setVisible(true);
        }
        else if (newPanel.equals("host")) {
            frame.setVisible(false);
            frame.setContentPane(hostPanel);
            frame.setVisible(true);

        }
        else if (newPanel.equals("join")) {
            frame.setVisible(false);
            frame.setContentPane(joinPanel);
            frame.setVisible(true);

        }
        else if (newPanel.equals("game")) {
            frame.setVisible(false);
            frame.setContentPane(gamePanel);
            frame.setVisible(true);

        }
        else {
            System.out.println("Unknown panel '" + newPanel + "'");
        }
    }
}
