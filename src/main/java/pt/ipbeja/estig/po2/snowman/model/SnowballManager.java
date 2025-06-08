package pt.ipbeja.estig.po2.snowman.model;

import java.util.List;

/**
 * Martim Dias - 24290
 *
 * Classe responsável pela gestão das bolas de neve no tabuleiro do jogo,
 * inclui movimentação, empurrar e empilhações de bolas de neve.
 */

public class SnowballManager {

    /** Referência ao modelo do tabuleiro do jogo. */
    private final BoardModel boardModel;

    /** Lista de bolas de neve presentes no tabuleiro. */
    private final List<Snowball> snowballs;

    /**
     * Constrói um gestor de bolas de neve associado ao modelo do tabuleiro dado.
     *
     * @param boardModel o modelo do tabuleiro onde as bolas de neve estão localizadas
     */
    public SnowballManager(BoardModel boardModel) {
        this.boardModel = boardModel;
        this.snowballs = boardModel.getSnowballs();
    }

    /**
     * Obtém a bola de neve na posição especificada (linha, coluna).
     *
     * @param row índice da linha no tabuleiro
     * @param col índice da coluna no tabuleiro
     * @return a bola de neve na posição indicada, ou null se não existir bola de neve nessa posição
     */
    public Snowball getSnowballAt(int row, int col) {
        for (Snowball ball : snowballs) {
            if (ball.getRow() == row && ball.getCol() == col) return ball;
        }
        return null;
    }

    /**
     * Tenta empurrar uma bola de neve da posição (fromRow, fromCol) para a posição
     * obtida aplicando o offset (rowOffSet, colOffSet).
     * Valida limites do tabuleiro e obstáculos, e trata a lógica de empilhamento
     * ou movimento da bola.
     *
     * @param fromRow linha da bola de neve a mover
     * @param fromCol coluna da bola de neve a mover
     * @param rowOffSet deslocamento em linhas para o movimento
     * @param colOffSet deslocamento em colunas para o movimento
     * @return true se o movimento ou empilhamento foi bem-sucedido, false caso contrário
     */
    public boolean tryToPushSnowball(int fromRow, int fromCol, int rowOffSet, int colOffSet) {
        int toRow = fromRow + rowOffSet;
        int toCol = fromCol + colOffSet;

        if (!boardModel.isInsideBoard(toRow, toCol)) return false;
        if (boardModel.getPositionContent(toRow, toCol) == PositionContent.BLOCK) return false;

        Snowball originalSnowball = getSnowballAt(fromRow, fromCol);
        if (originalSnowball == null) return false;

        Snowball target = getSnowballAt(toRow, toCol);
        if (target != null) {
            return tryToStack(originalSnowball, target, toRow, toCol);
        }

        return handleSnowballMovement(originalSnowball, toRow, toCol);
    }

    /**
     * Trata o movimento de uma bola de neve para uma nova posição.
     * Dependendo do estado da bola, pode criar uma nova bola em crescimento,
     * aumentar o tamanho da bola atual, ou simplesmente mudar a sua posição.
     * Também verifica e processa a interação com neve na nova posição.
     *
     * @param snowball bola de neve a mover
     * @param toRow linha para onde a bola deve ser movida
     * @param toCol coluna para onde a bola deve ser movida
     * @return sempre retorna true pois o movimento é efetuado se chegar a este mét0do
     */
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
            default -> actualBall.setPosition(toRow, toCol);
        }

        if (boardModel.getPositionContent(toRow, toCol) == PositionContent.SNOW &&
                (actualBall.getStatus() == SnowballStatus.SMALL || actualBall.getStatus() == SnowballStatus.MEDIUM)) {
            actualBall.growSnowball();
            boardModel.setPositionContent(toRow, toCol, PositionContent.NO_SNOW);
        }

        return true;
    }

    /**
     * Tenta empilhar uma bola de neve em cima de outra, criando estados compostos.
     * Se for possível combinar os estados, a bola alvo é atualizada e a bola em movimento removida.
     * Também processa o crescimento da bola em movimento se houver neve na posição.
     *
     * @param moving bola de neve que está a ser movida
     * @param target bola de neve que está a ser empilhada
     * @param row linha da posição alvo
     * @param col coluna da posição alvo
     * @return true se o empilhamento foi realizado com sucesso, false caso contrário
     */
    private boolean tryToStack(Snowball moving, Snowball target, int row, int col) {
        if (boardModel.getPositionContent(row, col) == PositionContent.SNOW &&
                (moving.getStatus() == SnowballStatus.SMALL || moving.getStatus() == SnowballStatus.MEDIUM)) {
            moving.growSnowball();
            boardModel.setPositionContent(row, col, PositionContent.NO_SNOW);
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
            boardModel.setPositionContent(row, col, PositionContent.SNOWMAN);
        }

        return true;
    }
}
