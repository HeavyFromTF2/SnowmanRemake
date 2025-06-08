package pt.ipbeja.estig.po2.snowman.model.interfaces;

/**
 * Martim Dias - 24290
 * Interface para comunicação entre o modelo e a interface gráfica (View).
 * Define métodos que a interface gráfica deve implementar para refletir alterações do jogo.
 */

public interface View {
    // Atualiza a visualização do tabuleiro conforme o estado atual do modelo.
    void updateBoard();

    // Reinicia elementos da interface, como contadores ou indicadores visuais.
    void resetUI();

    // Notifica que o jogo foi concluído com sucesso, para ações de finalização.
    void gameCompleted();

    // Retorna à tela inicial ou menu principal, usado em reinícios ou cancelamentos.
    void returnToMenu();

    // Exibe uma mensagem indicando que o nível não pode ser resolvido.
    void showUnsolvableDialog();

    // Exibe uma mensagem informando que o nível foi concluído com êxito. */
    void showLevelCompletedDialog();
}