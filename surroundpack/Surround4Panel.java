/**
 * Surround4Panel.java
 * 
 * @version 4.17.22
 * @author  Justin Jahlas, 
 * 			Brennan Luttrel, 
 * 			Munu Bhai, 
 * 			Cole Blunt, 
 * 			Noah Meyers
 */

package surroundpack;
import java.awt.*;
import javax.swing.*;

/********************************************************************
 * Game GUI Panel
 *******************************************************************/
public class Surround4Panel extends JPanel {

    private BoardPiece[][] board;

    // private static boolean boardCreated;

    // public Surround4Panel() {
    //     boardCreated = true;
    // }

    private JPanel panel1, panel2, panel3;
    private int 
        boardSize, numPlayers, startingPlayer, 
        lastRow, lastCol;

    private Surround4Game game;

    private JLabel[] scoreLabels;
    private static int[] scores;

    private boolean selectedTile;

    private Color[] playerColors = {Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.ORANGE};

    public void initBoard() {

        setLayout(new BorderLayout());
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();

        createScores();

        //game = new Surround4Game(boardSize,numPlayers,startingPlayer);
        createBoard();

        
        add(panel2, BorderLayout.NORTH);
        // if (boardCreated)
        add(panel1, BorderLayout.CENTER);
        add(panel3, BorderLayout.SOUTH);
    }

    private void createBoard() {

        board = new BoardPiece[boardSize][boardSize];
        panel1.setLayout(new GridLayout(boardSize,boardSize));

        for (int i = 0; i < boardSize; i++) {// rows
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = new BoardPiece();
                board[i][j].setYVal(i);
                board[i][j].setXVal(j);
                panel1.add(board[i][j]);
            }
        }
    }

    public BoardPiece[][] getBoard() {
        return board;
    }

    public int[] getLastCoords() {
        return new int[]{lastRow, lastCol};
    }

    public void resetBoard() {
        JButton temp = new JButton();

        // reset colors
        for (int i = 0; i < boardSize; i++) {// rows
            for (int j = 0; j < boardSize; j++) {
                board[i][j].setBackground(temp.getBackground());
            }
        }
    }

    private void displayBoard() {

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {

                Cell c = game.getCell(row, col);

                if (c != null) {
                    board[row][col].setBackground(playerColors[c.getPlayerNumber()]);
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
            scoreLabels[i].setForeground(playerColors[i]);
    }

    public void setBoardSize(int size) {
        boardSize = size;
    }

    public void setPlayers(int players) {
        numPlayers = players;
    }

    public boolean getSelectedTile() {
        return selectedTile;
    }

    public void setTile(int row, int col, int player) {
        board[row][col].setBackground(playerColors[player]);
        board[row][col].setForeground(playerColors[player]);

        // board[row][col].doClick();
        // board[row][col].setOwner(player);
    }

    public void setHasSelectedTile(boolean val) {
        selectedTile = val;
    }
}