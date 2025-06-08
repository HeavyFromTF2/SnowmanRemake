package pt.ipbeja.estig.po2.snowman.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import pt.ipbeja.estig.po2.snowman.model.*;

import java.util.Objects;

/**
 * Martim Dias - 24290
 *
 * Responsável por desenhar o tabuleiro do jogo.
 * Cuida da renderização das células, cabeçalhos e imagens.
 */
public class BoardRenderer {

    private final int cellSize;
    private final BoardModel model;
    private final GridPane grid;
    private MonsterDirections currentDirection;
    private final CellClickHandler clickHandler;

    /**
     * Interface para tratar dos cliques nas células do tabuleiro.
     */
    public interface CellClickHandler {
        void onCellClicked(int row, int col);
    }

    /**
     * Construtor do BoardRenderer.
     * @param model            modelo de jogo com a lógica e estado do tabuleiro
     * @param grid             GridPane JavaFX onde será desenhado o tabuleiro
     * @param cellSize         tamanho de cada célula em píxeis
     * @param initialDirection direção inicial do monstro
     * @param handler          função para tratar cliques nas células
     */
    public BoardRenderer(BoardModel model, GridPane grid, int cellSize, MonsterDirections initialDirection, CellClickHandler handler) {
        this.model = model;
        this.grid = grid;
        this.cellSize = cellSize;
        this.currentDirection = initialDirection;
        this.clickHandler = handler;
    }

    /**
     * Atualiza a direção atual do monstro.
     *
     * @param direction nova direção
     */
    public void setCurrentDirection(MonsterDirections direction) {
        this.currentDirection = direction;
    }

    /**
     * Renderiza visualmente o tabuleiro, incluindo cabeçalhos de linhas/colunas e todas as células.
     * Atualiza o GridPane com os elementos gráficos atuais do estado do modelo.
     */
    public void drawBoard() {
        grid.getChildren().clear();

        int rows = model.getRowCount();
        int cols = model.getColCount();

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

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid.add(createCell(row, col), col + 1, row + 1);
            }
        }
    }

    /**
     * Cria uma label de cabeçalho (linha ou coluna).
     *
     * @param text texto a mostrar
     * @return Label JavaFX com estilo aplicado
     */
    private Label createHeaderLabel(String text) {
        Label label = new Label(text);
        label.setMinSize(40, 40);
        label.setAlignment(Pos.CENTER);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    /**
     * Cria a célula visual para uma posição do tabuleiro.
     *
     * @param row linha
     * @param col coluna
     * @return ImageView com a imagem apropriada e evento de clique
     */
    private ImageView createCell(int row, int col) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(cellSize);
        imageView.setFitHeight(cellSize);

        String imagePath = getImagePath(row, col);

        try {
            Image img = new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm(),
                    cellSize, cellSize, false, true);
            imageView.setImage(img);
        } catch (NullPointerException e) {
            System.err.println("Image not found: " + imagePath);
        }

        imageView.setOnMouseClicked(e -> clickHandler.onCellClicked(row, col));
        return imageView;
    }

    /**
     * Retorna o caminho da imagem a desenhar numa célula,
     * dependendo do conteúdo (monstro, neve, boneco, etc.).
     *
     * @param row linha
     * @param col coluna
     * @return caminho relativo da imagem
     */
    private String getImagePath(int row, int col) {
        if (model.getMonsterRow() == row && model.getMonsterCol() == col) {
            String directionName = currentDirection.name().toLowerCase();
            return "/images/monster_" + directionName + ".png";
        }

        Snowball snowball = model.getSnowballManager().getSnowballAt(row, col);
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

        return switch (model.getPositionContent(row, col)) {
            case SNOW -> "/images/snow.png";
            case BLOCK -> "/images/block.png";
            case SNOWMAN -> "/images/complete_snowman.png";
            case NO_SNOW -> "/images/no_snow.png";
        };
    }
}
