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
 * TODO comentar totalmente esta classe
 */
public class SnowballGame extends Application implements View {

    private static final int CELL_SIZE = 60;

    private Stage gameStage;  // Guarda o stage para manipular depois

    private BoardModel model;
    private GridPane grid;
    private VBox scoreBoxRight;

    private MonsterDirections currentDirection = MonsterDirections.DOWN;

    private TextArea moveLog;
    private Label moveCounterLabel;
    private Label monsterPositionLabel;

    private int moveCount = 0;

    AudioPlayer audioPlayer = new AudioPlayer();
    ScoreManager scoreManager = new ScoreManager();
    public String playerName;

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void start(Stage primaryStage) {
        throw new UnsupportedOperationException("Use StartingMenu to start the game.");
    }


    public void startFromFile(String levelFileName) {
        gameStage = new Stage();

        this.model = new BoardModel(10, 10); // adjust as needed
        this.model.setView(this);
        this.grid = new GridPane();
        this.model.setLevelName(levelFileName);

        loadLevelFromFile(levelFileName.replace(".txt", "")); // strip .txt if necessary

        // 🎵 Toca a música conforme o nível
        if (levelFileName.equalsIgnoreCase("level1")) {
            audioPlayer.play("mus1.wav");
        } else if (levelFileName.equalsIgnoreCase("level2")) {
            audioPlayer.play("mus2.wav");
        }

        BorderPane mainLayout = createMainLayout();

        VBox topContainer = createMenuBar(); // novo mét0do
        mainLayout.setTop(topContainer);

        drawBoard();

        Scene scene = new Scene(mainLayout, 10 * CELL_SIZE + 200, 10 * CELL_SIZE + 100);
        gameStage.setTitle("Snowball Game - " + levelFileName);
        gameStage.setScene(scene);

        gameStage.show();
    }

