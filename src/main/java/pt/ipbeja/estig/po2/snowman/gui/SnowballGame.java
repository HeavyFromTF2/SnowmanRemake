package pt.ipbeja.estig.po2.snowman.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pt.ipbeja.estig.po2.snowman.model.*;
import pt.ipbeja.estig.po2.snowman.model.interfaces.View;
import pt.ipbeja.estig.po2.snowman.model.utilities.AudioPlayer;
import pt.ipbeja.estig.po2.snowman.model.utilities.ScoreManager;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

/**
 * Martim Dias - 24290
 * Rafael Picareta - 24288
 *
 * Classe principal da interface gráfica do jogo Snowball.
 * Responsável pela criação da janela do jogo,
 * interação com o utilizador e atualização visual do tabuleiro.
 * Implementa a interface {@link View} para receber atualizações do modelo.
 */
public class SnowballGame extends Application implements View {
    private static final int CELL_SIZE = 60;
    private Stage gameStage;
    private BoardModel model;
    private GridPane grid;
    private VBox scoreBoxRight;
    private TextArea moveLog;
    private Label moveCounterLabel;
    private Label monsterPositionLabel;
    public String playerName;
    private MonsterDirections currentDirection = MonsterDirections.DOWN;
    private BoardRenderer boardRenderer;

    AudioPlayer audioPlayer = new AudioPlayer();
    ScoreManager scoreManager = new ScoreManager();

    // --- Set do nome do Jogador ---

    /**
     * Define o nome do jogador atual.
     * @param playerName Nome do jogador
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    // --- Inicio do Jogo ---

    /**
     * Mét0do start padrão do JavaFX.
     * Está desativado pois o jogo deve ser iniciado com o startFromFile()
     *
     * @param primaryStage Stage principal recebido pelo JavaFX
     * @throws UnsupportedOperationException sempre
     */
    @Override
    public void start(Stage primaryStage) {
        throw new UnsupportedOperationException("Use StartingMenu to start the game.");
    }

    /**
     * Inicia o jogo a partir de um ficheiro de nível.
     * Cria a janela do jogo, carrega o nível e configura a interface.
     *
     * @param levelFileName Nome do ficheiro do nível (ex: "level1.txt")
     */
    public void startFromFile(String levelFileName) {
        gameStage = new Stage();

        this.model = new BoardModel(10, 10); // Cria tabuleiro 10x10 (pode ajustar)
        this.model.setView(this);
        this.grid = new GridPane();
        this.boardRenderer = new BoardRenderer(model, grid, CELL_SIZE, currentDirection, this::handleCellClick);
        this.model.setLevelName(levelFileName);

        loadLevelFromFile(levelFileName.replace(".txt", "")); // Carrega o nível do ficheiro

        // Toca música correspondente ao nível
        if (levelFileName.equalsIgnoreCase("level1")) {
            audioPlayer.play("mus1.wav");
        } else if (levelFileName.equalsIgnoreCase("level2")) {
            audioPlayer.play("mus2.wav");
        }

        BorderPane mainLayout = createMainLayout();

        VBox topContainer = createMenuBar(); // Cria barra de menus
        mainLayout.setTop(topContainer);

        boardRenderer.drawBoard(); // Desenha o tabuleiro inicialmente

        Scene scene = new Scene(mainLayout, 10 * CELL_SIZE + 150, 10 * CELL_SIZE + 100);
        gameStage.setTitle("Snowball Game - " + levelFileName);
        gameStage.setScene(scene);

        gameStage.show();
    }

    /**
     * Cria e devolve uma VBox contendo a barra de menus (Undo, Redo, Quit).
     *
     * @return VBox com a MenuBar pronta para usar
     */
    private VBox createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu editMenu = new Menu("Edit");

        MenuItem undoItem = new MenuItem("Undo");
        undoItem.setOnAction(e -> model.undo());

        MenuItem redoItem = new MenuItem("Redo");
        redoItem.setOnAction(e -> model.redo());

        editMenu.getItems().addAll(undoItem, redoItem);

        Menu quitMenu = new Menu("Quit");
        MenuItem exitItem = new MenuItem("Return to Menu");
        exitItem.setOnAction(e -> returnToMenu());
        quitMenu.getItems().add(exitItem);

        menuBar.getMenus().addAll(editMenu, quitMenu);

