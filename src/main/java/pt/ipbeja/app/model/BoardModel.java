package pt.ipbeja.app.model;


import java.util.ArrayList;
import java.util.List;

/**
 * The game board model, holding a grid of PositionContent.
 */
public class BoardModel {

    private final Monster monster;
    private final List<List<PositionContent>> board;
    private final List<Snowball> snowballs = new ArrayList<>();

    /**
     * Create a board with the given dimensions, all positions NO_SNOW.
     */
    public BoardModel(int rows, int cols) {
        if (rows <= 0 || cols <= 0) throw new IllegalArgumentException("Rows and cols must be positive...");
        board = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            List<PositionContent> row = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    row.add(PositionContent.BLOCK); // borda
                } else {
                    row.add(PositionContent.NO_SNOW); // interior
                }
            }
            board.add(row);
        }

        // Place the monster in the center of the board by default
        int startRow = rows / 2;
        int startCol = cols / 2;
        this.monster = new Monster(startRow, startCol);
    }

    /**
     * Get the row position of the monster.
     */
    public int getMonsterRow() {
        return this.monster.getRow();
    }

    /**
     * Get the column position of the monster.
     */
    public int getMonsterCol() {
        return this.monster.getCol();
    }


    /**
     * Get the PositionContent at a specific location.
     */
    public PositionContent getPositionContent(int row, int col) {
        return board.get(row).get(col);
    }

    /**
     * Get the snowballs list
     */



    /**
     * Attempt to move the monster in the given direction.
     */
    public void moveMonster(MonsterDirections direction) {
        int currentRow = monster.getRow();
        int currentCol = monster.getCol();

        int targetRow = currentRow;
        int targetCol = currentCol;

        if (direction == MonsterDirections.UP) targetRow--;
        else if (direction == MonsterDirections.DOWN) targetRow++;
        else if (direction == MonsterDirections.LEFT) targetCol--;
        else if (direction == MonsterDirections.RIGHT) targetCol++;

        if (!isInsideBoard(targetRow, targetCol)) return;
        if (getPositionContent(targetRow, targetCol) == PositionContent.BLOCK) return;

        Snowball snowball = getSnowballAt(targetRow, targetCol);
        if (snowball != null) {
            int dirRow = targetRow - currentRow;
            int dirCol = targetCol - currentCol;

            boolean pushed = tryToPushSnowball(targetRow, targetCol, dirRow, dirCol);
            if (!pushed) return;
        }

        monster.moveTo(targetRow, targetCol);
    }

    /**
     * Attempt to move the snowball in the given direction.
     */
    private boolean tryToPushSnowball(int ballRow, int ballCol, int dirRow, int dirCol) {
        int targetRow = ballRow + dirRow;
        int targetCol = ballCol + dirCol;

        if (!isInsideBoard(targetRow, targetCol)) return false;
        if (getPositionContent(targetRow, targetCol) == PositionContent.BLOCK) return false;
        if (getSnowballAt(targetRow, targetCol) != null) return false;

        Snowball ball = getSnowballAt(ballRow, ballCol);
        if (ball == null) return false;

        if (getPositionContent(targetRow, targetCol) == PositionContent.SNOW) {
            ball.growSnowball();
            board.get(targetRow).set(targetCol, PositionContent.NO_SNOW);
        }

        ball.setPosition(targetRow, targetCol);
        return true;
    }


    public Snowball getSnowballAt(int row, int col) {
        for (Snowball ball : snowballs) {
            if (ball.getRow() == row && ball.getCol() == col) return ball;
        }
        return null;
    }


    public List<Snowball> getSnowballs() {
        return this.snowballs;
    }


    public void setPositionContent(int row, int col, PositionContent content) {
        board.get(row).set(col, content);
    }


    /**
     * Check if the monster can move to a given position (must be inside board and not a BLOCK).
     */
    public boolean canMoveTo(int row, int col) {
        if (!isInsideBoard(row, col)) return false;
        return getPositionContent(row, col) != PositionContent.BLOCK;
    }


    /**
     * Check if the given position is within the board boundaries.
     */
    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < board.size() && col >= 0 && col < board.get(0).size();
    }


    public int getRowCount() {
        return board.size(); // número de linhas (linhas na lista)
    }

    public int getColCount() {
        return board.get(0).size(); // número de colunas (tamanho da primeira linha)
    }
}


