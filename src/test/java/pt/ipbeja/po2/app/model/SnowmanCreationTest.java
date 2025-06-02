package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipbeja.app.model.*;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testAverageBigSnowman() {
        Snowball large = new Snowball(1, 2, SnowballStatus.LARGE);
        Snowball medium = new Snowball(1, 1, SnowballStatus.MEDIUM);

        board.getSnowballs().add(large);
        board.getSnowballs().add(medium);

        board.moveMonster(MonsterDirections.RIGHT);

        Snowball stack = board.getSnowballAt(1, 2);
        assertEquals(SnowballStatus.LARGE_MEDIUM, stack.getStatus());
    }

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

        Snowball stack = board.getSnowballAt(1, 2);
        assertEquals(SnowballStatus.FULL_SNOWMAN, stack.getStatus());
        assertEquals(PositionContent.SNOWMAN, board.getPositionContent(1, 2));
    }
}