package pt.ipbeja.estig.po2.snowman.model;

/**
 * Martim Dias - 24290
 * Classe abstrata que representa um elemento móvel no tabuleiro.
 *
 * Armazena a posição (linha e coluna) e fornece métodos para leitura e atualização dessa posição.
 * Elementos móveis, como monstros e bolas de neve, estendem esta classe.
 */

public abstract class MobileElement {
    protected int row, col;

    /**
     * Cria um novo elemento móvel na posição especificada.
     *
     * @param row linha inicial do elemento
     * @param col coluna inicial do elemento
     */
    public MobileElement(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Atualiza a posição do elemento no tabuleiro.
     *
     * @param row nova linha
     * @param col nova coluna
     */
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Obtém a linha atual do elemento.
     *
     * @return linha atual
     */
    public int getRow() {
        return row;
    }

    /**
     * Obtém a coluna atual do elemento.
     *
     * @return coluna atual
     */
    public int getCol() {
        return col;
    }
}
