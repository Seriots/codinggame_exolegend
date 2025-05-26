#!/usr/bin/env python3

import sys
import math
import random

def moveRandom(maze, robot_x, robot_y, robot_a):
    cell = maze[robot_y][robot_x]

    if (robot_a == 0 and not cell[0]) or (robot_a == 1 and not cell[1]) or (robot_a == 2 and not cell[2]) or (robot_a == 3 and not cell[3]):
        return "MOVE_FORWARD"
    else:
        if (random.random() > 0.5):
            return "TURN_LEFT"
        else:
            return "TURN_RIGHT"

def nextMoveToTarget(maze, robot_x, robot_y, robot_a):
    score, direction = computeScoreForBomb(maze, robot_x, robot_y)

    print(score, file=sys.stderr)
    if (score == 1000):
        return moveRandom(maze, robot_x, robot_y, robot_a)
    if (direction == 0 and robot_a == 0) or (direction == 1 and robot_a == 1) or (direction == 2 and robot_a == 2) or (direction == 3 and robot_a == 3):
        return "MOVE_FORWARD"
    if (direction == 0 and robot_a == 1) or (direction == 1 and robot_a == 0) or (direction == 2 and robot_a == 3) or (direction == 3 and robot_a == 2):
        return "MOVE_BACKWARD"
    if (direction == 0 and robot_a == 2) or (direction == 1 and robot_a == 3) or (direction == 2 and robot_a == 1) or (direction == 3 and robot_a == 0):
        return "TURN_RIGHT"
    else:
        return "TURN_LEFT"

def computeScoreForBomb(maze, robot_x, robot_y, depth=10):
    score = [1000, 1000, 1000, 1000]
    cell = maze[robot_y][robot_x]
    if (cell[4]):
        return 10 - depth, 0
    if (depth == 0):
        return 1000, 0
    if not cell[0]:
        score[0] = computeScoreForBomb(maze, robot_x - 1, robot_y, depth - 1)[0]
    if not cell[1]:
        score[1] = computeScoreForBomb(maze, robot_x + 1, robot_y, depth - 1)[0]
    if not cell[2]:
        score[2] = computeScoreForBomb(maze, robot_x, robot_y - 1, depth - 1)[0]
    if not cell[3]:
        score[3] = computeScoreForBomb(maze, robot_x, robot_y + 1, depth - 1)[0]

    return min(score), score.index(min(score))


maze = []

rows = int(input())  # number of rows in the grid
columns = int(input())  # number of columns in the grid
for i in range(rows):
    maze.append([])
    for j in range(columns):
        west, east, north, south, has_bomb = [k != "0" for k in input().split()]
        maze[i].append([west, east, north, south, has_bomb])

# game loop
while True:
    # robot_x: the robot's current X coordinate
    # robot_y: the robot's current Y coordinate
    # robot_a: the robot's orientation
    # robot_bombs: the number of bombs the robot has
    robot_x, robot_y, robot_a, robot_bombs = [int(i) for i in input().split()]
    maze[robot_y][robot_x][4] = False
    if (robot_bombs > 0):
        print("DROP")
    else:
        print(nextMoveToTarget(maze, robot_x, robot_y, robot_a))
