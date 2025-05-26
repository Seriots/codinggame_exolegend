package com.codingame.game;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.codingame.gameengine.module.entities.World;

public class Constants {
    public static final int VIEWER_WIDTH = World.DEFAULT_WIDTH;
    public static final int VIEWER_HEIGHT = World.DEFAULT_HEIGHT;
    
    public static final int CELL_SIZE = 170;
    public static final int CELL_OFFSET_X = 190;
    public static final int CELL_OFFSET_Y = 110;
    public static final int COLUMNS = 10;
    public static final int ROWS = 6;
    public static final double WALL_SCALE_X = 0.33;
    public static final double WALL_SCALE_Y = 0.1;
    public static final double ROBOT_SCALE = 0.35;
    public static final double ROBOT_CRASH_SCALE_X = 0.20;
    public static final double COLLECTIBLE_BOMB_SCALE = 0.18;
    public static final int DROPPED_BOMB_COUNTDOWN = 6;

    public static final String BACKGROUND_SPRITE = "backgroundGreen.png";
    public static final String ROBOT_SPRITE = "robot.png";
    public static final String WALL_SPRITE = "arena-a/full.gif";
    public static final String COLLECTIBLE_BOMB_SPRITE = "collectible_bomb.png";
    public static final String DROPPED_BOMB_SPRITE = "dropped_bomb.gif";

    public static final String MOVE_FORWARD_ACTION = "MOVE_FORWARD";
    public static final String MOVE_BACKWARD_ACTION = "MOVE_BACKWARD";
    public static final String TURN_RIGHT_ACTION = "TURN_RIGHT";
    public static final String TURN_LEFT_ACTION = "TURN_LEFT";
    public static final String DROP_ACTION = "DROP";

    public static final String[] ACTIONS = new String[] { MOVE_FORWARD_ACTION, MOVE_BACKWARD_ACTION, TURN_RIGHT_ACTION, TURN_LEFT_ACTION, DROP_ACTION };
}
