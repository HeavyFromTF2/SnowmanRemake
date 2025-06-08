package pt.ipbeja.estig.po2.snowman.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Martim Dias - 24290
 *
 * Classe responsável por registar os movimentos do monstro e guardar o estado
 * do jogo, incluindo o mapa, nível e posição do boneco de neve, num ficheiro de texto.
 */
public class MovementLogger {

    // Referência ao modelo do tabuleiro do jogo.
    private final BoardModel boardModel;

    /**
     * Constrói um MovementLogger para o modelo do tabuleiro especificado.
     *
     * @param boardModel instância de BoardModel que representa o estado do jogo
     */
    public MovementLogger(BoardModel boardModel) {
        this.boardModel = boardModel;
    }

    /**
     * Adiciona a posição atual do monstro ao registo de movimentos do tabuleiro.
     * A posição é formatada como "(linha, letraColuna)".
     */
    public void addMonsterPositionToLog() {
        char colLetter = (char) ('A' + boardModel.getMonsterCol());
        String pos = "(" + boardModel.getMonsterRow() + "," + colLetter + ")";
        // Nota: o campo 'monsterPositions' deve ser package-private ou acessível via getter/setter
        boardModel.monsterPositions.add(pos);
    }

    /**
     * Guarda o registo de movimentos do monstro juntamente com o mapa atual,
     * nome do nível, total de movimentos e posição do boneco de neve num ficheiro
     * de texto com nome baseado no timestamp atual. O ficheiro é guardado na pasta "snowman_files".
     */
    public void saveMonsterPositionsToFile() {
        String folderName = "snowman_files";
        String filename = folderName + "/" + generateFilename();

        List<String> lines = new ArrayList<>();
        lines.add("MAP:");
        lines.addAll(getMapLines());

        lines.add("LEVEL PLAYED: " + boardModel.getLevelName());
        lines.add("MONSTER MOVEMENT LOG:");
        lines.add(String.join(" ", boardModel.monsterPositions));
        lines.add("TOTAL MOVEMENTS (SCORE): " + boardModel.monsterPositions.size());
        lines.add("SNOWMAN POSITION: " + findSnowmanPosition());

        writeLinesToFile(filename, lines);
    }

    /**
     * Gera um nome para o ficheiro de log com base na data e hora atual,
     * com o formato "snowmanYYYYMMDDHHMMSS.txt".
     *
     * @return o nome do ficheiro com timestamp
     */
    private String generateFilename() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "snowman" + now.format(formatter) + ".txt";
    }

    /**
     * Constroi uma lista de strings que representam o mapa atual do jogo,
     * onde cada carácter simboliza o conteúdo de uma posição no tabuleiro:
     * '#' para BLOCO, '*' para NEVE, '⛄' para BONECO_DE_NEVE, e '.' para SEM_NEVE.
     *
     * @return lista de linhas do mapa como strings
     */
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

    /**
     * Procura e devolve a posição atual do boneco de neve no tabuleiro,
     * formatada como "(linha, letraColuna)", onde as linhas começam em 1.
     * Caso não seja encontrado, devolve uma string indicativa.
     *
     * @return posição formatada do boneco de neve ou diz se não foi feito
     */
    private String findSnowmanPosition() {
        for (int row = 0; row < boardModel.getRowCount(); row++) {
            for (int col = 0; col < boardModel.getColCount(); col++) {
                if (boardModel.getPositionContent(row, col) == PositionContent.SNOWMAN) {
                    char colLetter = (char) ('A' + col);
                    return "(" + (row + 1) + "," + colLetter + ")";
                }
            }
        }
        return "(No snowman found)";
    }

    /**
     * Escreve as linhas fornecidas num ficheiro de texto com o nome especificado.
     * Cada string da lista representa uma linha no ficheiro.
     * Caso ocorra uma IOException, imprime o stack trace.
     *
     * @param filename o caminho do ficheiro onde escrever as linhas
     * @param lines as linhas de texto a escrever no ficheiro
     */
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