        VBox topContainer = new VBox(menuBar);
        return topContainer;
    }

    /**
     * Cria o layout principal do jogo, incluindo o tabuleiro,
     * área de texto com histórico de movimentos e painel de scores.
     *
     * @return BorderPane com toda a interface configurada
     */
    private BorderPane createMainLayout() {
        moveLog = new TextArea();
        moveLog.setEditable(false);
        moveLog.setPrefHeight(100);

        moveCounterLabel = new Label("Movements: 0");

        // Obtem posição inicial do monstro para mostrar
        int monsterRow = model.getMonsterRow();
        int monsterCol = model.getMonsterCol();
        char colLetter = (char) ('A' + monsterCol);

        monsterPositionLabel = new Label("Monster position: (" + (monsterRow + 1) + ", " + colLetter + ")");

        VBox bottomBox = new VBox(moveCounterLabel, monsterPositionLabel, moveLog);

        // VBOX para mostrar scores à direita (inicialmente escondida)
        scoreBoxRight = new VBox();
        scoreBoxRight.setSpacing(10);
        scoreBoxRight.setAlignment(Pos.TOP_CENTER);
        scoreBoxRight.setStyle("-fx-padding: 10; -fx-border-color: gray;");
        scoreBoxRight.setVisible(false);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(grid);
        mainLayout.setBottom(bottomBox);
        mainLayout.setRight(scoreBoxRight);

        return mainLayout;
    }

    // --- Atualizações da Interface ---

    /**
     * Atualiza o tabuleiro visualmente chamando o BoardRenderer.
     * Chamado pelo modelo quando há mudanças no jogo.
     */
    @Override
    public void updateBoard() {
        boardRenderer.drawBoard();
    }

    /**
     * Reseta os elementos da interface relacionados a movimentos e logs.
     * Chamado pelo modelo para reiniciar a UI.
     */
    @Override
    public void resetUI() {
        moveCounterLabel.setText("Movements: 0");
        moveLog.clear();
    }

    // --- Controlo de Eventos do Jogo ---

    /**
     * Fecha a janela atual do jogo e retorna ao menu inicial.
     * Também para o áudio e reseta o estado do modelo.
     */
    @Override
    public void returnToMenu() {
        audioPlayer.stop();

        if (gameStage != null) {
            gameStage.close();
        }

        model.resetGame();

        StartingMenu menu = new StartingMenu();
        try {
            Stage menuStage = new Stage();
            menu.start(menuStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Trata o final do jogo quando o jogador completa o nível.
     * Fecha a janela atual e reabre o menu inicial.
     */
    @Override
    public void gameCompleted() {
        System.out.println("Game completed!");

        if (gameStage != null) {
            gameStage.close();
        }

        StartingMenu menu = new StartingMenu();
        try {
            Stage menuStage = new Stage();
            menu.start(menuStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        audioPlayer.stop();
    }

    // --- Diálogos Finais e Scores ---

    /**
     * Mostra um diálogo de aviso quando o jogo não tem solução possível.
     * Depois de fechar o alerta, retorna ao menu principal.
     */
    @Override
    public void showUnsolvableDialog() {
        javafx.application.Platform.runLater(() -> {
            showFinalScorePanel(); // Mostra scores antes do alerta

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("This game can no longer be completed.\nYou lost!...");

            alert.setOnHidden(e -> returnToMenu());
            alert.show();
        });
    }

    /**
     * Mostra um diálogo de sucesso ao completar o nível.
     * Atualiza os scores, mostra painel de resultados e depois fecha o jogo.
     */
    @Override
    public void showLevelCompletedDialog() {
        javafx.application.Platform.runLater(() -> {
            scoreManager.addScore(new Score(playerName, model.getLevelName(), model.getMoveCount()));

            showFinalScorePanel();

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("🏆 You did it! 🏆");
            alert.setHeaderText(null);
            alert.setContentText("Good job, you built a snowman! How cool!");

            alert.setOnHidden(e -> {
                scoreManager.addScore(new Score(playerName, model.getLevelName(), model.getMoveCount()));
                gameCompleted();
                model.saveMonsterPositionsToFile();
                model.resetGame();
            });

            alert.show();
        });
    }

    /**
     * Atualiza o painel de scores à direita com o score atual e os top 3 scores (atualiza se o novo score for top 3).
     */
    private void showFinalScorePanel() {
        scoreBoxRight.getChildren().clear();

        Label title = new Label("🎯 Final Score");
        Label current = new Label("Player: " + playerName + "\nLevel: " + model.getLevelName() + "\nMoves: " + model.getMoveCount());

        List<Score> topScores = scoreManager.getTopScores(model.getLevelName());

        StringBuilder sb = new StringBuilder("🏆 Top 3 Scores:\n");
        for (Score s : topScores) {
            sb.append(s.getPlayerName()).append(" - ").append(s.getMoves()).append(" moves\n");
        }

        Label topScoresLabel = new Label(sb.toString());

        scoreBoxRight.getChildren().addAll(title, current, topScoresLabel);
        scoreBoxRight.setVisible(true);
    }

    // --- Carregamento de Nível ---

    /**
     * Lê o ficheiro do nível e popula o modelo com o conteúdo do tabuleiro.
     * Suporta múltiplos tipos de conteúdos codificados por símbolos no ficheiro.
     *
     * @param levelName Nome do ficheiro do nível sem extensão (.txt)
     */
    private void loadLevelFromFile(String levelName) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/levels/" + levelName + ".txt"))))) {

            for (int row = 0; br.ready(); row++) {
                String[] tokens = br.readLine().trim().split("\\s+");
                for (int col = 0; col < tokens.length; col++) {
                    switch (tokens[col]) {
                        case "*" -> model.setPositionContent(row, col, PositionContent.SNOW);
                        case "s" -> model.getSnowballs().add(new Snowball(row, col, SnowballStatus.SMALL));
                        case "m" -> model.getSnowballs().add(new Snowball(row, col, SnowballStatus.MEDIUM));
                        case "b" -> model.getSnowballs().add(new Snowball(row, col, SnowballStatus.LARGE));
                        case "#" -> model.setPositionContent(row, col, PositionContent.BLOCK);
                        case "." -> model.setPositionContent(row, col, PositionContent.NO_SNOW);
                        case "M" -> model.setMonster(new Monster(row, col));
                    }
                }
            }
            model.saveState(); // Guarda estado inicial para undo/redo

        } catch (Exception e) {
            System.err.println("Error loading level: " + e.getMessage());
        }
    }

    // --- Interação com o Tabuleiro ---

    /**
     * Mét0do chamado quando o utilizador clica numa célula do tabuleiro.
     * Verifica se o movimento do monstro é válido, atualiza a direção e move o monstro.
     *
     * @param targetRow Linha da célula clicada
     * @param targetCol Coluna da célula clicada
     */
    private void handleCellClick(int targetRow, int targetCol) {
        int currentRow = model.getMonsterRow();
        int currentCol = model.getMonsterCol();

        int dRow = targetRow - currentRow;
        int dCol = targetCol - currentCol;

        // Só aceita movimentos para células adjacentes (sem diagonais)
        if (Math.abs(dRow) + Math.abs(dCol) != 1) return;

        // Verifica se pode mover para a célula alvo
        if (!model.canMoveTo(targetRow, targetCol)) return;

        // Atualiza direção do monstro para desenhar sprite correto
        if (dRow == -1) currentDirection = MonsterDirections.UP;
        if (dRow == 1) currentDirection = MonsterDirections.DOWN;
        if (dCol == -1) currentDirection = MonsterDirections.LEFT;
        if (dCol == 1) currentDirection = MonsterDirections.RIGHT;

        boardRenderer.setCurrentDirection(currentDirection);

        // Move o monstro no modelo
        model.moveMonster(currentDirection);

        int newRow = model.getMonsterRow();
        int newCol = model.getMonsterCol();

        // Atualiza labels e UI após movimento
        updateAfterMonsterMove(currentRow, currentCol, newRow, newCol);
        boardRenderer.drawBoard();
    }

    /**
     * Atualiza a interface com o contador de movimentos e a posição do monstro.
     *
     * @param currentRow Linha antiga do monstro
     * @param currentCol Coluna antiga do monstro
     * @param newRow Linha nova do monstro
     * @param newCol Coluna nova do monstro
     */
    private void updateAfterMonsterMove(int currentRow, int currentCol, int newRow, int newCol) {
        moveCounterLabel.setText("Movements: " + model.getMoveCount());

        char oldColLetter = (char) ('A' + currentCol);
        String oldPosition = "(" + (currentRow + 1) + ", " + oldColLetter + ")";
        char newColLetter = (char) ('A' + newCol);
        String newPosition = "(" + (newRow + 1) + ", " + newColLetter + ")";
        monsterPositionLabel.setText("Monster Movement: " + oldPosition + " -> " + newPosition);
    }
}