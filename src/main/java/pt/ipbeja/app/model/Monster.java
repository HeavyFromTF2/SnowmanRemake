package pt.ipbeja.app.model;

/**
 * Representa o monstro que o jogador controla.
 */
public class Monster extends MobileElement {

    /**
     * Construtor do monstro.
     */
    public Monster(int row, int col) {
        super(row, col);
    }

    /**
     * Move o monstro para uma nova posição.
     */
    public void moveTo(int newRow, int newCol) {
        this.setPosition(newRow, newCol);
    }
}