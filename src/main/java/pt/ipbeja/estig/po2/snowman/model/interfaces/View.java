package pt.ipbeja.estig.po2.snowman.model.interfaces;

/**
 * Martim Dias - 24290
 * Interface para comunicação entre o modelo e a interface gráfica (View).
 * Define métodos que a interface gráfica deve implementar para refletir alterações do jogo.
 */

public interface View {
    void updateBoard();
    void resetUI();  // opcional: por ex. para contador de movimentos lol
    void gameCompleted();  // opcional: se quiseres sinalizar a vitória

    void returnToMenu();  // para quando o jogo resetar após quebra de regras

    void showUnsolvableDialog();
    void showLevelCompletedDialog();
}
