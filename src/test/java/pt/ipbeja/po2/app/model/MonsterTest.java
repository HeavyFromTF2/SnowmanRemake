package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.model.Monster;
import pt.ipbeja.estig.po2.snowman.model.MonsterDirections;
import pt.ipbeja.estig.po2.snowman.model.PositionContent;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Martim Dias - 24290
 *
 * Testes unitários para verificar o movimento do monstro
 * e restrições de movimento devido a obstáculos.
 */
public class MonsterTest {

    private BoardModel board;
    private Monster monster;

    @BeforeEach
    void setUp() {
        board = new BoardModel(5, 5);
        monster = new Monster(2, 2);
        board.setMonster(monster);

        // Preenche com neve exceto nas bordas
        for (int r = 1; r < board.getRowCount() - 1; r++) {
            for (int c = 1; c < board.getColCount() - 1; c++) {
                board.setPositionContent(r, c, PositionContent.SNOW);
            }
        }
    }

    /**
     * Testa o movimento do monstro para a esquerda numa célula livre.
     */
    @DisplayName("Monster moves left into a free cell")
    @Test
    void testMonsterToTheLeft() {
        assertEquals(2, board.getMonsterRow());
        assertEquals(2, board.getMonsterCol());

        board.moveMonster(MonsterDirections.LEFT);

        assertEquals(2, board.getMonsterRow());
        assertEquals(1, board.getMonsterCol());
    }

    /**
     * Testa o movimento do monstro para baixo numa célula livre.
     */
    @DisplayName("Monster moves down into a free cell")
    @Test
    void testMonsterToDown() {
        assertEquals(2, board.getMonsterRow());
        assertEquals(2, board.getMonsterCol());

        board.moveMonster(MonsterDirections.DOWN);

        assertEquals(3, board.getMonsterRow());
        assertEquals(2, board.getMonsterCol());
    }

    /**
     * Verifica que o monstro não se move para uma célula com bloco.
     */
    @DisplayName("Monster blocked by a BLOCK cell")
    @Test
    void testBlockedByBlock() {
        board.setPositionContent(2, 3, PositionContent.BLOCK); // bloquear posição à direita
        monster.setPosition(2, 2); // monstro no meio

        board.moveMonster(MonsterDirections.RIGHT);

        // Monstro deve continuar no mesmo sítio
        assertEquals(2, board.getMonsterRow());
        assertEquals(2, board.getMonsterCol());
    }
}
