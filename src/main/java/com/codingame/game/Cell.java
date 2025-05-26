package com.codingame.game;

import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Rectangle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cell {
    private final GraphicEntityModule graphicEntityModule;

    public boolean collectibleBomb;
    public boolean droppedBomb;
    public int droppedBombCountdown;
    public boolean leftWall;
    public boolean rightWall;
    public boolean topWall;
    public boolean bottomWall;

    public int x;
    public int y;

    private Sprite cellSprite;
    private Rectangle coloredBackground;
    private boolean backgroundColored = false;

    public Cell(GraphicEntityModule graphicEntityModule, boolean left, boolean right, boolean top, boolean bottom, int x, int y) {
        this.graphicEntityModule = graphicEntityModule;
        this.leftWall = left;
        this.rightWall = right;
        this.topWall = top;
        this.bottomWall = bottom;
        this.x = x;
        this.y = y;
    }

    public void ShowWalls() {
        if (this.leftWall) {
            graphicEntityModule.createSprite().setImage(Constants.WALL_SPRITE)
                    .setX(this.x * Constants.CELL_SIZE + Constants.CELL_OFFSET_X - Constants.CELL_SIZE / 2)
                    .setY(this.y * Constants.CELL_SIZE + Constants.CELL_OFFSET_Y)
                    .setAnchor(.5)
                    .setZIndex(4)
                    .setScaleX(Constants.WALL_SCALE_X)
                    .setScaleY(Constants.WALL_SCALE_Y)
                    .setRotation(Math.PI / 2);
        }
        if (this.rightWall) {
            graphicEntityModule.createSprite().setImage(Constants.WALL_SPRITE)
                    .setX(this.x * Constants.CELL_SIZE + Constants.CELL_OFFSET_X + Constants.CELL_SIZE / 2)
                    .setY(this.y * Constants.CELL_SIZE + Constants.CELL_OFFSET_Y)
                    .setAnchor(.5)
                    .setZIndex(4)
                    .setScaleX(Constants.WALL_SCALE_X)
                    .setScaleY(Constants.WALL_SCALE_Y)
                    .setRotation(Math.PI / 2);
        }
        if (this.topWall) {
            graphicEntityModule.createSprite().setImage(Constants.WALL_SPRITE)
                    .setX(this.x * Constants.CELL_SIZE + Constants.CELL_OFFSET_X)
                    .setY(this.y * Constants.CELL_SIZE + Constants.CELL_OFFSET_Y - Constants.CELL_SIZE / 2)
                    .setAnchor(.5)
                    .setZIndex(4)
                    .setScaleX(Constants.WALL_SCALE_X)
                    .setScaleY(Constants.WALL_SCALE_Y);
        }
        if (this.bottomWall) {
            graphicEntityModule.createSprite().setImage(Constants.WALL_SPRITE)
                    .setX(this.x * Constants.CELL_SIZE + Constants.CELL_OFFSET_X)
                    .setY(this.y * Constants.CELL_SIZE + Constants.CELL_OFFSET_Y + Constants.CELL_SIZE / 2)
                    .setAnchor(.5)
                    .setZIndex(4)
                    .setScaleX(Constants.WALL_SCALE_X)
                    .setScaleY(Constants.WALL_SCALE_Y);
        }
    }

    public Wall breakOneWall(int size_x, int size_y, Random rand) {
        List<Wall> walls = new ArrayList<>();
        if (this.x != 0 && this.leftWall) {
            walls.add(Wall.LEFT);
        }
        if (this.x != size_x - 1 && this.rightWall) {
            walls.add(Wall.RIGHT);
        }
        if (this.y != 0 && this.topWall) {
            walls.add(Wall.TOP);
        }
        if (this.y != size_y - 1 && this.bottomWall) {
            walls.add(Wall.BOTTOM);
        }

        if (walls.isEmpty()) {
            return Wall.NONE;
        }
        Wall wallSelected = walls.get(rand.nextInt(walls.size()));
        if (wallSelected == Wall.LEFT) {
            this.leftWall = false;
        } else if (wallSelected == Wall.RIGHT) {
            this.rightWall = false;
        } else if (wallSelected == Wall.TOP) {
            this.topWall = false;
        } else if (wallSelected == Wall.BOTTOM) {
            this.bottomWall = false;
        }
        return wallSelected;
    }

    public void reportBrokenWall(Wall wall) {
        if (wall == Wall.LEFT) {
            this.rightWall = false;
        } else if (wall == Wall.RIGHT) {
            this.leftWall = false;
        } else if (wall == Wall.TOP) {
            this.bottomWall = false;
        } else if (wall == Wall.BOTTOM) {
            this.topWall = false;
        }
    }

    public boolean generateBomb() {
        if (this.droppedBomb || this.collectibleBomb) {
            return false;
        }
        this.collectibleBomb = true;
        cellSprite = graphicEntityModule.createSprite().setImage(Constants.COLLECTIBLE_BOMB_SPRITE)
                .setX(this.x * Constants.CELL_SIZE + Constants.CELL_OFFSET_X)
                .setY(this.y * Constants.CELL_SIZE + Constants.CELL_OFFSET_Y)
                .setAnchor(.5)
                .setScale(Constants.COLLECTIBLE_BOMB_SCALE)
                .setZIndex(1);
        return true;
    }

    public void colorBackground(Robot robot) {
        if (!this.backgroundColored) {
            this.backgroundColored = true;
            this.coloredBackground = graphicEntityModule.createRectangle()
                    .setFillColor(0xEE1531)
                    .setX(this.x * Constants.CELL_SIZE + Constants.CELL_OFFSET_X - Constants.CELL_SIZE / 2)
                    .setY(this.y * Constants.CELL_SIZE + Constants.CELL_OFFSET_Y - Constants.CELL_SIZE / 2)
                    .setZIndex(0)  // Set to lowest z-index to be behind everything
                    .setWidth(Constants.CELL_SIZE)  // Adjust scale to cover the cell
                    .setHeight(Constants.CELL_SIZE);  // Adjust scale to cover the cell

            robot.points += 1;
        }
    }


    public void dropBomb() {
        this.droppedBomb = true;
        this.droppedBombCountdown = Constants.DROPPED_BOMB_COUNTDOWN;
        cellSprite = graphicEntityModule.createSprite().setImage(Constants.DROPPED_BOMB_SPRITE)
                .setX(this.x * Constants.CELL_SIZE + Constants.CELL_OFFSET_X)
                .setY(this.y * Constants.CELL_SIZE + Constants.CELL_OFFSET_Y)
                .setAnchor(.5)
                .setScale(Constants.COLLECTIBLE_BOMB_SCALE)
                .setZIndex(1);
    }

    public boolean updateBomb(Maze maze, Robot robot) {
        this.droppedBombCountdown -= 1;
        if (this.droppedBombCountdown <= 0) {
            this.droppedBomb = false;
            cellSprite.setAlpha(0.0, Curve.EASE_OUT);
            return true;
        }
        else {
            cellSprite.setScale(Constants.COLLECTIBLE_BOMB_SCALE * (1 + ((Constants.DROPPED_BOMB_COUNTDOWN - this.droppedBombCountdown) / 10.0)), Curve.LINEAR);
            return false;
        }
    }

    public boolean tryGetBomb() {
        if (this.collectibleBomb) {
            collectibleBomb = false;
            cellSprite.setScale(0.0, Curve.EASE_OUT);
            return true ;
        }
        return false;
    }

    public int getWallValue() {
        return ((this.leftWall ? 1 : 0) |
                ((this.rightWall ? 1 : 0) << 1) |
                ((this.topWall ? 1 : 0) << 2) |
                ((this.bottomWall ? 1 : 0) << 3));
    }

    public String toString() {
        return (leftWall ? "1" : "0")  + " " + (rightWall ? "1" : "0") + " " + (topWall ? "1" : "0") + " " + (bottomWall ? "1" : "0") + " " + (collectibleBomb ? "1" : "0");
    }
}
