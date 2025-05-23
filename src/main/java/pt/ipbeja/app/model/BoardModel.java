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

    // Callback para avisar a interface gráfica que o modelo mudou
    private Runnable onBoardChanged;

    /**
     * Define o que fazer quando o modelo for alterado (ex: redesenhar o tabuleiro).
     * A UI chama isto para receber notificações do modelo.
     */
    public void setOnBoardChanged(Runnable callback) {
        this.onBoardChanged = callback;
    }

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
     * Attempt to move the monster in the given direction.
     */
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

        Snowball snowball = getSnowballAt(targetRow, targetCol);
        if (snowball != null) {
            int dRow = targetRow - currentRow;
            int dCol = targetCol - currentCol;

            boolean pushed = tryToPushSnowball(targetRow, targetCol, dRow, dCol);

            // Só move o monstro se a bola foi empurrada com sucesso
            if (pushed) {
                Snowball remaining = getSnowballAt(targetRow, targetCol);
                if (remaining == null || (remaining.getRow() != targetRow || remaining.getCol() != targetCol)) {
                    monster.moveTo(targetRow, targetCol);
                }
            }
        } else {
            // Movimento sem bola — pode andar normalmente
            monster.moveTo(targetRow, targetCol);
        }
        checkLevelCompleted();
    }

    /**
     * Attempt to move the snowball in the given direction.
     */
    private boolean tryToPushSnowball(int fromRow, int fromCol, int dRow, int dCol) {
        int toRow = fromRow + dRow;
        int toCol = fromCol + dCol;

        if (!isInsideBoard(toRow, toCol)) return false;
        if (getPositionContent(toRow, toCol) == PositionContent.BLOCK) return false;

        Snowball originalSnowball = getSnowballAt(fromRow, fromCol);
        if (originalSnowball == null) return false;

        Snowball target = getSnowballAt(toRow, toCol);
        if (target != null) {
            return tryToStack(originalSnowball, target, toRow, toCol);
        }

        // Desempilha se for uma combinação
        if (originalSnowball.getStatus() == SnowballStatus.MEDIUM_SMALL) {
            originalSnowball.setStatus(SnowballStatus.MEDIUM);
            Snowball small = new Snowball(toRow, toCol, SnowballStatus.SMALL);
            snowballs.add(small);
        } else if (originalSnowball.getStatus() == SnowballStatus.LARGE_SMALL) {
            originalSnowball.setStatus(SnowballStatus.LARGE);
            Snowball small = new Snowball(toRow, toCol, SnowballStatus.SMALL);
            snowballs.add(small);
        } else if (originalSnowball.getStatus() == SnowballStatus.LARGE_MEDIUM) {
            originalSnowball.setStatus(SnowballStatus.LARGE);
            Snowball medium = new Snowball(toRow, toCol, SnowballStatus.MEDIUM);
            snowballs.add(medium);
        } else {
            // crescimento normal
            if (getPositionContent(toRow, toCol) == PositionContent.SNOW &&
                    (originalSnowball.getStatus() == SnowballStatus.SMALL ||
                            originalSnowball.getStatus() == SnowballStatus.MEDIUM)) {

                originalSnowball.growSnowball();
                board.get(toRow).set(toCol, PositionContent.NO_SNOW);
            }
            originalSnowball.setPosition(toRow, toCol);
            return true;
        }

        return true;
    }


    /**
     * Attempt to stack a snowball over another one in the target position.
     */
    private boolean tryToStack(Snowball moving, Snowball target, int row, int col) {
        SnowballStatus newStatus = switch (target.getStatus()) {
            case LARGE -> switch (moving.getStatus()) {
                case MEDIUM -> SnowballStatus.LARGE_MEDIUM;
                case SMALL -> SnowballStatus.LARGE_SMALL;
                default -> null;
            };
            case MEDIUM -> switch (moving.getStatus()) {
                case SMALL -> SnowballStatus.MEDIUM_SMALL;
                default -> null;
            };
            case LARGE_MEDIUM -> moving.getStatus() == SnowballStatus.SMALL ? SnowballStatus.FULL_SNOWMAN : null;
            default -> null;
        };

        if (newStatus == null) return false;

        target.setStatus(newStatus);
        snowballs.remove(moving);

        if (newStatus == SnowballStatus.FULL_SNOWMAN) {
            board.get(row).set(col, PositionContent.SNOWMAN);
        }

        return true;
    }


    private void checkLevelCompleted() {
        for (Snowball s : snowballs) {
            if (s.getStatus() == SnowballStatus.FULL_SNOWMAN) {
                showLevelCompletedDialog();
                break;
            }
        }
    }

    private void showLevelCompletedDialog() {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("You did it");
            alert.setHeaderText(null);
            alert.setContentText("Good job, you did a full snowman");

            alert.setOnHidden(e -> resetGame());
            alert.show();
        });
    }


    private void resetGame() {
        snowballs.clear();

        // limpa o conteúdo do board (exceto as bordas)
        for (int row = 1; row < board.size() - 1; row++) {
            for (int col = 1; col < board.get(0).size() - 1; col++) {
                board.get(row).set(col, PositionContent.NO_SNOW);
            }
        }

        // adiciona bolas e neve de novo (exemplo simples)
        setPositionContent(7, 4, PositionContent.SNOW);
        setPositionContent(7, 5, PositionContent.SNOW);
        setPositionContent(7, 6, PositionContent.SNOW);
        setPositionContent(7, 7, PositionContent.SNOW);
        setPositionContent(7, 8, PositionContent.SNOW);

        // posições das bolas de neve (depois do reset)
        snowballs.add(new Snowball(5, 3, SnowballStatus.SMALL));
        snowballs.add(new Snowball(5, 4, SnowballStatus.MEDIUM));
        snowballs.add(new Snowball(5, 6, SnowballStatus.LARGE));

        // Reposiciona o monstro no centro
        monster.setPosition(board.size() / 2, board.get(0).size() / 2);

        // Notifica a UI para redesenhar o tabuleiro
        if (onBoardChanged != null) onBoardChanged.run();
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


