package pt.ipbeja.app.model;

import pt.ipbeja.app.model.interfaces.View;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


// TODO onde foi usado chatgpt terá que ser comentado

/**
 * Martim Dias - 24290
 * TODO comentar totalmente esta classe E possivelmente separar para uma classe diferente as regras
 */
public class BoardModel {
    private View view;

    public Monster monster;
    private final List<List<PositionContent>> board;
    private final List<Snowball> snowballs = new ArrayList<>();
    private final List<String> monsterPositions = new ArrayList<>();

    private String levelName;

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setView(View view) {
        this.view = view;
    }

    /**
     * Construtor de tabuleiro padrão para testes e debug manuais.
     * Cria um tabuleiro com bordas de BLOCK e interior NO_SNOW.
     * É FOCADO EM TESTES, JÁ QUE OS NIVEIS CARREGAM DOS FICHEIROS TXT (por decidir se mantenho)
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
     * POR VER: Mover bolas na parede da molhongo (incrementa o move counter. ver isso)
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
        addMonsterPositionToLog();
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

        return handleSnowballMovement(originalSnowball, toRow, toCol);
    }


    private boolean handleSnowballMovement(Snowball snowball, int toRow, int toCol) {
        Snowball actualBall = snowball;

        switch (snowball.getStatus()) {
            case MEDIUM_SMALL -> {
                snowball.setStatus(SnowballStatus.MEDIUM);
                actualBall = new Snowball(toRow, toCol, SnowballStatus.SMALL);
                snowballs.add(actualBall);
            }
            case LARGE_SMALL -> {
                snowball.setStatus(SnowballStatus.LARGE);
                actualBall = new Snowball(toRow, toCol, SnowballStatus.SMALL);
                snowballs.add(actualBall);
            }
            case LARGE_MEDIUM -> {
                snowball.setStatus(SnowballStatus.LARGE);
                actualBall = new Snowball(toRow, toCol, SnowballStatus.MEDIUM);
                snowballs.add(actualBall);
            }
            default -> {
                actualBall.setPosition(toRow, toCol);
            }
        }

        // Agora verifica se o conteúdo é SNOW e a bola pode crescer
        if (getPositionContent(toRow, toCol) == PositionContent.SNOW &&
                (actualBall.getStatus() == SnowballStatus.SMALL || actualBall.getStatus() == SnowballStatus.MEDIUM)) {
            actualBall.growSnowball();
            board.get(toRow).set(toCol, PositionContent.NO_SNOW);
        }

        return true;
    }


    /**
     * Attempt to stack a snowball over another one in the target position.
     */
    private boolean tryToStack(Snowball moving, Snowball target, int row, int col) {
        // Se a bola a mover é pequena ou média e o destino tem neve,
        // a bola cresce consumindo a neve antes de empilhar
        if (getPositionContent(row, col) == PositionContent.SNOW &&
                (moving.getStatus() == SnowballStatus.SMALL || moving.getStatus() == SnowballStatus.MEDIUM)) {
            moving.growSnowball();
            board.get(row).set(col, PositionContent.NO_SNOW);
        }

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


    /**
     * Check if the level is completed or if it becomes impossible to complete.
     */
    private void checkLevelCompleted() {
        boolean hasFullSnowman = false;
        int smallCount = 0;
        int largeCount = 0;

        for (Snowball s : snowballs) {
            switch (s.getStatus()) {
                case FULL_SNOWMAN -> hasFullSnowman = true;
                case SMALL -> smallCount++;
                case MEDIUM_SMALL -> {
                    smallCount++;
                }
                case LARGE_SMALL -> {
                    smallCount++;
                    // largeCount++;
                }
                case LARGE -> largeCount++;
            }
        }
        // If the snowman is full, end game
        if (hasFullSnowman) {
            showLevelCompletedDialog();
        }
        // If no small balls remain or there are 2 or more large balls, game is impossible
        else if (smallCount == 0 || largeCount >= 2) {
            showUnsolvableDialog();
        }
    }

    /**
     * Show a warning dialog if the game can no longer be completed,
     * then reset the game after user clicks OK.
     */
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

    // TODO retiro metodos destes? na teoria ja n precisa resetar, pq o jogo volta ao menu
    public void resetGame() {
        snowballs.clear();

        // Limpa o conteúdo do board (exceto as bordas)
        for (int row = 1; row < board.size() - 1; row++) {
            for (int col = 1; col < board.get(0).size() - 1; col++) {
                board.get(row).set(col, PositionContent.NO_SNOW);
            }
        }

        // Reposiciona o monstro no centro
        monster.setPosition(board.size() / 2, board.get(0).size() / 2);

        // Notifica a UI para redesenhar o tabuleiro
        if (view != null) view.updateBoard();
        // Notifica a UI do reset dos movimentos do monstro
        if (view != null) view.resetUI();

        // Reset do contador
        monsterPositions.clear();
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



    private String generateFilename() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "snowman" + now.format(formatter) + ".txt";
    }

    private List<String> getMapLines() {
        List<String> map = new ArrayList<>();
        for (int row = 0; row < board.size(); row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < board.get(0).size(); col++) {
                PositionContent c = getPositionContent(row, col);
                char symbol = switch (c) {
                    case BLOCK -> '#';
                    case SNOW -> '*';
                    case SNOWMAN -> '⛄';
                    case NO_SNOW -> '.';
                };
                line.append(symbol).append(' ');
            }
            map.add(line.toString());
        }
        return map;
    }

    private String findSnowmanPosition() {
        for (int row = 0; row < board.size(); row++) {
            for (int col = 0; col < board.get(0).size(); col++) {
                if (getPositionContent(row, col) == PositionContent.SNOWMAN) {
                    char columnLetter = (char) ('A' + col);
                    return "(" + (row + 1) + "," + columnLetter + ")";
                }
            }
        }
        return "(not found snowman)";
    }

    private void writeLinesToFile(String filename, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addMonsterPositionToLog() {
        // Regista a posição atual do monstro no formato (linha, coluna)
        char columnLetter = (char) ('A' + monster.getCol());
        String pos = "(" + monster.getRow() + "," + columnLetter + ")";
        monsterPositions.add(pos);
    }

    public void saveMonsterPositionsToFile() {
        String folderName = "snowman_files";
        String filename = folderName + "/" + generateFilename();

        List<String> lines = new ArrayList<>();
        lines.add("MAP:");
        lines.addAll(getMapLines());

        lines.add("MOVEMENT LOG OF MONSTER:");
        lines.add(String.join(" ", monsterPositions));  // todos na mesma linha
        lines.add("TOTAL MOVEMENTS: " + monsterPositions.size());
        lines.add("SNOWMAN POSITION: " + findSnowmanPosition());

        writeLinesToFile(filename, lines);
    }


    public int getRowCount() {
        return board.size(); // número de linhas (linhas na lista)
    }

    public int getColCount() {
        return board.get(0).size(); // número de colunas (tamanho da primeira linha)
    }

    public void setMonster(Monster monster) { this.monster = monster; }

}


