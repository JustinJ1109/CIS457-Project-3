//package surroundpack;

/**
 * Main handler that handles all the logic behind cell interactions
 */
public class Surround4Game {
    public Cell[][] board;
    private int player;
    private int row, col;

    private int[] remainingPlayers;

    private int boardSize;
    private int numPlayers;
    private int[] playerStatus;


    /******************************************************************
     * Default constructor for Surround4Game Class. Defaults board size
     * to 10 and max players of 2 starting with player 0
     *
     *****************************************************************/
    public Surround4Game() {
        //super();
        board = new Cell[10][10];
        this.player = 0;
        remainingPlayers = new int[]{0, 1};
    }

    /******************************************************************
     * Constructor for Surround4Game class that sets board size,
     * players, and player one to specified.
     *
     * @param boardS Board size in one direction (square board).
     * @param numP Total number of players participating in game
     * @param stP First player to begin the game.
     *****************************************************************/
    public Surround4Game(int boardS, int numP, int stP) {
        this.boardSize = boardS;
        this.numPlayers = numP;
        this.player = stP;

        // Creates the 2D array of Cell objects using boardSize
        board = new Cell[boardSize][boardSize];

        // Sets all player's status to "in the game"
        playerStatus = new int[numPlayers];
        for (int i = 0; i < playerStatus.length; i++)
            playerStatus[i] = 1;
    }

    /******************************************************************
     * Resets the board to default when called. Sets all tiles to null
     *****************************************************************/
    public void reset() {
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                board[r][c] = null;
            }
        }
    }

    /******************************************************************
     * Retrieves the current cell
     * @param row row of the current cell.
     * @param col column of the current cell.
     * @return returns a Cell at the row and column
     *****************************************************************/
    public Cell getCell(int row, int col) {

        return board[row][col];
    }

    /******************************************************************
     * Retrieves the current player
     * @return returns the current player
     *****************************************************************/
    public int getCurrentPlayer() {
        return player;
    }

    /******************************************************************
     * Rotates to next player in order when called. Resets to 0 once
     * last player is called.
     ****************************************************************
     * @return*/
    public int nextPlayer() {
        do {
            player = player + 1;
            if (player == numPlayers)
                player = 0;
        } while (playerStatus[player] == -1);
        return 0;
    }

    /******************************************************************
     * Determines if it is possible to select the tile the user wishes
     * to take.
     *
     * @param row current row of Cell.
     * @param col current column of Cell.
     * @return returns true if the tile is not taken. False otherwise.
     *****************************************************************/
    public boolean select(int row, int col) {
        if (board[row][col] == null && getWinner() == -1) { //TODO: don't select any more tiles after game is complete
            Cell c = new Cell(player);
            board[row][col] = c;

            this.row = row;
            this.col = col;

            return true;
        } else
            return false;
    }

    /******************************************************************
     * Determines if a win is valid.
     *
     * @return returns the last player to play when a tile gets
     * surrounded. else will return -1 if no winner is determined.
     * if a player wins the game but also is surrounded in doing so,
     * returns -2.
     *****************************************************************/
    public int getWinner() {
        int winner = -1;
        if (scanBoard() != -1) {
            winner = getCurrentPlayer() - 1;
            if (winner < 0)
                winner = numPlayers - 1;
        }
        if (winner != -1 && scanBoard() == winner)
            return -2;

        return winner;
    }

    /******************************************************************
     * Scans the entire board for any tiles that may be surrounded
     *
     * @return returns player number that was surrounded
     *****************************************************************/
    public int scanBoard() {
        int playerSurrounded = -1;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {

                for (int r1 = 0; r1 < board.length; r1++) //TODO: change var names, at request of prof
                    for (int c1 = 0; c1 < board.length; c1++)
                        if (board[r1][c1] != null)
                            board[r1][c1].setSeen(false);

                if (board[r][c] != null) {
                    if (isSurrounded(r, c)) {
                        playerSurrounded = board[r][c].getPlayerNumber();
                        return playerSurrounded;
                    }
                }
            }
        }
        return -1;
    }

    /******************************************************************
     * Checks everything surrounding the cell that gets called and
     * determines if a cell is surrounded
     *
     * @param row the row of the cell to be checked
     * @param col the col of the cell to be checked
     * @return returns true if the cell is surrounded, else false.
     *****************************************************************/
    public boolean isSurrounded(int row, int col) {

        //set cell to seen
        board[row][col].setSeen(true);
            

        if ((row < board.length &&(board[row + 1][col] == null || board[row - 1][col] == null) ||
        (col < board[0].length && (board[row][col + 1] == null || board[row][col - 1] == null)) {
            return false;
        } else {
            // if there is a friendly tile touching that hasn't been looked at, re-call method on that tile
            if (board[row + 1][col].getPlayerNumber() == board[row][col].getPlayerNumber() &&
                    !board[row + 1][col].isSeen()) {

                if (!isSurrounded(row + 1, col))
                    return false;
            }
            if (board[row - 1][col].getPlayerNumber() == board[row][col].getPlayerNumber() &&
                    !board[row - 1][col].isSeen()) {

                if (!isSurrounded(row - 1, col))
                    return false;
            }

            if (board[row][col + 1].getPlayerNumber() == board[row][col].getPlayerNumber() &&
                    !board[row][col + 1].isSeen()) {

                if (!isSurrounded(row, col + 1))
                    return false;
            }

            if (board[row][col - 1].getPlayerNumber() == board[row][col].getPlayerNumber() &&
                    !board[row][col - 1].isSeen()) {

                if (!isSurrounded(row, col - 1))
                    return false;
            }
        }
        return true;
    }

    public void undo(int row, int col) {
        board[row][col] = null;
    }

    public int previousPlayer() {
        do {
            player = player - 1;
            if (player == -1)
                player = numPlayers - 1;
        } while (playerStatus[player] == -1);
        return player;
    }
}