    /**
     * Cria uma barra de menu simplificada com opções Edit (Undo/Redo) e Quit.
     * @return VBox contendo a barra de menu.
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


    // 20 ";"
    private BorderPane createMainLayout() {
        moveLog = new TextArea();
        moveLog.setEditable(false);
        moveLog.setPrefHeight(100);

        moveCounterLabel = new Label("Movements: 0");

        // Posição inicial do monstro
        int monsterRow = model.getMonsterRow();
        int monsterCol = model.getMonsterCol();
        char colLetter = (char) ('A' + monsterCol);

        monsterPositionLabel = new Label("Monster position: (" + (monsterRow + 1) + ", " + colLetter + ")");

        VBox bottomBox = new VBox(moveCounterLabel, monsterPositionLabel, moveLog);

        // --- VBOX PARA OS SCORES À DIREITA ---
        scoreBoxRight = new VBox();
        scoreBoxRight.setSpacing(10);
        scoreBoxRight.setAlignment(Pos.TOP_CENTER);
        scoreBoxRight.setStyle("-fx-padding: 10; -fx-border-color: gray; -fx-border-width: 1;");
        scoreBoxRight.setVisible(false); // Oculta inicialmente
        // ------------------------------------

        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(grid);
        mainLayout.setBottom(bottomBox);
        mainLayout.setRight(scoreBoxRight);

        return mainLayout;
    }

    private void drawBoard() {
        grid.getChildren().clear();

        int rows = model.getRowCount();
        int cols = model.getColCount();

        // Adiciona letras das colunas (topo) e números das linhas (esquerda)
        for (int i = 0; i < Math.max(rows, cols); i++) {
            if (i < cols) {
                char colLetter = (char) ('A' + i);
                Label colLabel = createHeaderLabel(String.valueOf(colLetter));
                grid.add(colLabel, i + 1, 0);
            }
            if (i < rows) {
                Label rowLabel = createHeaderLabel(String.valueOf(i + 1));
                grid.add(rowLabel, 0, i + 1);
            }
        }

        // Adiciona as células do tabuleiro
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid.add(createCell(row, col), col + 1, row + 1);
            }
        }
    }

    @Override
    public void updateBoard() {
        drawBoard();
    }

    @Override
    public void resetUI() {
        moveCount = 0;
        moveCounterLabel.setText("Movements: 0");
        moveLog.clear();
    }

    @Override
    public void returnToMenu() {
        // para a música, se estiver a tocar
        audioPlayer.stop();

        // fecha a janela do jogo atual
        if (gameStage != null) {
            gameStage.close();
        }

        // reseta o modelo (limpa o tabuleiro)
        model.resetGame();

        // abre o menu inicial numa nova janela
        StartingMenu menu = new StartingMenu();
        try {
            Stage menuStage = new Stage();
            menu.start(menuStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void gameCompleted() {
        System.out.println("Game completed!");

        if (gameStage != null) {
            gameStage.close();  // Fecha a janela do jogo
        }

        // Abre novamente o menu inicial numa nova janela
        StartingMenu menu = new StartingMenu();
        try {
            Stage menuStage = new Stage();
            menu.start(menuStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        audioPlayer.stop();
    }

    @Override
    public void showUnsolvableDialog() {
        javafx.application.Platform.runLater(() -> {
            showFinalScorePanel(); //  Mostra scores antes do alerta

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText("This game can no longer be completed.\nYou lost!...");

            alert.setOnHidden(e -> returnToMenu());
            alert.show();
        });
    }

    @Override
    public void showLevelCompletedDialog() {
        javafx.application.Platform.runLater(() -> {
            scoreManager.addScore(new Score(playerName, model.getLevelName(), moveCount));  // guarda o score

            showFinalScorePanel(); // Depois mostra o painel com os top 3 já atualizados

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("🏆 You did it! 🏆");
            alert.setHeaderText(null);
            alert.setContentText("Good job, you built a snowman! How cool!");

            alert.setOnHidden(e -> {
                // Guarda score
                scoreManager.addScore(new Score(playerName, model.getLevelName(), moveCount));
                gameCompleted();
                model.saveMonsterPositionsToFile();
                model.resetGame();
            });

            alert.show();
        });
    }

    private void showFinalScorePanel() {
        scoreBoxRight.getChildren().clear();

        Label title = new Label("🎯 Final Score");
        Label current = new Label("Player: " + playerName + "\nLevel: " + model.getLevelName() + "\nMoves: " + moveCount);

        List<Score> topScores = scoreManager.getTopScores(model.getLevelName());

        StringBuilder sb = new StringBuilder("🏆 Top 3 Scores:\n");
        for (Score s : topScores) {
            sb.append(s.getPlayerName()).append(" - ").append(s.getMoves()).append(" moves\n");
        }

        Label topScoresLabel = new Label(sb.toString());

        scoreBoxRight.getChildren().addAll(title, current, topScoresLabel);
        scoreBoxRight.setVisible(true);
    }


    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setMinSize(40, 40);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }


    // PICKAXE
    private void loadLevelFromFile(String levelName) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/levels/" + levelName + ".txt"))))) {
            //Para quando for dinamico, meter no metodo(String levelName): new InputStreamReader(getClass().getResourceAsStream("/levels/" + levelName + ".txt")))) {

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
            //if (model.getOnBoardChanged() != null) model.getOnBoardChanged().run();

        } catch (Exception e) {
            System.err.println("Error loading level: " + e.getMessage());
        }
    }


    private ImageView createCell(int row, int col) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(CELL_SIZE);
        imageView.setFitHeight(CELL_SIZE);

        String imagePath = getImagePath(row, col);

        try {
            Image img = new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm(),
                    CELL_SIZE, CELL_SIZE, false, true);
            imageView.setImage(img);
        } catch (NullPointerException e) {
            System.err.println("Error: image not found -> " + imagePath);
        }

        imageView.setOnMouseClicked(event -> handleCellClick(row, col));
        return imageView;
    }

    private String getImagePath(int row, int col) {
        // Se for a posição do monstro, mostrar a imagem do monstro na direção atual
        if (model.getMonsterRow() == row && model.getMonsterCol() == col) {
            String directionName = currentDirection.name().toLowerCase();
            return "/images/monster_" + directionName + ".png";
        }

        // Se existir uma bola de neve na posição, mostrar a imagem correta
        Snowball snowball = model.getSnowballAt(row, col);
        if (snowball != null) {
            return switch (snowball.getStatus()) {
                case SMALL -> "/images/small_snowball.png";
                case MEDIUM -> "/images/medium_snowball.png";
                case LARGE -> "/images/big_snowball.png";
                case MEDIUM_SMALL -> "/images/small_medium_snowballs.png";
                case LARGE_SMALL -> "/images/small_big_snowballs.png";
                case LARGE_MEDIUM -> "/images/medium_big_snowballs.png";
                case FULL_SNOWMAN -> "/images/complete_snowman.png";
            };
        }

        // Para o restante conteúdo do tabuleiro
        return switch (model.getPositionContent(row, col)) {
            case SNOW -> "/images/snow.png";
            case BLOCK -> "/images/block.png";
            case SNOWMAN -> "/images/complete_snowman.png";  // Usar nome correto da imagem
            case NO_SNOW -> "/images/no_snow.png";
            default -> "/images/no_snow.png";
        };
    }

    //TODO tem 21 ";"
    private void handleCellClick(int targetRow, int targetCol) {
        int currentRow = model.getMonsterRow();
        int currentCol = model.getMonsterCol();

        int dRow = targetRow - currentRow;
        int dCol = targetCol - currentCol;

        // Verifica se o movimento é válido (apenas uma célula na horizontal ou vertical)
        if (Math.abs(dRow) + Math.abs(dCol) != 1) return;
        if (!model.canMoveTo(targetRow, targetCol)) return;

        // Salva a posição anterior em notação (linha, letra)
        char oldColLetter = (char) ('A' + currentCol);
        String oldPosition = "(" + (currentRow + 1) + ", " + oldColLetter + ")";

        // Determina a direção
        if (dRow == -1) currentDirection = MonsterDirections.UP;
        if (dRow == 1) currentDirection = MonsterDirections.DOWN;
        if (dCol == -1) currentDirection = MonsterDirections.LEFT;
        if (dCol == 1) currentDirection = MonsterDirections.RIGHT;

        // Move o monstro
        model.moveMonster(currentDirection);
        moveCount++;  // Conta o movimento
        moveCounterLabel.setText("Movements: " + model.getMoveCount());


        // Obtém nova posição
        int newRow = model.getMonsterRow();
        int newCol = model.getMonsterCol();
        char newColLetter = (char) ('A' + newCol);
        String newPosition = "(" + (newRow + 1) + ", " + newColLetter + ")";

        // Atualiza label para mostrar o movimento feito
        monsterPositionLabel.setText("Movimento do Monstro: " + oldPosition + " -> " + newPosition);

        drawBoard();
    }
}
