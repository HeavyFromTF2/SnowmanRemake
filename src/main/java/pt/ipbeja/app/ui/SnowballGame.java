package pt.ipbeja.app.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pt.ipbeja.app.model.BoardModel;
import pt.ipbeja.app.model.MonsterDirections;
import pt.ipbeja.app.model.PositionContent;

import java.util.Objects;

/**
 * The JavaFX main game window.
 */
public class SnowballGame extends Application {

    private static final int CELL_SIZE = 60;

    private BoardModel model;
    private GridPane grid;
    private MonsterDirections currentDirection = MonsterDirections.DOWN;

    @Override
    public void start(Stage primaryStage) {
        this.model = new BoardModel(5, 7);
        this.grid = new GridPane();

        drawBoard();

        Scene scene = new Scene(grid, 7 * CELL_SIZE, 5 * CELL_SIZE);
        primaryStage.setTitle("Snowball Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Redraws the full board grid.
     */
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

    /**
     * Creates a single cell (ImageView) with image and click handler.
     *
     * @param row the row index
     * @param col the column index
     * @return the ImageView representing this cell
     */
    private ImageView createCell(int row, int col) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(CELL_SIZE);
        imageView.setFitHeight(CELL_SIZE);

        String imagePath = getImagePath(row, col);

        // Attempts to load the cell image; prints error if image not found
        try {
            Image img = new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm(),
                    CELL_SIZE, CELL_SIZE, false, true);
            imageView.setImage(img);
        } catch (NullPointerException e) {
            System.err.println("Erro: imagem não encontrada -> " + imagePath);
        }

        imageView.setOnMouseClicked(event -> handleCellClick(row, col));
        return imageView;
    }

    /**
     * Determines the correct image path for a given cell.
     */
    private String getImagePath(int row, int col) {
        if (model.getMonsterRow() == row && model.getMonsterCol() == col) {
            String directionName = currentDirection.name().toLowerCase();
            return "/images/monster_" + directionName + ".png";
        }
        return switch (model.getPositionContent(row, col)) {
            case SNOW -> "/images/snow.png";
            case BLOCK -> "/images/block.png";
            case SNOWMAN -> "/images/snowman_complete.png";
            case NO_SNOW -> "/images/no_snow.png";
            default -> "/images/no_snow.png";
        };
    }

    /**
     * Handles clicks on a board cell. Moves the monster if valid.
     */
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
        drawBoard();
    }

    public static void main(String[] args) {
        launch(args);
    }
}