package pt.ipbeja.estig.po2.snowman.model;

import pt.ipbeja.estig.po2.snowman.model.interfaces.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Martim Dias - 24290
 * TODO comentar totalmente esta classe E possivelmente separar para uma classe diferente as regras
 */
public class BoardModel {
    private View view;
    public Monster monster;
    private final List<List<PositionContent>> board;
    private final List<Snowball> snowballs = new ArrayList<>();
    final List<String> monsterPositions = new ArrayList<>();
    private final MovementLogger movementLogger;
    private final SnowballManager snowballManager;

    // Histórico dos estados para Undo/Redo
    private final List<GameState> history = new ArrayList<>();
    private int currentStateIndex = -1;

    private String levelName;

    // --- Construtor ---
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

        movementLogger = new MovementLogger(this);
        snowballManager = new SnowballManager(this);  // aqui
    }

    // --- Getters e Setters ---
    public void setView(View view) {
        this.view = view;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    public int getMonsterRow() {
        return this.monster.getRow();
    }

    public int getMonsterCol() {
        return this.monster.getCol();
    }

    public PositionContent getPositionContent(int row, int col) {
        return board.get(row).get(col);
    }

    public void setPositionContent(int row, int col, PositionContent content) {
        board.get(row).set(col, content);
    }

    public List<Snowball> getSnowballs() {
        return this.snowballs;
    }

    public SnowballManager getSnowballManager() {
        return this.snowballManager;
    }

    public int getRowCount() {
        return board.size();
    }

    public int getColCount() {
        return board.get(0).size();
    }

    public int getMoveCount() {
        return monsterPositions.size();
    }

    // --- Movimento e interação ---
    public void moveMonster(MonsterDirections direction) {
        int currentRow = monster.getRow();
        int currentCol = monster.getCol();

        int targetRow = currentRow;
        int targetCol = currentCol;

        switch (direction) {
            case UP -> targetRow--;
            case DOWN -> targetRow++;
            case LEFT -> targetCol--;
            case RIGHT -> targetCol++;
        }

        if (!isInsideBoard(targetRow, targetCol)) return;
        if (getPositionContent(targetRow, targetCol) == PositionContent.BLOCK) return;

        Snowball snowball = snowballManager.getSnowballAt(targetRow, targetCol);
        if (snowball != null) {
            int rowOffSet = targetRow - currentRow;
            int colOffSet = targetCol - currentCol;

            boolean pushed = snowballManager.tryToPushSnowball(targetRow, targetCol, rowOffSet, colOffSet);

            if (pushed) {
                Snowball remaining = snowballManager.getSnowballAt(targetRow, targetCol);
                if (remaining == null || (remaining.getRow() != targetRow || remaining.getCol() != targetCol)) {
                    monster.moveTo(targetRow, targetCol);
                }
            }
        } else {
            monster.moveTo(targetRow, targetCol);
        }
        saveState();
        addMonsterPositionToLog();
        checkLevelCompleted();
    }

    // --- Validação ---
    public boolean canMoveTo(int row, int col) {
        if (!isInsideBoard(row, col)) return false;
        return getPositionContent(row, col) != PositionContent.BLOCK;
    }

    boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < board.size() && col >= 0 && col < board.get(0).size();
    }

    // --- Undo / Redo e estados ---
    public void saveState() {
        while (history.size() > currentStateIndex + 1) {
            history.remove(history.size() - 1);
        }

        GameState snapshot = cloneCurrentState();
        history.add(snapshot);
        currentStateIndex++;
    }

    public void undo() {
        if (currentStateIndex > 0) {
            currentStateIndex--;
            applyState(history.get(currentStateIndex));
            if (view != null) {
                view.updateBoard();
                view.resetUI();
            }
        }
    }

    public void redo() {
        if (currentStateIndex < history.size() - 1) {
            currentStateIndex++;
            applyState(history.get(currentStateIndex));
            if (view != null) {
                view.updateBoard();
                view.resetUI();
            }
        }
    }

    private GameState cloneCurrentState() {
        List<List<PositionContent>> boardCopy = new ArrayList<>();
        for (List<PositionContent> row : board) {
            boardCopy.add(new ArrayList<>(row));
        }

        List<Snowball> snowballCopy = new ArrayList<>();
        for (Snowball s : snowballs) {
            snowballCopy.add(new Snowball(s.getRow(), s.getCol(), s.getStatus()));
        }

        Monster clonedMonster = new Monster(monster.getRow(), monster.getCol());

        return new GameState(boardCopy, snowballCopy, clonedMonster);
    }

    private void applyState(GameState state) {
        board.clear();
        for (List<PositionContent> row : state.board()) {
            board.add(new ArrayList<>(row));
        }

        snowballs.clear();
        snowballs.addAll(state.snowballs());

        monster = new Monster(state.monster().getRow(), state.monster().getCol());
    }

    // --- Reset do jogo ---
    public void resetGame() {
        snowballs.clear();

        for (int row = 1; row < board.size() - 1; row++) {
            for (int col = 1; col < board.get(0).size() - 1; col++) {
                board.get(row).set(col, PositionContent.NO_SNOW);
            }
        }

        monster.setPosition(board.size() / 2, board.get(0).size() / 2);

        if (view != null) view.updateBoard();
        if (view != null) view.resetUI();

        monsterPositions.clear();
    }

    // --- Verificação de estado do jogo ---
    private void checkLevelCompleted() {
        boolean hasFullSnowman = false;
        int smallCount = 0;
        int largeCount = 0;

        for (Snowball s : snowballs) {
            switch (s.getStatus()) {
                case FULL_SNOWMAN -> hasFullSnowman = true;
                case SMALL, MEDIUM_SMALL, LARGE_SMALL -> smallCount++;
                case LARGE -> largeCount++;
            }
        }

        if (hasFullSnowman) {
            showLevelCompletedDialog();
        } else if (smallCount == 0 || largeCount >= 2) {
            showUnsolvableDialog();
        }
    }

    private void showUnsolvableDialog() {
        if (view != null) {
            view.showUnsolvableDialog();
        }
    }

    private void showLevelCompletedDialog() {
        if (view != null) {
            view.showLevelCompletedDialog();
        }
    }


    // --- Log e ficheiros ---
    public void addMonsterPositionToLog() {
        movementLogger.addMonsterPositionToLog();
    }

    public void saveMonsterPositionsToFile() {
        movementLogger.saveMonsterPositionsToFile();
    }
}
