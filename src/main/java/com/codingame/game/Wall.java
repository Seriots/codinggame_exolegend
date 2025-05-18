package com.codingame.game;

public enum Wall {
    NONE(0),
    LEFT(1),
    RIGHT(2),
    TOP(4),
    BOTTOM(8);

    private final int value;

    Wall(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
