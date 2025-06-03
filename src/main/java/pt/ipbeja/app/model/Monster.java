/**
 * Martim Dias - 24290
 * Representa o monstro controlado pelo jogador no tabuleiro.
 *
 * Esta classe estende ´MobileElement´, o que significa que o monstro é movel.
 * Ele pode se mover pelo mapa, mudando a posição
 */
package pt.ipbeja.app.model;


public class Monster extends MobileElement {
    /**
     * Construtor que define a posição inicial do monstro.
     * @param row Linha inicial
     * @param col Coluna inicial
     */
    public Monster(int row, int col) {
        super(row, col);
    }

    /**
     * Move o monstro para uma nova posição no tabuleiro.
     * @param newRow Nova linha
     * @param newCol Nova coluna
     */
    public void moveTo(int newRow, int newCol) {
        this.setPosition(newRow, newCol);
    }
}