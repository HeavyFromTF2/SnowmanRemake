package pt.ipbeja.po2.app.model;

import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.model.BoardModel;
import pt.ipbeja.estig.po2.snowman.model.PositionContent;

import static org.junit.jupiter.api.Assertions.*;

class BoardModelTest {

    // TODO fazer pelo menos um teste melhor que este, é mt genérico

    @Test
    void testInitialBoardHasNoSnowInsideAndBlocksOnBorders() {
        BoardModel board = new BoardModel(5, 5);

        for (int row = 0; row < board.getRowCount(); row++) {
            for (int col = 0; col < board.getColCount(); col++) {
                PositionContent content = board.getPositionContent(row, col);

                // Bordas devem ser BLOCK
                if (row == 0 || row == board.getRowCount() - 1 ||
                        col == 0 || col == board.getColCount() - 1) {
                    assertEquals(PositionContent.BLOCK, content,
                            "Border at (" + row + "," + col + ") should be BLOCK");
                }
                // Interior deve ser SNOW
                else {
                    assertEquals(PositionContent.NO_SNOW, content,
                            "Inner cell at (" + row + "," + col + ") should be NO_SNOW");
                }
            }
        }
    }


}