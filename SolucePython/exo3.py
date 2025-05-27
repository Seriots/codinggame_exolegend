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

def setupInDanger(maze, robot_x, robot_y):
    cell = maze[robot_y][robot_x]
    cell[5] += 1
    print(f"set initial bomb in {robot_x} {robot_y}", file=sys.stderr)
    if not cell[0]:
        maze[robot_y][robot_x - 1][5] += 1
        print(f"set bomb in {robot_x - 1} {robot_y}", file=sys.stderr)
    if not cell[1]:
        maze[robot_y][robot_x + 1][5] += 1
        print(f"set bomb in {robot_x + 1} {robot_y}", file=sys.stderr)
    if not cell[2]:
        maze[robot_y - 1][robot_x][5] += 1
        print(f"set bomb in {robot_x} {robot_y - 1}", file=sys.stderr)
    if not cell[3]:
        maze[robot_y + 1][robot_x][5] += 1
        print(f"set bomb in {robot_x} {robot_y + 1}", file=sys.stderr)

def unsetDanger(removed_bombs):
    for bomb in removed_bombs:
        print(f"Unset danger for bomb  {bomb}", file=sys.stderr)
        cell = maze[bomb[1]][bomb[0]]
        cell[5] -= 1
        if not cell[0]:
            maze[bomb[1]][bomb[0] - 1][5] -= 1
        if not cell[1]:
            maze[bomb[1]][bomb[0] + 1][5] -= 1
        if not cell[2]:
            maze[bomb[1] - 1][bomb[0]][5] -= 1
        if not cell[3]:
            maze[bomb[1] + 1][bomb[0]][5] -= 1

def nextMoveToTarget(maze, robot_x, robot_y, robot_a):
    score, direction = computeScoreForBomb(maze, robot_x, robot_y)

    print(maze[robot_y][robot_x][5], file=sys.stderr)
    if (score == 1000):
        return moveRandom(maze, robot_x, robot_y, robot_a)
    if (direction == 0 and robot_a == 0 and maze[robot_y][robot_x][5]) or (direction == 1 and robot_a == 1 and maze[robot_y][robot_x][5]) or (direction == 2 and robot_a == 2 and maze[robot_y][robot_x][5]) or (direction == 3 and robot_a == 3 and maze[robot_y][robot_x][5]):
        return "MOVE_FORWARD"
    if (direction == 0 and robot_a == 1 and maze[robot_y][robot_x][5]) or (direction == 1 and robot_a == 0 and maze[robot_y][robot_x][5]) or (direction == 2 and robot_a == 3 and maze[robot_y][robot_x][5]) or (direction == 3 and robot_a == 2 and maze[robot_y][robot_x][5]):
        return "MOVE_BACKWARD"
    if (direction == 0 and robot_a == 0 and not maze[robot_y][robot_x - 1][5]) or (direction == 1 and robot_a == 1 and not maze[robot_y][robot_x + 1][5]) or (direction == 2 and robot_a == 2 and not maze[robot_y - 1][robot_x][5]) or (direction == 3 and robot_a == 3 and not maze[robot_y + 1][robot_x][5]):
        return "MOVE_FORWARD"
    if (direction == 0 and robot_a == 1 and not maze[robot_y][robot_x - 1][5]) or (direction == 1 and robot_a == 0 and not maze[robot_y][robot_x + 1][5]) or (direction == 2 and robot_a == 3 and not maze[robot_y - 1][robot_x][5]) or (direction == 3 and robot_a == 2 and not maze[robot_y + 1][robot_x][5]):
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


def displayDanger(maze):
    for i in range(6):
        for j in range(10):
            print(maze[i][j][5], end=" ", file=sys.stderr)
        print(file=sys.stderr)

maze = []
bombs = []


rows = int(input())  # number of rows in the grid
columns = int(input())  # number of columns in the grid
for i in range(rows):
    maze.append([])
    for j in range(columns):
        west, east, north, south, has_bomb = [k != "0" for k in input().split()]
        maze[i].append([west, east, north, south, has_bomb, 0])

# game loop
while True:
    # robot_x: the robot's current X coordinate
    # robot_y: the robot's current Y coordinate
    # robot_a: the robot's orientation
    # robot_bombs: the number of bombs the robot has
    robot_x, robot_y, robot_a, robot_bombs = [int(i) for i in input().split()]
    maze[robot_y][robot_x][4] = False
    if (robot_bombs > 0):
        setupInDanger(maze, robot_x, robot_y)
        bombs.append([robot_x, robot_y, 6])
        print("DROP")
    else:
        print(nextMoveToTarget(maze, robot_x, robot_y, robot_a))

    removed_bombs = [[bomb[0], bomb[1], bomb[2] - 1] for bomb in bombs if bomb[2] <= 0]
    bombs = [[bomb[0], bomb[1], bomb[2] - 1] for bomb in bombs if bomb[2] > 0]
    unsetDanger(removed_bombs)
    displayDanger(maze)

