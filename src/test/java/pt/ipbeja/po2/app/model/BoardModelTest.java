package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ipbeja.app.model.*;

import static org.junit.jupiter.api.Assertions.*;

class BoardModelTest {

    private BoardModel board;
    private Monster monster;


    @BeforeEach
    // Board normal cheia de neve
    void setUp() {
        board = new BoardModel(5, 5);
        monster = new Monster(2, 2);
        board.setMonster(monster);

        // Limpa o tabuleiro exceto borda para os testes
        for (int r = 1; r < board.getRowCount() - 1; r++) {
            for (int c = 1; c < board.getColCount() - 1; c++) {
                board.setPositionContent(r, c, PositionContent.SNOW);
            }
        }
    }

    // Board específica sem nada para testes
    void normalBoard() {
        board = new BoardModel(5, 5);
        monster = new Monster(1, 0);
        board.setMonster(monster);

        // Limpa o tabuleiro exceto borda para os testes
        for (int r = 1; r < board.getRowCount() - 1; r++) {
            for (int c = 1; c < board.getColCount() - 1; c++) {
                board.setPositionContent(r, c, PositionContent.NO_SNOW);
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
     * Testar criação de uma bola de neve média, empurrando uma bola pequena sobre uma posição com neve.
     */
    @Test
    void testCreateAverageSnowball() {
        // Bola pequena em (2,2), posição (2,3) tem neve
        Snowball smallSnowball = new Snowball(2, 2, SnowballStatus.SMALL);
        board.getSnowballs().add(smallSnowball);
        board.setPositionContent(2, 3, PositionContent.SNOW);

        // Posicionar monstro em (2,1) para empurrar para a direita
        monster.setPosition(2, 1);

        // O monstro vai empurrar a bola pequena de (2,2) para (2,3) com neve, deve criar bola média
        board.moveMonster(MonsterDirections.RIGHT);

        Snowball ballAt3 = board.getSnowballAt(2, 3);
        assertNotNull(ballAt3);
        assertEquals(SnowballStatus.MEDIUM, ballAt3.getStatus());

        // A bola pequena original deve ter crescido e a nova posição deve estar NO_SNOW agora
        assertEquals(PositionContent.NO_SNOW, board.getPositionContent(2, 3));
    }

    /**
     * Testar criação de uma bola de neve grande, empurrando uma bola média sobre uma posição com neve.
     */
    @Test
    void testCreateBigSnowball() {
        // Bola média em (2,2), posição (2,3) tem neve
        Snowball mediumBall = new Snowball(2, 2, SnowballStatus.MEDIUM);
        board.getSnowballs().add(mediumBall);
        board.setPositionContent(2, 3, PositionContent.SNOW);

        // Monstro em (2,1)
        monster.setPosition(2, 1);

        // Move para a direita, empurrando bola média para neve, deve crescer para grande
        board.moveMonster(MonsterDirections.RIGHT);

        Snowball ballAt3 = board.getSnowballAt(2, 3);
        assertNotNull(ballAt3);
        assertEquals(SnowballStatus.LARGE, ballAt3.getStatus());

        assertEquals(PositionContent.NO_SNOW, board.getPositionContent(2, 3));
    }

    /**
     * Testar que uma bola de neve grande se mantém do mesmo tamanho mesmo quando empurrada sobre uma posição com neve.
     */
    @Test
    void testMaintainBigSnowball() {
        // Bola grande em (2,2), posição (2,3) tem neve
        Snowball largeBall = new Snowball(2, 2, SnowballStatus.LARGE);
        board.getSnowballs().add(largeBall);
        board.setPositionContent(2, 3, PositionContent.SNOW);

        // Monstro em (2,1)
        monster.setPosition(2, 1);

        board.moveMonster(MonsterDirections.RIGHT);

        Snowball ballAt3 = board.getSnowballAt(2, 3);
        assertNotNull(ballAt3);
        // Continua grande, não deve crescer nem mudar
        assertEquals(SnowballStatus.LARGE, ballAt3.getStatus());

        // A neve permanece intacta? (Pode permanecer como SNOW ou virar NO_SNOW dependendo da implementação)
        // Pelo código parece que a neve não some ao empurrar bola grande.
        assertEquals(PositionContent.SNOW, board.getPositionContent(2, 3));
    }

    /**
     * Testar que empurrar uma bola de neve média para cima de uma posição com uma bola grande cria um boneco de neve incompleto.
     */
    @Test
    void testAverageBigSnowman() {
        normalBoard();

        // Bola grande em (1,2)
        Snowball largeBall = new Snowball(1, 2, SnowballStatus.LARGE);
        board.getSnowballs().add(largeBall);

        // Bola média em (1,1)
        Snowball mediumBall = new Snowball(1, 1, SnowballStatus.MEDIUM);
        board.getSnowballs().add(mediumBall);

        // Monstro passa de (1,0) para (1,1)
        board.moveMonster(MonsterDirections.RIGHT);

        Snowball stack = board.getSnowballAt(1, 2);

        assertEquals(SnowballStatus.LARGE_MEDIUM, stack.getStatus());
    }

    /**
     * Testar que empurrar uma bola de neve pequena para cima de uma posição com uma bola grande e uma bola média cria um boneco de neve completo.
     */
    @Test
    void testCompleteSnowman() {
        normalBoard();

        // Bola grande em (1,2)
        Snowball largeBall = new Snowball(1, 2, SnowballStatus.LARGE);
        board.getSnowballs().add(largeBall);

        // Bola média em (1,1)
        Snowball mediumBall = new Snowball(1, 1, SnowballStatus.MEDIUM);
        board.getSnowballs().add(mediumBall);

        // Monstro passa de (1,0) para (1,1)
        board.moveMonster(MonsterDirections.RIGHT);
        Snowball stack = board.getSnowballAt(1, 2);
        assertEquals(SnowballStatus.LARGE_MEDIUM, stack.getStatus());

        // Bola média em (1,1)
        Snowball smallBall = new Snowball(2, 2, SnowballStatus.SMALL);
        board.getSnowballs().add(smallBall);

        // Monstro passa de (1,1) para (2,1)
        board.moveMonster(MonsterDirections.DOWN);

        // Monstro passa de (2,1) para (3,1)
        board.moveMonster(MonsterDirections.DOWN);

        // Monstro passa de (3,1) para (3,2)
        board.moveMonster(MonsterDirections.RIGHT);

        // Monstro passa de (3,2) para (2,2)
        board.moveMonster(MonsterDirections.UP);

        stack = board.getSnowballAt(1, 2);
        assertEquals(SnowballStatus.FULL_SNOWMAN, stack.getStatus());
    }
}