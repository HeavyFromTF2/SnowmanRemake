/**
 * Martim Dias - 24290
 * Classe responsável pelo menu inicial do jogo
 */

package pt.ipbeja.estig.po2.snowman.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartingMenu extends Application {

    /**
     * Métod0 principal da interface gráfica. Cria todos os elementos visuais do menu inicial.
     * @param primaryStage Janela principal da aplicação
     */
    @Override
    public void start(Stage primaryStage) {
        Label title = new Label("A Good Snowman is Hard to Build");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        Label nameLabel = new Label("Enter your name (3 letters total):");
        TextField nameField = createNameField();
        Button level1Button = createLevelButton("Level 1", nameField, primaryStage, "level1");
        Button level2Button = createLevelButton("Level 2", nameField, primaryStage, "level2");

        VBox layout = new VBox(20, title, nameLabel, nameField, level1Button, level2Button);
        layout.setStyle("-fx-padding: 40; -fx-alignment: center; -fx-background-color: #f0f8ff;");

        // Configuração da cena e apresentação
        Scene scene = new Scene(layout, 600, 500);
        primaryStage.setTitle("Starting Menu of the game - A Good Snowman is Hard to Build");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Cria o campo de texto onde o jogador insere o nome.
     * @return Campo de texto configurado
     */
    private TextField createNameField() {
        TextField field = new TextField();
        field.setPromptText("EX. JOE");
        field.setMaxWidth(100);
        return field;
    }

    /**
     * Cria um botão para iniciar um nível específico do jogo.
     * @param text Texto a mostrar no botão
     * @param nameField Campo de texto com o nome do jogador
     * @param stage Janela atual a ser fechada
     * @param level Nome do ficheiro do nível (sem extensão .txt)
     * @return Botão configurado
     */
    private Button createLevelButton(String text, TextField nameField, Stage stage, String level) {
        Button button = new Button(text);
        button.setPrefSize(200, 50);
        button.setStyle("-fx-font-size: 16px;");

        // Ação a executar quando o botão é clicado
        button.setOnAction(e -> {
            String name = nameField.getText().trim().toUpperCase(); // Remove espaços e converte para maiúsculas
            if (name.length() == 3) {
                openLevel(stage, level, name);
            } else {
                nameField.setStyle("-fx-border-color: red;");
            }
        });
        return button;
    }

    /**
     * Inicia o jogo, abrindo o nível escolhido com o nome do jogador.
     * Fecha o menu inicial.
     *
     * @param menuStage Janela atual do menu
     * @param levelName Nome do nível a carregar (sem ".txt")
     * @param playerName Nome do jogador (3 letras)
     */
    private void openLevel(Stage menuStage, String levelName, String playerName) {
        menuStage.close();

        SnowballGame game = new SnowballGame();
        game.setPlayerName(playerName);

        try {
            game.startFromFile(levelName); // Carrega o nível escolhido no botão do menu
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Métod0 principal que arranca a aplicação JavaFX.
     * @param args Argumentos da linha de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}