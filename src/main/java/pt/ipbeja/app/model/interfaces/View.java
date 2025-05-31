package pt.ipbeja.app.model.interfaces;

// Em principio a view ta bem feita. Pode ter algo mal tho
public interface View {
    void updateBoard();
    void resetUI();  // opcional: por ex. para contador de movimentos lol
    void gameCompleted();  // opcional: se quiseres sinalizar a vitória

    void returnToMenu();  // para quando o jogo resetar após quebra de regras

    void showUnsolvableDialog();
    void showLevelCompletedDialog();
}
