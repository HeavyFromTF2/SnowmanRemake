package pt.ipbeja.estig.po2.snowman.model;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MovementLogger {

    private final BoardModel boardModel;

    public MovementLogger(BoardModel boardModel) {
        this.boardModel = boardModel;
    }

    public void addMonsterPositionToLog() {
        char columnLetter = (char) ('A' + boardModel.getMonsterCol());
        String pos = "(" + boardModel.getMonsterRow() + "," + columnLetter + ")";
        boardModel.monsterPositions.add(pos);  // Para isto funcionar, tem que mudar o campo monsterPositions para package-private ou criar getter/setter
    }

    public void saveMonsterPositionsToFile() {
        String folderName = "snowman_files";
        String filename = folderName + "/" + generateFilename();

        List<String> lines = new ArrayList<>();
        lines.add("MAP:");
        lines.addAll(getMapLines());

        lines.add("LEVEL PLAYED: " + boardModel.getLevelName());
        lines.add("MOVEMENT LOG OF MONSTER:");
        lines.add(String.join(" ", boardModel.monsterPositions));
        lines.add("TOTAL MOVEMENTS: " + boardModel.monsterPositions.size());
        lines.add("SNOWMAN POSITION: " + findSnowmanPosition());

        writeLinesToFile(filename, lines);
    }

    private String generateFilename() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "snowman" + now.format(formatter) + ".txt";
    }

    private List<String> getMapLines() {
        List<String> map = new ArrayList<>();
        for (int row = 0; row < boardModel.getRowCount(); row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < boardModel.getColCount(); col++) {
                PositionContent c = boardModel.getPositionContent(row, col);
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
        for (int row = 0; row < boardModel.getRowCount(); row++) {
            for (int col = 0; col < boardModel.getColCount(); col++) {
                if (boardModel.getPositionContent(row, col) == PositionContent.SNOWMAN) {
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
}
