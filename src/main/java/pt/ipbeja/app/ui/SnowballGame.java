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
        this.model = new BoardModel(5, 7); // tabuleiro 5x7
        this.grid = new GridPane();

        drawBoard();

        Scene scene = new Scene(grid, 7 * CELL_SIZE, 5 * CELL_SIZE);

        // Detetar teclas e mover o monstro com método do modelo
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                model.moveMonster(MonsterDirections.UP);
                currentDirection = MonsterDirections.UP;
            }
            if (event.getCode() == KeyCode.DOWN) {
                model.moveMonster(MonsterDirections.DOWN);
                currentDirection = MonsterDirections.DOWN;
            }
            if (event.getCode() == KeyCode.LEFT) {
                model.moveMonster(MonsterDirections.LEFT);
                currentDirection = MonsterDirections.LEFT;
            }
            if (event.getCode() == KeyCode.RIGHT) {
                model.moveMonster(MonsterDirections.RIGHT);
                currentDirection = MonsterDirections.RIGHT;
            }
            drawBoard();
        });

        primaryStage.setTitle("Snowball Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Necessário para receber eventos de teclado
        scene.getRoot().requestFocus();
    }

    /**
     * Atualiza o tabuleiro gráfico com base na posição do monstro e conteúdo do chão.
     */
    private void drawBoard() {
        grid.getChildren().clear();

        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColCount(); col++) {
                ImageView imageView = new ImageView();
                imageView.setFitWidth(CELL_SIZE);
                imageView.setFitHeight(CELL_SIZE);

                String imagePath;

                if (model.getMonsterRow() == row && model.getMonsterCol() == col) {
                    String directionName = currentDirection.name().toLowerCase(); // ex: "down", "up", etc.
                    imagePath = "/images/monster_" + directionName + ".png"; // ex: "/images/monster_down.png"
                } else {
                    PositionContent content = model.getPositionContent(row, col);
                    switch (content) {
                        case SNOW -> imagePath = "/images/snow.png";
                        case BLOCK -> imagePath = "/images/block.png";
                        case SNOWMAN -> imagePath = "/images/snowman_complete.png";
                        case NO_SNOW -> imagePath = "/images/no_snow.png";
                        default -> imagePath = "/images/no_snow.png";
                    }
                }

                try {
                    Image img = new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm(), CELL_SIZE, CELL_SIZE, false, true);
                    imageView.setImage(img);
                } catch (NullPointerException e) {
                    System.err.println("Erro: imagem não encontrada -> " + imagePath);
                }

                grid.add(imageView, col, row);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
