package pt.ipbeja.app.ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pt.ipbeja.app.model.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;


/**
 * The JavaFX main game window.
 */
public class SnowballGame extends Application {

    private static final int CELL_SIZE = 60;

    private BoardModel model;
    private GridPane grid;

    private MonsterDirections currentDirection = MonsterDirections.DOWN;

    private TextArea moveLog;
    private Label moveCounterLabel;
    private Label monsterPositionLabel;

    private int moveCount = 0;

    @Override
    public void start(Stage primaryStage) {
        this.model = new BoardModel(10, 10); // Adjust size to match level
        this.model.setOnBoardChanged(this::drawBoard);
        this.grid = new GridPane();

        loadLevelFromFile("nivel2");

        BorderPane mainLayout = createMainLayout();
        drawBoard();

        Scene scene = new Scene(mainLayout, 10 * CELL_SIZE, 10 * CELL_SIZE + 40);
        primaryStage.setTitle("Snowball Game");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

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

        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(grid);
        mainLayout.setBottom(bottomBox);

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
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/levels/nivel2.txt"))))) {
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

            if (model.getOnBoardChanged() != null) model.getOnBoardChanged().run();

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
        moveCounterLabel.setText("Movements: " + moveCount);

        // Obtém nova posição
        int newRow = model.getMonsterRow();
        int newCol = model.getMonsterCol();
        char newColLetter = (char) ('A' + newCol);
        String newPosition = "(" + (newRow + 1) + ", " + newColLetter + ")";

        // Atualiza label para mostrar o movimento feito
        monsterPositionLabel.setText("Movimento do Monstro: " + oldPosition + " -> " + newPosition);

        drawBoard();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
