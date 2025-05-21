package pt.ipbeja.app.model;

/**
 * Represents a snowball in the game grid, with a specific position and status (size or stack).
 * A snowball can grow in size or combine with others to form a snowman.
 */

public class Snowball extends MobileElement {

    private SnowballStatus status;

    /**
     * Constructs a Snowball.
     *
     * @param row    the row position on the board
     * @param col    the column position on the board
     * @param status the initial status (size or combination)
     */
    public Snowball(int row, int col, SnowballStatus status) {
        super(row, col);
        this.status = status;
    }

    /**
     * Returns the current status of the snowball.
     */
    public SnowballStatus getStatus() {
        return this.status;
    }

    /**
     * Updates the snowball's status.
     *
     * @param status the new status
     */
    public void setStatus(SnowballStatus status) {
        this.status = status;
    }

    /**
     * Grows the snowball if it's SMALL or MEDIUM and it's moved over SNOW.
     * Does nothing if it's already LARGE or a combined type.
     */
    public void grow() {
        switch (this.status) {
            case SMALL:
                this.status = SnowballStatus.MEDIUM;
                break;
            case MEDIUM:
                this.status = SnowballStatus.LARGE;
                break;
            default:
                // Do nothing if already LARGE or stacked
                break;
        }
    }

    /**
     * Checks if this snowball can be stacked on the base snowball.
     *
     * @param base the snowball on which this one will be stacked
     * @return true if stacking is valid according to size
     */
    
    public boolean canBeStackedOn(Snowball base) {
        return (this.status == SnowballStatus.SMALL && base.status == SnowballStatus.MEDIUM) ||
                (this.status == SnowballStatus.MEDIUM && base.status == SnowballStatus.LARGE);
    }

    /**
     * Checks if this snowball is a complete snowman.
     */
    public boolean isCompleteSnowman() {
        return this.status == SnowballStatus.FULL_SNOWMAN;
    }


    @Override
    public String toString() {
        return "Snowball{" +
                "status=" + status +
                ", row=" + getRow() +
                ", col=" + getCol() +
                '}';
    }
}
