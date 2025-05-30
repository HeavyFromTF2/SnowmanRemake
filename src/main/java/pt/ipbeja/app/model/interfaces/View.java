package pt.ipbeja.app.model.interfaces;

// TODO ISTO TEM QUE SER COM VIEW, PARA SER MEDIANTE ENTRE O MODEL E A UI
public interface View {
    void updateBoard();
    void resetUI();  // opcional: por ex. para contador de movimentos
    void gameCompleted();  // opcional: se quiseres sinalizar a vitória
}
