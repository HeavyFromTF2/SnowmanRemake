package pt.ipbeja.app.model;


import java.util.ArrayList;
import java.util.List;

/**
 * The game board model, holding a grid of PositionContent.
 */
public class BoardModel {
    private final List<List<PositionContent>> board;
    private Monster monster;

    /**
     * Create a board with the given dimensions, all positions NO_SNOW.
     */
    public BoardModel(int rows, int cols) {
        if (rows <= 0 || cols <= 0) throw new IllegalArgumentException("Rows and cols must be positive");
        board = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            List<PositionContent> row = new ArrayList<>();
            for (int c = 0; c < cols; c++) {
                row.add(PositionContent.NO_SNOW);
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
     * Get the monster object.
     */
    public Monster getMonster() {
        return this.monster;
    }

    /**
     * Get the PositionContent at a specific location.
     */
    public PositionContent getPositionContent(int row, int col) {
        return board.get(row).get(col);
    }

    /**
     * Attempt to move the monster in the given direction.
     */
    public void moveMonster(MonsterDirections direction) {
        int newRow = monster.getRow();
        int newCol = monster.getCol();

        switch (direction) {
            case UP -> newRow--;
            case DOWN -> newRow++;
            case LEFT -> newCol--;
            case RIGHT -> newCol++;
        }

        if (isInsideBoard(newRow, newCol)) {
            monster.moveTo(newRow, newCol);
        }
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


