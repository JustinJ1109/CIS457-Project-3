//package surroundpack;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Surround4Panel extends JPanel {

    private JButton[][] board;

    private JPanel panel1, panel2, panel3;
    private int boardSize, numPlayers, startingPlayer, lastRow, lastCol;
    private boolean undoStatus;
    private ButtonListener listen;
    private JMenuItem quitItem, newGameItem;
    private Surround4Game game;
    private JButton undoButton;

    private JLabel[] scoreLabels;
    private static int[] scores;



    public Surround4Panel(JMenuItem pQuitItem, JMenuItem pNewGameItem, JButton pundoButton) {

        undoStatus = false;
        undoButton = pundoButton;
        quitItem = pQuitItem;
        newGameItem = pNewGameItem;
        listen = new ButtonListener();


        setLayout(new BorderLayout());
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();

        String strBdSize = JOptionPane.showInputDialog (null,
                "Enter in the size of the board: ");
        if(strBdSize == null){
            JOptionPane.showMessageDialog(null, "Game canceled");
            System.exit(0);
        }
        try {
            boardSize = Integer.parseInt(strBdSize);
            if(boardSize <= 3 || boardSize >= 20) {
                //Jumps to catch statement
                throw new NumberFormatException();
            }
        }
        catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null,
                    "Invalid input. Using board of size 10.");
            boardSize = 10;
        }

        String strNumPlayers = JOptionPane.showInputDialog (null,
                "Enter the number of players: ");
        if(strNumPlayers == null){
            JOptionPane.showMessageDialog(null, "Game canceled");
            System.exit(0);
        }
        try {
            numPlayers = Integer.parseInt(strNumPlayers);
            if(numPlayers < 2 || numPlayers > 99) {
                //Jumps to catch statement
                throw new NumberFormatException();
            }
        }
        catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null,
                    "Invalid input. Use 2 players.");
            
            numPlayers = 2;
        }

        String strStartingPlayer = JOptionPane.showInputDialog(null,
                "Who starts first?");
        if(strStartingPlayer == null){
            JOptionPane.showMessageDialog(null, "Game canceled");
            System.exit(0);
        }
        try{
            startingPlayer = Integer.parseInt(strStartingPlayer);
            if(startingPlayer < 0 || startingPlayer > numPlayers - 1){
                //Jumps to catch statement
                throw new NumberFormatException();
            }
        }
        catch(NumberFormatException e ){
            JOptionPane.showMessageDialog(null,
                    "Invalid input. Starting with Player 0.");
            startingPlayer = 0;
        }

        createScores();

        game = new Surround4Game(boardSize,numPlayers,startingPlayer);
        createBoard();


        add(panel2, BorderLayout.NORTH);
        add(panel1, BorderLayout.CENTER);
        add(panel3, BorderLayout.SOUTH);

        undoButton.addActionListener(listen);
        quitItem.addActionListener(listen);
        newGameItem.addActionListener(listen);

    }

    private class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == quitItem) {
                System.exit(1);
            }

            if (e.getSource() == newGameItem) {
                game.reset();
                panel1.removeAll();
                panel2.removeAll();
                panel3.removeAll();

                String strBdSize = JOptionPane.showInputDialog(null,
                        "Enter in the size of the board: ");
                try {
                    boardSize = Integer.parseInt(strBdSize);
                    if (boardSize <= 3 || boardSize >= 20) {
                        //Jumps to catch statement
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException f) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid input. Using board of size 10.");
                    boardSize = 10;
                }

                String strNumPlayers = JOptionPane.showInputDialog(null,
                        "Enter the number of players: ");
                try {
                    numPlayers = Integer.parseInt(strNumPlayers);
                    if (numPlayers < 2 || numPlayers > 99) {
                        //Jumps to catch statement
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException f) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid input. Using 2 players.");
                    numPlayers = 2;
                }

                String strStartingPlayer = JOptionPane.showInputDialog(null,
                        "Who starts first?");
                try {
                    startingPlayer = Integer.parseInt(strStartingPlayer);
                    if (startingPlayer < 0 || startingPlayer > numPlayers - 1) {
                        //Jumps to catch statement
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException f) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid input. Starting with Player 0.");
                    startingPlayer = 0;
                }
                createScores();

                game = new Surround4Game(boardSize,numPlayers,startingPlayer);
                createBoard();
                add(panel2, BorderLayout.NORTH);
                add(panel1, BorderLayout.CENTER);
                add(panel3, BorderLayout.SOUTH);
                panel1.revalidate();
                panel2.revalidate();
                panel3.revalidate();
                panel1.repaint();
                panel2.repaint();
                panel3.repaint();

            }
            if(e.getSource() == undoButton) {
                if(undoStatus) {
                    game.undo(lastRow, lastCol);
                    game.previousPlayer();
                    board[lastRow][lastCol].setBackground(null);
                    undoStatus = false;
                }
                else {
                    JOptionPane.showMessageDialog(null, "Unable to undo.");
                }
            }

            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[0].length; col++) {
                    if (board[row][col] == e.getSource()) {

                        if (game.select(row, col)) {
                            lastRow = row;
                            lastCol = col;
                            undoStatus = true;

                            game.nextPlayer();
                        }

                        else {
                            JOptionPane.showMessageDialog(null,
                                    "Not a valid square! Pick again.");
                        }
                    }
                }
            }

            displayBoard();

            int winner = game.getWinner();
            if(winner == -2)
                JOptionPane.showMessageDialog(null,"Draw Game!");
            else if (winner != -1) {
                JOptionPane.showMessageDialog(null,
                        "Player " + winner + " Wins!");
                scores[winner] += 1;
                displayScores();
                game = new Surround4Game(boardSize, numPlayers, startingPlayer);
                for (int row = 0; row < boardSize; row++) {
                    for (int col = 0; col < boardSize; col++) {
                        board[row][col].setBackground(null);
                    }
                }
                displayBoard();
                undoStatus = false;
            }
        }
    }


    private void createBoard() {

        board = new JButton[boardSize][boardSize];
        panel1.setLayout(new GridLayout(boardSize,boardSize));

        for (int i = 0; i < boardSize; i++) // rows
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = new JButton("");
                board[i][j].addActionListener(listen);
                panel1.add(board[i][j]);
            }
    }

    private void displayBoard() {

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {

                Cell c = game.getCell(row, col);

                if (c != null) {
                    board[row][col].setText("" + c.getPlayerNumber());
                }
            }
        }
    }

    private void createScores() {
        scoreLabels = new JLabel[numPlayers];
        scores = new int[numPlayers];

        //The number of rows changes to fit the number of players
        //so all scores stay on screen
        int tempRow = numPlayers / 5 + 1;
        panel2.setLayout(new GridLayout(tempRow,1));

        for(int i = 0; i < scores.length; i++)
            scores[i] = 0;

        for(int i = 0; i < scoreLabels.length; i++)
            scoreLabels[i] = new JLabel("Player " + i + ":   " + scores[i] + "     ");

        for(int i = 0; i < scoreLabels.length; i++)
            panel2.add(scoreLabels[i]);
    }

    private void displayScores() {
        for(int i = 0; i < scores.length; i++)
            scoreLabels[i].setText("Player " + i + ":   " + scores[i] + "     ");
    }
}