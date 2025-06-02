package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipbeja.app.model.*;

import static org.junit.jupiter.api.Assertions.*;


public class SnowballGrowthTest {

    private BoardModel board;
    private Monster monster;

    @BeforeEach
    void setUp() {
        board = new BoardModel(5, 5);
        monster = new Monster(2, 1);
        board.setMonster(monster);

        // Limpa e adiciona neve no destino
        for (int r = 1; r < board.getRowCount() - 1; r++) {
            for (int c = 1; c < board.getColCount() - 1; c++) {
                board.setPositionContent(r, c, PositionContent.NO_SNOW);
            }
        }
        board.setPositionContent(2, 3, PositionContent.SNOW);
    }

    @Test
    void testCreateAverageSnowball() {
        Snowball small = new Snowball(2, 2, SnowballStatus.SMALL);
        board.getSnowballs().add(small);

        board.moveMonster(MonsterDirections.RIGHT);

        Snowball result = board.getSnowballAt(2, 3);
        assertNotNull(result);
        assertEquals(SnowballStatus.MEDIUM, result.getStatus());
        assertEquals(PositionContent.NO_SNOW, board.getPositionContent(2, 3));
    }

    @Test
    void testCreateBigSnowball() {
        Snowball medium = new Snowball(2, 2, SnowballStatus.MEDIUM);
        board.getSnowballs().add(medium);

        board.moveMonster(MonsterDirections.RIGHT);

        Snowball result = board.getSnowballAt(2, 3);
        assertNotNull(result);
        assertEquals(SnowballStatus.LARGE, result.getStatus());
        assertEquals(PositionContent.NO_SNOW, board.getPositionContent(2, 3));
    }

    @Test
    void testMaintainBigSnowball() {
        Snowball large = new Snowball(2, 2, SnowballStatus.LARGE);
        board.getSnowballs().add(large);

        board.moveMonster(MonsterDirections.RIGHT);

        Snowball result = board.getSnowballAt(2, 3);
        assertNotNull(result);
        assertEquals(SnowballStatus.LARGE, result.getStatus());
        assertEquals(PositionContent.SNOW, board.getPositionContent(2, 3)); // Neve permanece
    }
}