package pt.ipbeja.app.model;

/**
 * Martim Dias - 24290
 * Representa uma bola de neve no tabuleiro, com um tamanho/estado específico.
 * Pode crescer ou ser empilhada para formar um boneco de neve.
 *
 * Esta classe estende `MobileElement` pois as bolas de neve são elementos móveis no jogo.
 * Ou seja, elas podem ser empurradas e mudar de posição no tabuleiro,
 * por isso partilham a mesma lógica básica de posição que está em `MobileElement`.
 */

public class Snowball extends MobileElement {

    private SnowballStatus status;

    /**
     * Construtor de uma bola de neve.
     *
     * @param row    linha onde está localizada
     * @param col    coluna onde está localizada
     * @param status estado inicial (ex: SMALL, MEDIUM, etc.)
     */
    public Snowball(int row, int col, SnowballStatus status) {
        super(row, col);
        this.status = status;
    }

    /**
     * Devolve o estado atual da bola de neve.
     *
     * @return status (tamanho ou combinação)
     */
    public SnowballStatus getStatus() {
        return this.status;
    }

    /**
     * Atualiza o estado da bola de neve.
     *
     * @param status novo estado (ex: crescer de SMALL para MEDIUM)
     */
    public void setStatus(SnowballStatus status) {
        this.status = status;
    }

    /**
     * Faz a bola de neve crescer de tamanho ao passar por neve,
     * se ainda não for a maior (LARGE). Só bolas SMALL ou MEDIUM podem crescer.
     */
    public void growSnowball() {
        switch (this.status) {
            case SMALL:
                this.status = SnowballStatus.MEDIUM;
                break;
            case MEDIUM:
                this.status = SnowballStatus.LARGE;
                break;
            default:
                break;
        }
    }

    /**
     * Representação textual só para debugging.
     */
    @Override
    public String toString() {
        return "Snowball{" +
                "status=" + status +
                ", row=" + getRow() +
                ", col=" + getCol() +
                '}';
    }
}