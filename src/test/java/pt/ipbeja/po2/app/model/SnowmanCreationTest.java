package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.model.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Martim Dias - 24290
 *
 * Testes para validar a criação de um boneco de neve completo,
 * empilhando bolas de tamanhos adequados na ordem certa.
 */
public class SnowmanCreationTest {

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
     * Testa a criação completa de boneco de neve com três bolas.
     */
    @DisplayName("Create complete snowman with 3 snowballs")
    @Test
    void testCompleteSnowman() {
        Snowball large = new Snowball(1, 2, SnowballStatus.LARGE);
        Snowball medium = new Snowball(1, 1, SnowballStatus.MEDIUM);
        Snowball small = new Snowball(2, 2, SnowballStatus.SMALL);

        board.getSnowballs().add(large);
        board.getSnowballs().add(medium);
        board.getSnowballs().add(small);

        board.moveMonster(MonsterDirections.RIGHT);
        board.moveMonster(MonsterDirections.DOWN);
        board.moveMonster(MonsterDirections.DOWN);
        board.moveMonster(MonsterDirections.RIGHT);
        board.moveMonster(MonsterDirections.UP);

        Snowball stack = board.getSnowballManager().getSnowballAt(1, 2);
        assertEquals(SnowballStatus.FULL_SNOWMAN, stack.getStatus());
        assertEquals(PositionContent.SNOWMAN, board.getPositionContent(1, 2));
    }

    /**
     * Testa que não é possível criar um boneco de neve completo se a ordem das bolas estiver incorreta.
     */
    @DisplayName("Fail to create complete snowman with wrong snowballs order")
    @Test
    void testFailCompleteSnowmanWrongOrder() {
        Snowball small = new Snowball(1, 2, SnowballStatus.SMALL);
        Snowball medium = new Snowball(1, 1, SnowballStatus.MEDIUM);
        Snowball large = new Snowball(2, 2, SnowballStatus.LARGE);

        board.getSnowballs().add(small);
        board.getSnowballs().add(medium);
        board.getSnowballs().add(large);

        board.moveMonster(MonsterDirections.RIGHT);
        board.moveMonster(MonsterDirections.DOWN);
        board.moveMonster(MonsterDirections.DOWN);
        board.moveMonster(MonsterDirections.RIGHT);
        board.moveMonster(MonsterDirections.UP);

        Snowball stack = board.getSnowballManager().getSnowballAt(1, 2);

        // Verifica que o status não é FULL_SNOWMAN porque a ordem está incorreta
        assertNotEquals(SnowballStatus.FULL_SNOWMAN, stack.getStatus());
    }
}