package pt.ipbeja.app.ui;

import javafx.application.Application;
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
    private int moveCount = 0;

    @Override
    public void start(Stage primaryStage) {
        this.model = new BoardModel(10, 10);
        this.model.setOnBoardChanged(this::drawBoard);
        this.grid = new GridPane();

        model.setPositionContent(7, 4, PositionContent.SNOW);
        model.setPositionContent(7, 5, PositionContent.SNOW);
        model.setPositionContent(7, 6, PositionContent.SNOW);
        model.setPositionContent(7, 7, PositionContent.SNOW);
        model.setPositionContent(7, 8, PositionContent.SNOW);

        model.getSnowballs().add(new Snowball(5, 3, SnowballStatus.SMALL));
        model.getSnowballs().add(new Snowball(5, 4, SnowballStatus.MEDIUM));
        model.getSnowballs().add(new Snowball(5, 6, SnowballStatus.LARGE));


        model.setOnGameCompleted(() -> {
            model.saveGameToFile(moveLog.getText(), moveCount);
        });

        drawBoard();

        BorderPane mainLayout = createMainLayout();

        Scene scene = new Scene(mainLayout, 10 * CELL_SIZE, 10 * CELL_SIZE + 100);
        primaryStage.setTitle("Snowball Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private BorderPane createMainLayout() {
        moveLog = new TextArea();
        moveLog.setEditable(false);
        moveLog.setPrefHeight(100);

        moveCounterLabel = new Label("Movements: 0");

        VBox bottomBox = new VBox(moveCounterLabel, moveLog);
        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(grid);
        mainLayout.setBottom(bottomBox);

        return mainLayout;
    }

    private void drawBoard() {
        grid.getChildren().clear();
        int rows = model.getRowCount();
        int cols = model.getColCount();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid.add(createCell(row, col), col, row);
            }
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

        if (Math.abs(dRow) + Math.abs(dCol) != 1) return;
        if (!model.canMoveTo(targetRow, targetCol)) return;

        if (dRow == -1) currentDirection = MonsterDirections.UP;
        if (dRow == 1) currentDirection = MonsterDirections.DOWN;
        if (dCol == -1) currentDirection = MonsterDirections.LEFT;
        if (dCol == 1) currentDirection = MonsterDirections.RIGHT;

        model.moveMonster(currentDirection);
        moveCount++;  // conta o movimento
        moveCounterLabel.setText("Movements: " + moveCount);
        drawBoard();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
