package surroundpack;
import javax.swing.JButton;

public class BoardPiece extends JButton{
    private int xVal, yVal, owner;

    public void setXVal(int xVal) {
        this.xVal = xVal;
    }

    public void setYVal(int yVal) {
        this.yVal = yVal;
    }

    public void setOwner(int playerNum) {
        this.owner = playerNum;
    }

    public int getXVal() {
        return xVal;
    }

    public int getYVal() {
        return yVal;
    }

    public int getOwner() {
        return owner;
    }
}
