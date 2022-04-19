/**
 * GUI.java
 * 
 * @version 4.17.22
 * @author  Justin Jahlas, 
 * 			Brennan Luttrel, 
 * 			Prakash Lingden, 
 * 			Cole Blunt, 
 * 			Noah Meyers
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import surroundpack.Surround4Panel;

/********************************************************************
 * Client GUI that displays all menus the user can navigate through.
 *******************************************************************/
public class GUI {

    private final static Dimension SCREEN_SIZE = new Dimension(500, 600);

    /** Frame that holds everything */
    private JFrame frame;

    /** JButtons */
    private JButton 
        menuHostGameButton,
        menuConnectGameButton,
        menuQuitButton,

        hostStartButton,
        hostBackButton,

        joinStartButton,
        joinBackButton,

        refreshButton,
        lobbyBackButton,
        lobbyPlayButton;

    /** JPanels */
    private JPanel
        menuPanel,
        joinPanel,
        hostPanel,
        lobbyPanel;

    private Surround4Panel gamePanel;

    /** Text Fields (user input) */
    private JTextField
        userNameField,
        serverHostIPField;

    private JComboBox 
        numPlayersBox,
        boardSizeBox;

    private JTable lobbyTable;

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
        lobbyPanel = new JPanel();
        gamePanel = new Surround4Panel();

    /** MENU LAYOUT */
        GroupLayout menuLayout = new GroupLayout(menuPanel);
        menuLayout.setAutoCreateGaps(true);
        menuLayout.setAutoCreateContainerGaps(true);
        menuPanel.setLayout(menuLayout);

        JLabel menuTitleLabel = new JLabel("Surround Game Online!");
        JLabel menuServerHostIPLabel = new JLabel("Server IP");
        JLabel menuUserNameLabel = new JLabel("Username");

        menuTitleLabel.setFont(new Font(menuTitleLabel.getFont().getName(), Font.PLAIN, 40));

        menuHostGameButton = new JButton("Host");
        menuConnectGameButton = new JButton("Join");
        menuQuitButton = new JButton("Quit");

        // Text field
        userNameField = new JTextField();
        serverHostIPField = new JTextField();

        userNameField.setColumns(22);
        serverHostIPField.setColumns(22);

        JPanel userNamePanel = new JPanel();
        userNamePanel.add(menuUserNameLabel);
        userNamePanel.add(userNameField);

        JPanel serverConnectionIPPanel = new JPanel();
        serverConnectionIPPanel.add(menuServerHostIPLabel);
        serverConnectionIPPanel.add(serverHostIPField);

        menuPanel.add(menuTitleLabel);
        menuPanel.add(menuHostGameButton);
        menuPanel.add(menuConnectGameButton);
        menuPanel.add(menuQuitButton);
        menuPanel.add(userNamePanel);
        menuPanel.add(serverConnectionIPPanel);

        menuLayout.setHorizontalGroup(
            menuLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(menuTitleLabel)
                .addComponent(menuHostGameButton)
                .addComponent(menuConnectGameButton)
                .addComponent(menuQuitButton)

                .addComponent(userNamePanel)
                .addComponent(serverConnectionIPPanel)
        );
        menuLayout.setVerticalGroup(
            menuLayout.createSequentialGroup()
                .addComponent(menuTitleLabel)
                .addComponent(menuHostGameButton)
                .addComponent(menuConnectGameButton)
                .addComponent(menuQuitButton)

                .addComponent(userNamePanel)

                .addComponent(serverConnectionIPPanel)
        );

    /** HOST LAYOUT */
        GroupLayout hostLayout = new GroupLayout(hostPanel);
        hostLayout.setAutoCreateGaps(true);
        hostLayout.setAutoCreateContainerGaps(true);
        hostPanel.setLayout(hostLayout);

        JLabel hostTitleLabel = new JLabel("Host a Game");
        hostTitleLabel.setFont(new Font(hostTitleLabel.getFont().getName(), Font.PLAIN, 40));

        // Labels
        JLabel hostBoardSizeLabel = new JLabel("Board Size");
        JLabel hostNumPlayersLabel = new JLabel("Number of Players");

