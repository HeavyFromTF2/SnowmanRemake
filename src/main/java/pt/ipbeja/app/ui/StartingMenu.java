package pt.ipbeja.app.ui;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class StartingMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Título do menu
        Label title = new Label("A Good Snowman is Hard to Build");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        // Botões para os níveis
        Button level1Button = new Button("Level 1");
        Button level2Button = new Button("Level 2");

        // Estiliza os botões para serem maiores e com espaçamento
        level1Button.setPrefWidth(200);
        level1Button.setPrefHeight(50);
        level2Button.setPrefWidth(200);
        level2Button.setPrefHeight(50);

        level1Button.setStyle("-fx-font-size: 16px;");
        level2Button.setStyle("-fx-font-size: 16px;");

        level1Button.setOnAction(e -> openLevel(primaryStage, "level1"));
        level2Button.setOnAction(e -> openLevel(primaryStage, "level2"));

        // VBox com espaçamento e alinhamento centrado
        VBox layout = new VBox(30, title, level1Button, level2Button);
        layout.setStyle("-fx-padding: 40; -fx-alignment: center; -fx-background-color: #f0f8ff; -fx-border-color: #2E8B57; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10;");


        Scene scene = new Scene(layout, 600, 500);
        primaryStage.setTitle("Starting Menu of the game - A Good Snowman is Hard to Build");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openLevel(Stage menuStage, String levelName) {
        menuStage.close(); // close the menu window

        SnowballGame game = new SnowballGame();

        try {
            game.startFromFile(levelName); // call method with level name (without .txt)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}