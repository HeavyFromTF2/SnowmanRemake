package pt.ipbeja.estig.po2.snowman.model;

/**
 * Martim Dias - 24290
 * Representa o score de um jogador num determinado nível.
 *
 * Um score é composto pelo nome do jogador, nome do nível e número de movimentos realizados.
 * Quanto menor o número de movimentos, melhor o score.
 */
public class Score implements Comparable<Score> {
    private String playerName;
    private String levelName;
    private int moves;

    /**
     * Construtor de Score.
     *
     * @param playerName nome do jogador
     * @param levelName  nome do nível
     * @param moves      número de movimentos realizados
     */
    public Score(String playerName, String levelName, int moves) {
        this.playerName = playerName;
        this.levelName = levelName;
        this.moves = moves;
    }

    public String getPlayerName() { return playerName; }
    public String getLevelName() { return levelName; }
    public int getMoves() { return moves; }

    /**
     * Compara dois scores com base no número de movimentos.
     *
     * @param other outro score a comparar
     * @return valor negativo se este score for melhor (menos movimentos), positivo se for pior
     */
    @Override
    public int compareTo(Score other) {
        return Integer.compare(this.moves, other.moves);
    }

    @Override
    public String toString() {
        return playerName + " - " + moves;
    }
}