        // Combo Box contents
        String boardSizes[] = {"6x6", "8x8", "10x10", "12x12", "16x16"};
        String numPlayers[] = {"2", "3", "4", "5"};

        // Combo Boxes
        boardSizeBox = new JComboBox<>(boardSizes);
        numPlayersBox = new JComboBox<>(numPlayers);

        // default to 10x10
        boardSizeBox.setSelectedIndex(2);


        // add text fields and start button
        hostStartButton = new JButton("Host");
        hostBackButton = new JButton("Back");


        // Sub-panel for board size box
        JPanel hostBoardSizePanel = new JPanel();
        hostBoardSizePanel.add(hostBoardSizeLabel);
        hostBoardSizePanel.add(boardSizeBox);

        // Sub-panel for num players box
        JPanel hostNumPlayersPanel = new JPanel();
        hostNumPlayersPanel.add(hostNumPlayersLabel);
        hostNumPlayersPanel.add(numPlayersBox);

        // add fields and button to panel
        hostPanel.add(hostTitleLabel);
        hostPanel.add(hostStartButton);
        hostPanel.add(hostBoardSizePanel);
        hostPanel.add(hostNumPlayersPanel);

        hostLayout.setHorizontalGroup(
            hostLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(hostTitleLabel)
                .addComponent(hostBoardSizePanel)
                .addComponent(hostNumPlayersPanel)
                .addComponent(hostBackButton)
                .addComponent(hostStartButton)
                
        );
        hostLayout.setVerticalGroup(
            hostLayout.createSequentialGroup()
                .addComponent(hostTitleLabel)
                .addComponent(hostBoardSizePanel)
                .addComponent(hostNumPlayersPanel)
                .addComponent(hostBackButton)
                .addComponent(hostStartButton)
                
        );

    /** JOIN LAYOUT */

        GroupLayout joinLayout = new GroupLayout(joinPanel);
        joinLayout.setAutoCreateGaps(true);
        joinLayout.setAutoCreateContainerGaps(true);
        joinPanel.setLayout(joinLayout);

        JLabel joinTitleLabel = new JLabel("Join a Game");

        joinTitleLabel.setFont(new Font(joinTitleLabel.getFont().getName(), Font.PLAIN, 40));

        // add text fields and start button
        joinStartButton = new JButton("Join Game");
        joinBackButton = new JButton("Back");

        JPanel joinInputPanels = new JPanel();
        joinInputPanels.setLayout(new BoxLayout(joinInputPanels, BoxLayout.Y_AXIS));

        // add fields and button to panel
        joinPanel.add(joinTitleLabel);
        joinPanel.add(joinInputPanels);
        joinPanel.add(joinStartButton);

        joinLayout.setHorizontalGroup(
            joinLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(joinTitleLabel)
                .addComponent(joinInputPanels)
                .addComponent(joinBackButton)
                .addComponent(joinStartButton)
        );
        joinLayout.setVerticalGroup(
            joinLayout.createSequentialGroup()
                .addComponent(joinTitleLabel)
                .addComponent(joinInputPanels)
                .addComponent(joinBackButton)
                .addComponent(joinStartButton)
        );

    /** LOBBY LAYOUT */

        GroupLayout lobbyLayout = new GroupLayout(lobbyPanel);
        lobbyLayout.setAutoCreateGaps(true);
        lobbyLayout.setAutoCreateContainerGaps(true);
        lobbyPanel.setLayout(lobbyLayout);

        JLabel lobbyTitleLabel = new JLabel("Lobby");


        lobbyTitleLabel.setFont(new Font(lobbyTitleLabel.getFont().getName(), Font.PLAIN, 40));

        // add text fields and start button
        refreshButton = new JButton("Refresh");
        lobbyPlayButton = new JButton("Play");
        lobbyBackButton = new JButton("Back");

        String[] colNames = {"User", "Player"};
        String[][] initTableData = {{""}, {""}};

        lobbyTable = new JTable(initTableData, colNames);
        lobbyTable.setPreferredScrollableViewportSize(lobbyTable.getPreferredSize());

        JScrollPane lobbyTablePane = new JScrollPane(lobbyTable);


