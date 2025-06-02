package pt.ipbeja.app.ui;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class StartingMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label title = createTitleLabel();
        Label nameLabel = new Label("Enter your name (3 letters total):");
        TextField nameField = createNameField();
        Button level1Button = createLevelButton("Level 1", nameField, primaryStage, "level1");
        Button level2Button = createLevelButton("Level 2", nameField, primaryStage, "level2");

        VBox layout = new VBox(20, title, nameLabel, nameField, level1Button, level2Button);
        layout.setStyle("-fx-padding: 40; -fx-alignment: center; -fx-background-color: #f0f8ff; -fx-border-color: #2E8B57; -fx-border-width: 3; -fx-border-radius: 10; -fx-background-radius: 10;");

        Scene scene = new Scene(layout, 600, 500);
        primaryStage.setTitle("Starting Menu of the game - A Good Snowman is Hard to Build");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createTitleLabel() {
        Label title = new Label("A Good Snowman is Hard to Build");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        return title;
    }

    private TextField createNameField() {
        TextField field = new TextField();
        field.setPromptText("e.g. ABC");
        field.setMaxWidth(100);
        return field;
    }

    private Button createLevelButton(String text, TextField nameField, Stage stage, String level) {
        Button button = new Button(text);
        button.setPrefSize(200, 50);
        button.setStyle("-fx-font-size: 16px;");
        button.setOnAction(e -> {
            String name = nameField.getText().trim().toUpperCase();
            if (name.length() == 3) {
                openLevel(stage, level, name);
            } else {
                nameField.setStyle("-fx-border-color: red;");
            }
        });
        return button;
    }

    private void openLevel(Stage menuStage, String levelName, String playerName) {
        menuStage.close(); // close the menu window

        SnowballGame game = new SnowballGame();
        game.setPlayerName(playerName);

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