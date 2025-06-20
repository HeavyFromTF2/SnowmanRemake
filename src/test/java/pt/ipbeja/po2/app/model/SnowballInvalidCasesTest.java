package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.model.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Martim Dias - 24290
 *
 * Testes para garantir que casos inválidos com bolas de neve,
 * como empilhamentos incorretos e limites do tabuleiro, são tratados corretamente.
 */
public class SnowballInvalidCasesTest {

    private BoardModel board;
    private Monster monster;

    @BeforeEach
    void setUp() {
        board = new BoardModel(5, 5);
        monster = new Monster(1, 0);
        board.setMonster(monster);

        for (int r = 1; r < board.getRowCount() - 1; r++) {
            for (int c = 1; c < board.getColCount() - 1; c++) {
                board.setPositionContent(r, c, PositionContent.NO_SNOW);
            }
        }
    }

    /**
     * Verifica que duas bolas pequenas não são empilhadas incorretamente.
     */
    @DisplayName("Invalid: two small snowballs cannot stack")
    @Test
    void testInvalidStackSmallSmall() {
        Snowball small1 = new Snowball(1, 1, SnowballStatus.SMALL);
        Snowball small2 = new Snowball(1, 2, SnowballStatus.SMALL);

        board.getSnowballs().add(small1);
        board.getSnowballs().add(small2);

        board.moveMonster(MonsterDirections.RIGHT);

        Snowball result = board.getSnowballManager().getSnowballAt(1, 2);
        assertEquals(SnowballStatus.SMALL, result.getStatus());
        assertNotNull(board.getSnowballManager().getSnowballAt(1, 1));
    }

    /**
     * Garante que a bola não é empurrada para fora do tabuleiro.
     */
    @DisplayName("Cannot push snowball out of bounds")
    @Test
    void testPushSnowballOutOfBounds() {
        Snowball small = new Snowball(1, 3, SnowballStatus.SMALL);
        monster.setPosition(1, 2);

        board.getSnowballs().add(small);
        board.moveMonster(MonsterDirections.RIGHT);

        Snowball result = board.getSnowballManager().getSnowballAt(1, 3);
        assertNotNull(result);
        assertEquals(SnowballStatus.SMALL, result.getStatus());

        assertEquals(1, board.getMonsterRow());
        assertEquals(2, board.getMonsterCol());
    }
}