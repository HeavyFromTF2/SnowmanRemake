package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.model.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Martim Dias - 24290
 *
 * Testes para verificar o crescimento das bolas de neve
 * ao entrarem em contacto com células com neve.
 */
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

    /**
     * Testa crescimento de uma bola pequena para média após passar por neve.
     */
    @DisplayName("Small snowball grows to medium on snow")
    @Test
    void testCreateAverageSnowball() {
        Snowball small = new Snowball(2, 2, SnowballStatus.SMALL);
        board.getSnowballs().add(small);

        board.moveMonster(MonsterDirections.RIGHT);

        Snowball result = board.getSnowballManager().getSnowballAt(2, 3);
        assertNotNull(result);
        assertEquals(SnowballStatus.MEDIUM, result.getStatus());
        assertEquals(PositionContent.NO_SNOW, board.getPositionContent(2, 3));
    }

    /**
     * Testa crescimento de uma bola média para grande após passar por neve.
     */
    @DisplayName("Medium snowball grows to large on snow")
    @Test
    void testCreateBigSnowball() {
        Snowball medium = new Snowball(2, 2, SnowballStatus.MEDIUM);
        board.getSnowballs().add(medium);

        board.moveMonster(MonsterDirections.RIGHT);

        Snowball result = board.getSnowballManager().getSnowballAt(2, 3);
        assertNotNull(result);
        assertEquals(SnowballStatus.LARGE, result.getStatus());
        assertEquals(PositionContent.NO_SNOW, board.getPositionContent(2, 3));
    }

    /**
     * Garante que uma bola grande não cresce mais e a neve permanece.
     */
    @DisplayName("Large snowball stays large and snow remains")
    @Test
    void testMaintainBigSnowball() {
        Snowball large = new Snowball(2, 2, SnowballStatus.LARGE);
        board.getSnowballs().add(large);

        board.moveMonster(MonsterDirections.RIGHT);

        Snowball result = board.getSnowballManager().getSnowballAt(2, 3);
        assertNotNull(result);
        assertEquals(SnowballStatus.LARGE, result.getStatus());
        assertEquals(PositionContent.SNOW, board.getPositionContent(2, 3)); // Neve permanece
    }

    @DisplayName("Create snowman with large and medium snowballs")
    @Test
    void testAverageBigSnowball() {
        // Posiciona bolas na mesma linha do monstro
        Snowball medium = new Snowball(2, 2, SnowballStatus.MEDIUM);
        Snowball large = new Snowball(2, 3, SnowballStatus.LARGE);

        board.getSnowballs().add(medium);
        board.getSnowballs().add(large);

        // Move o monstro para empurrar as bolas para a direita
        board.moveMonster(MonsterDirections.RIGHT);

        Snowball stack = board.getSnowballManager().getSnowballAt(2, 3);
        assertEquals(SnowballStatus.LARGE_MEDIUM, stack.getStatus());
    }
}