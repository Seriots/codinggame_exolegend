package com.codingame.game;
import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;

public class Robot {

    public Coord position;
    public double rotation;
    public int bombCount;
    public int points;

    public Sprite sprite;

    public Robot(GraphicEntityModule graphicEntityModule, Coord position, double rotation) {
        this.bombCount = 0;
        this.position = position;
        this.rotation = rotation;
        this.points = 0;

        this.sprite = graphicEntityModule.createSprite().setImage(Constants.ROBOT_SPRITE)
                .setX(this.position.x * Constants.CELL_SIZE + Constants.CELL_OFFSET_X)
                .setY(this.position.y * Constants.CELL_SIZE + Constants.CELL_OFFSET_Y)
                .setAnchor(.5)
                .setZIndex(5)
                .setScale(Constants.ROBOT_SCALE)
                .setRotation(this.rotation);
    }

    private boolean isWallInTrajectory(Action action, Maze maze) {
        Wall wall;

        if (this.rotation == 0) {
            wall = (action == Action.MOVE_FORWARD ? Wall.RIGHT: Wall.LEFT);
        } else if (this.rotation == Math.PI / 2) {
            wall = (action == Action.MOVE_FORWARD ? Wall.BOTTOM: Wall.TOP);
        } else if (this.rotation == -Math.PI / 2) {
            wall = (action == Action.MOVE_FORWARD ? Wall.TOP: Wall.BOTTOM);
        } else {
            wall = (action == Action.MOVE_FORWARD ? Wall.LEFT: Wall.RIGHT);
        }

        return wall == Wall.TOP && maze.cells[this.position.y][this.position.x].topWall
                || wall == Wall.BOTTOM && maze.cells[this.position.y][this.position.x].bottomWall
                || wall == Wall.LEFT && maze.cells[this.position.y][this.position.x].leftWall
                || wall == Wall.RIGHT && maze.cells[this.position.y][this.position.x].rightWall;
    }

    public boolean move(Action action, Maze maze) {
        if (action == Action.MOVE_FORWARD) {
            if (this.isWallInTrajectory(action, maze)) {
                this.sprite.setX((int)((double)(this.position.x + 0.25 * Math.cos(this.rotation)) * Constants.CELL_SIZE) + Constants.CELL_OFFSET_X, Curve.LINEAR)
                        .setY((int)((double)(this.position.y + 0.25 * Math.sin(this.rotation)) * Constants.CELL_SIZE) + Constants.CELL_OFFSET_Y, Curve.LINEAR)
                        .setScaleX(Constants.ROBOT_CRASH_SCALE_X, Curve.EASE_IN_AND_OUT)
                        .setZIndex(3)
                        .setRotation(this.rotation, Curve.LINEAR);
                return false;
            }
            this.position.x = (int)(this.position.x + 1 * Math.cos(this.rotation));
            this.position.y = (int)(this.position.y + 1 * Math.sin(this.rotation));
        }
        else if (action == Action.MOVE_BACKWARD) {
            if (this.isWallInTrajectory(action, maze)) {
                this.sprite.setX((int)((double)(this.position.x - 0.40 * Math.cos(this.rotation)) * Constants.CELL_SIZE) + Constants.CELL_OFFSET_X, Curve.LINEAR)
                        .setY((int)((double)(this.position.x - 0.40 * Math.sin(this.rotation)) * Constants.CELL_SIZE) + Constants.CELL_OFFSET_Y, Curve.LINEAR)
                        .setScaleX(Constants.ROBOT_CRASH_SCALE_X, Curve.EASE_IN)
                        .setZIndex(3)
                        .setRotation(this.rotation, Curve.LINEAR);
                return false;
            }
            this.position.x = (int)(this.position.x - 1 * Math.cos(this.rotation));
            this.position.y = (int)(this.position.y - 1 * Math.sin(this.rotation));
        }
        else if (action == Action.TURN_RIGHT) {
            this.rotation += Math.PI / 2;
            if (this.rotation > Math.PI) {
                this.rotation -= 2 * Math.PI;
            }
            if (this.rotation == -Math.PI)
                this.rotation = Math.PI;
        }
        else if (action == Action.TURN_LEFT) {
            this.rotation -= Math.PI / 2;
            if (this.rotation < -Math.PI) {
                this.rotation += 2 * Math.PI;
            }
            if (this.rotation == -Math.PI)
                this.rotation = Math.PI;
        }
        else if (action == Action.DROP) {
            if (this.bombCount > 0 && maze.dropBomb(this.position)) {
                this.bombCount -= 1;
            }
        }
        this.sprite.setX(this.position.x * Constants.CELL_SIZE + Constants.CELL_OFFSET_X, Curve.LINEAR)
                .setY(this.position.y * Constants.CELL_SIZE + Constants.CELL_OFFSET_Y, Curve.LINEAR)
                .setRotation(this.rotation, Curve.EASE_OUT);

        return true;
    }

    public boolean tryGetbomb(Maze maze) {
        if (maze.playerTryToGetBomb(this.position)) {
            this.bombCount += 1;
            this.points += 1;
            return true;
        }
        return false;
    }

    public void explode() {
        this.sprite.setSkewX(0.5, Curve.EASE_OUT);
        this.sprite.setSkewY(0.5, Curve.EASE_OUT);
    }

    public String toString() {
        int rotation_direction;

        if (this.rotation == 0) {
            rotation_direction = 1;
        } else if (this.rotation == Math.PI / 2) {
            rotation_direction = 3;
        } else if (this.rotation == -Math.PI / 2) {
            rotation_direction = 2;
        } else {
            rotation_direction = 0;
        }
        return position.toString() + " " + rotation_direction + " " + bombCount;
    }
}
