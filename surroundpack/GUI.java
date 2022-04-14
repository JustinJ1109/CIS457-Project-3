import java.awt.*;
import javax.swing.*;

public class GUI {

    private final Dimension SCREEN_SIZE = new Dimension(500, 600);

    private JFrame frame;

    private JButton 
        menuHostGameButton,
        menuConnectGameButton,
        menuQuitButton;

    public GUI(String title) {
        frame = new JFrame(title);
        
        
        JPanel menuPanel = new JPanel();

        GroupLayout menuLayout = new GroupLayout(menuPanel);
        menuLayout.setAutoCreateGaps(true);
        menuLayout.setAutoCreateContainerGaps(true);
        menuPanel.setLayout(menuLayout);

        JLabel menuTitleLabel = new JLabel("Surround Game Online!");
        menuTitleLabel.setFont(new Font(menuTitleLabel.getFont().getName(), Font.PLAIN, 40));

        

        menuHostGameButton = new JButton("Host");
        menuConnectGameButton = new JButton("Join");
        menuQuitButton = new JButton("Quit");

        Dimension buttonSize = new Dimension(100, 30);

        menuHostGameButton.setPreferredSize(buttonSize);
        menuConnectGameButton.setPreferredSize(buttonSize);
        menuQuitButton.setPreferredSize(buttonSize);


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

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(SCREEN_SIZE);
        frame.setPreferredSize(SCREEN_SIZE);
        frame.setMaximumSize(SCREEN_SIZE);
        frame.setMinimumSize(SCREEN_SIZE);
        frame.setResizable(false);
        frame.pack();

        frame.getContentPane().add(menuPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private void drawMenu() {
        
    }
    public static void main(String[] args) {
        GUI g = new GUI("test");
    }
}

