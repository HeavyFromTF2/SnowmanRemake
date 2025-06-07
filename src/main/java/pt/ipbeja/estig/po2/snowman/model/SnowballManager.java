package pt.ipbeja.estig.po2.snowman.model;

import java.util.List;

public class SnowballManager {

    private final BoardModel boardModel;
    private final List<Snowball> snowballs;

    public SnowballManager(BoardModel boardModel) {
        this.boardModel = boardModel;
        this.snowballs = boardModel.getSnowballs();
    }

    public Snowball getSnowballAt(int row, int col) {
        for (Snowball ball : snowballs) {
            if (ball.getRow() == row && ball.getCol() == col) return ball;
        }
        return null;
    }

    public boolean tryToPushSnowball(int fromRow, int fromCol, int rowOffSet, int colOffSet) {
        int toRow = fromRow + rowOffSet;
        int toCol = fromCol + colOffSet;

        if (!boardModel.isInsideBoard(toRow, toCol)) return false;
        if (boardModel.getPositionContent(toRow, toCol) == PositionContent.BLOCK) return false;

        Snowball originalSnowball = getSnowballAt(fromRow, fromCol);
        if (originalSnowball == null) return false;

        Snowball target = getSnowballAt(toRow, toCol);
        if (target != null) {
            return tryToStack(originalSnowball, target, toRow, toCol);
        }

        return handleSnowballMovement(originalSnowball, toRow, toCol);
    }

    private boolean handleSnowballMovement(Snowball snowball, int toRow, int toCol) {
        Snowball actualBall = snowball;

        switch (snowball.getStatus()) {
            case MEDIUM_SMALL -> {
                snowball.setStatus(SnowballStatus.MEDIUM);
                actualBall = new Snowball(toRow, toCol, SnowballStatus.SMALL);
                snowballs.add(actualBall);
            }
            case LARGE_SMALL -> {
                snowball.setStatus(SnowballStatus.LARGE);
                actualBall = new Snowball(toRow, toCol, SnowballStatus.SMALL);
                snowballs.add(actualBall);
            }
            case LARGE_MEDIUM -> {
                snowball.setStatus(SnowballStatus.LARGE);
                actualBall = new Snowball(toRow, toCol, SnowballStatus.MEDIUM);
                snowballs.add(actualBall);
            }
            default -> actualBall.setPosition(toRow, toCol);
        }

        if (boardModel.getPositionContent(toRow, toCol) == PositionContent.SNOW &&
                (actualBall.getStatus() == SnowballStatus.SMALL || actualBall.getStatus() == SnowballStatus.MEDIUM)) {
            actualBall.growSnowball();
            boardModel.setPositionContent(toRow, toCol, PositionContent.NO_SNOW);
        }

        return true;
    }

    private boolean tryToStack(Snowball moving, Snowball target, int row, int col) {
        if (boardModel.getPositionContent(row, col) == PositionContent.SNOW &&
                (moving.getStatus() == SnowballStatus.SMALL || moving.getStatus() == SnowballStatus.MEDIUM)) {
            moving.growSnowball();
            boardModel.setPositionContent(row, col, PositionContent.NO_SNOW);
        }

        SnowballStatus newStatus = switch (target.getStatus()) {
            case LARGE -> switch (moving.getStatus()) {
                case MEDIUM -> SnowballStatus.LARGE_MEDIUM;
                case SMALL -> SnowballStatus.LARGE_SMALL;
                default -> null;
            };
            case MEDIUM -> switch (moving.getStatus()) {
                case SMALL -> SnowballStatus.MEDIUM_SMALL;
                default -> null;
            };
            case LARGE_MEDIUM -> moving.getStatus() == SnowballStatus.SMALL ? SnowballStatus.FULL_SNOWMAN : null;
            default -> null;
        };

        if (newStatus == null) return false;

        target.setStatus(newStatus);
        snowballs.remove(moving);

        if (newStatus == SnowballStatus.FULL_SNOWMAN) {
            boardModel.setPositionContent(row, col, PositionContent.SNOWMAN);
        }

        return true;
    }
}
