#!/usr/bin/env python3

import sys
import math

# Read a grid layout of cells with boolean flags for directions and goal presence. Each turn, read the robot's current position, orientation, and number of bombs.

rows = int(input())  # number of rows in the grid
columns = int(input())  # number of columns in the grid
for i in range(rows):
    for j in range(columns):
        west, east, north, south, has_bomb = [k != "0" for k in input().split()]

out = ["MOVE_FORWARD", "MOVE_FORWARD","MOVE_FORWARD","MOVE_FORWARD","MOVE_FORWARD","MOVE_FORWARD","TURN_RIGHT","MOVE_FORWARD","MOVE_FORWARD","MOVE_FORWARD","TURN_RIGHT","MOVE_FORWARD"]

# game loop
while True:
    # robot_x: the robot's current X coordinate
    # robot_y: the robot's current Y coordinate
    # robot_a: the robot's orientation
    # robot_bombs: the number of bombs the robot has
    robot_x, robot_y, robot_a, robot_bombs = [int(i) for i in input().split()]
    print(out[0])
    out = out[1:]