        // add fields and button to panel
        lobbyPanel.add(lobbyTitleLabel);
        lobbyPanel.add(refreshButton);
        lobbyPanel.add(lobbyPlayButton);
        lobbyPanel.add(lobbyBackButton);
        lobbyPanel.add(lobbyTable);

        lobbyLayout.setHorizontalGroup(
            lobbyLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lobbyTitleLabel)
                .addComponent(refreshButton)
                .addComponent(lobbyTablePane)
                .addComponent(lobbyBackButton)
                .addComponent(lobbyPlayButton)
        );
        lobbyLayout.setVerticalGroup(
            lobbyLayout.createSequentialGroup()
                .addComponent(lobbyTitleLabel)
                .addComponent(refreshButton)
                .addComponent(lobbyTablePane)
                .addComponent(lobbyBackButton)
                .addComponent(lobbyPlayButton)
        );
        

    /** INITIAL STATE SETUP DO NOT TOUCH BELOW **/
        hostBackButton.addActionListener(e -> swapPanel("menu"));
        joinBackButton.addActionListener(e -> swapPanel("menu"));
        menuHostGameButton.addActionListener(e -> swapPanel("host"));
        menuConnectGameButton.addActionListener(e -> swapPanel("join"));
        // lobbyBackButton.addActionListener(e -> swapPanel("menu"));

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

    /****************************************************************
     * Getters and Setters for buttons, textfields, combobox, 
     * and table
     * 
     ***************************************************************/
    public JButton getHostStartButton() {
        return hostStartButton;
    }

    public JButton getJoinStartButton() {
        return joinStartButton;
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

    public JButton getRefreshButton() {
        return refreshButton;
    }

    public JButton getLobbyPlayButton() {
        return lobbyPlayButton;
    }

    public JButton getLobbyBackButton() {
        return lobbyBackButton;
    }

    public JTextField getUserNameField() {
        return userNameField;
    }

    public JTextField getServerHostIPField() {
        return serverHostIPField;
    }

    public JComboBox getNumPlayersBox() {
        return numPlayersBox;
    }

    public JComboBox getBoardSizeBox() {
        return boardSizeBox;
    }

    public JTable getLobbyTable() {
        return lobbyTable;
    }

    public void generateDialog(String msg, String label) {
        JOptionPane.showMessageDialog(frame, msg, label, JOptionPane.OK_OPTION);
    }

    public int generateWinDialog(String msg, String label) {
        int input = JOptionPane.showOptionDialog(frame, msg, label, JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
        return input;
    }

    public Surround4Panel getGamePanel() {
        return gamePanel;
    }

    /****************************************************************
     * Update the JTable with the input new data
     * 
     * DOESN'T WORK AND JTABLES ARE THE BANE OF MY EXISTENCE WHY IS
     * IT SO HARD TO JUST UPDATE A TABLE
     * 
     * @param data
     * @param playerNum
     *******************************************************************/
    public void updateLobbyTable(String[] data, int[] playerNum) {
        lobbyTable.setModel(new DefaultTableModel());
        DefaultTableModel tableModel = (DefaultTableModel) lobbyTable.getModel();
        tableModel.setRowCount(0);

        Object[] row = new Object[2];

        for (int i = 0; i < data.length; i++) {
            row[0] = data[i];
            row[1] = playerNum[i];
            tableModel.addRow(row);
        }
    }

    public void resetGameBoard() {
        gamePanel = new Surround4Panel();
    }

    //FIXME: doesnt work
    /****************************************************************
     * Remove the start button from non-host gui screen
     *******************************************************************/
    public void rmStartButtonFromNonHost() {
        lobbyPanel.getLayout().removeLayoutComponent(lobbyPlayButton);
    }

    /****************************************************************
     * Display new panel on screen when event is triggered
     * 
     * @param newPanel
     ***************************************************************/
    public void swapPanel(String newPanel) {
        // System.out.println("Swapping to " + newPanel);
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
        else if (newPanel.equals("lobby")) {
            frame.getContentPane().add(lobbyPanel);
        }
        else {
            System.out.println("Unknown panel '" + newPanel + "'");
        }
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();
        frame.setVisible(true);
    }
}
