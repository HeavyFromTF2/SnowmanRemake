package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.model.Monster;
import pt.ipbeja.estig.po2.snowman.model.MonsterDirections;
import pt.ipbeja.estig.po2.snowman.model.PositionContent;

import static org.junit.jupiter.api.Assertions.*;

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
     * Testar movimento do monstro para uma posição livre para a esquerda.
     */
    @Test
    void testMonsterToTheLeft() {
        assertEquals(2, board.getMonsterRow());
        assertEquals(2, board.getMonsterCol());

        board.moveMonster(MonsterDirections.LEFT);

        assertEquals(2, board.getMonsterRow());
        assertEquals(1, board.getMonsterCol());
    }

    /**
     * Testar movimento do monstro para uma posição livre para a esquerda.
     */
    @Test
    void testMonsterToDown() {
        assertEquals(2, board.getMonsterRow());
        assertEquals(2, board.getMonsterCol());

        board.moveMonster(MonsterDirections.DOWN);

        assertEquals(3, board.getMonsterRow());
        assertEquals(2, board.getMonsterCol());
    }

    /**
     * Testar que o monstro não se move para uma posição com BLOCK.
     */
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
