package pt.ipbeja.estig.po2.snowman.model;

import java.util.List;

/**
 * Martim Dias - 24290
 *
 * Representa o estado imutável do jogo num dado momento específico.
 *
 * Contém o conteúdo do tabuleiro, a lista das bolas de neve e a posição do monstro.
 * Usado para salvar e restaurar o estado atual do jogo, por exemplo, em funcionalidades de undo/redo.
 *
 * @param board Tabuleiro com o conteúdo de cada célula.
 * @param snowballs Lista das bolas de neve com seus estados e posições.
 * @param monster O monstro com sua posição atual.
 */
public record GameState(
        List<List<PositionContent>> board,
        List<Snowball> snowballs,
        Monster monster
) {}