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

        // Bola pequena em (2,2)
        Snowball smallBall = new Snowball(2, 2, SnowballStatus.SMALL);
        board.getSnowballs().add(smallBall);

        // Monstro leva a pequena até cima
        board.moveMonster(MonsterDirections.DOWN);
        board.moveMonster(MonsterDirections.DOWN);
        board.moveMonster(MonsterDirections.RIGHT);
        board.moveMonster(MonsterDirections.UP);

        stack = board.getSnowballAt(1, 2);
        assertEquals(SnowballStatus.FULL_SNOWMAN, stack.getStatus());

        // ✅ Aqui está a verificação adicional
        assertEquals(PositionContent.SNOWMAN, board.getPositionContent(1, 2));
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

    /**
     * Testar que empilhar duas bolas pequenas não é permitido.
     */
    @Test
    void testInvalidStackSmallSmall() {
        normalBoard();

        // Bola pequena em (1,1) e outra em (1,2)
        Snowball small1 = new Snowball(1, 1, SnowballStatus.SMALL);
        Snowball small2 = new Snowball(1, 2, SnowballStatus.SMALL);
        board.getSnowballs().add(small1);
        board.getSnowballs().add(small2);

        // Monstro em (1,0)
        monster.setPosition(1, 0);

        // Tenta empurrar small1 sobre small2
        board.moveMonster(MonsterDirections.RIGHT);

        // Ambos ainda devem existir como bolas separadas
        Snowball result = board.getSnowballAt(1, 2);
        assertEquals(SnowballStatus.SMALL, result.getStatus());

        assertNotNull(board.getSnowballAt(1, 1));
    }

    /**
     * Testar que o monstro não consegue empurrar uma bola para fora do tabuleiro.
     */
    @Test
    void testPushSnowballOutOfBounds() {
        // Criar tabuleiro limpo
        normalBoard();

        // Colocar bola pequena no canto (1,3)
        Snowball smallBall = new Snowball(1, 3, SnowballStatus.SMALL);
        board.getSnowballs().add(smallBall);

        // Monstro em (1,2), vai tentar empurrar para a direita
        monster.setPosition(1, 2);

        // Tentar empurrar para fora
        board.moveMonster(MonsterDirections.RIGHT);

        // Bola deve continuar na mesma posição, pois não pode sair do tabuleiro
        Snowball ball = board.getSnowballAt(1, 3);
        assertNotNull(ball);
        assertEquals(SnowballStatus.SMALL, ball.getStatus());

        // Monstro não se moveu
        assertEquals(1, board.getMonsterRow());
        assertEquals(2, board.getMonsterCol());
    }

}