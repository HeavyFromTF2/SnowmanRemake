package pt.ipbeja.app.model;


public class Score implements Comparable<Score> {
    private String playerName;
    private String levelName;
    private int moves;

    public Score(String playerName, String levelName, int moves) {
        this.playerName = playerName;
        this.levelName = levelName;
        this.moves = moves;
    }

    public String getPlayerName() { return playerName; }
    public String getLevelName() { return levelName; }
    public int getMoves() { return moves; }

    @Override
    public int compareTo(Score other) {
        return Integer.compare(this.moves, other.moves); // Menos movimentos = melhor score
    }

    @Override
    public String toString() {
        return playerName + " - " + moves;
    }
}
