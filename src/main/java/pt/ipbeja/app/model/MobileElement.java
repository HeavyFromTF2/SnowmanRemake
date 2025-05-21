package pt.ipbeja.app.model;

/**
 * Abstract class for any element that has a position on the board.
 */
public abstract class MobileElement {
    protected int row, col;

    public MobileElement(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
