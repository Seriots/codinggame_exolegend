package com.codingame.game;

import com.codingame.gameengine.module.entities.GraphicEntityModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public class Maze {

    private final double startWallPercent;
    private final int breakByWave;

    private final GraphicEntityModule graphicEntityModule;

    public Cell[][] cells;
    private int size_x;
    private int size_y;

    public Maze(GraphicEntityModule graphicEntityModule, double startWallPercent, int breakByWave) {
        this.graphicEntityModule = graphicEntityModule;
        this.startWallPercent = startWallPercent;
        this.breakByWave = breakByWave > 0 ? breakByWave : 1;
    }

    private void markZoneRec(int[][] markedCell, int zoneValue, int row, int column) {
        if (row < 0 || row >= this.size_y || column < 0 || column >= this.size_x) {
            return;
        }
        if (markedCell[row][column] != 0) {
            return;
        }
        markedCell[row][column] = zoneValue;

        if (!this.cells[row][column].leftWall) {
            this.markZoneRec(markedCell, zoneValue, row, column - 1);
        }
        if (!this.cells[row][column].rightWall) {
            this.markZoneRec(markedCell, zoneValue, row, column + 1);
        }
        if (!this.cells[row][column].topWall) {
            this.markZoneRec(markedCell, zoneValue, row - 1, column);
        }
        if (!this.cells[row][column].bottomWall) {
            this.markZoneRec(markedCell, zoneValue, row + 1, column);
        }
    }

    private List<List<Cell>> getMazeZones() {
        List<List<Cell>> zonesList;
        int[][] markedCell = new int[this.size_y][this.size_x];

        int zoneCount = 1;
        for (int row = 0; row < this.size_y; row++) {
            for (int column = 0; column < this.size_x; column++) {
                if (markedCell[row][column] == 0) {
                    this.markZoneRec(markedCell, zoneCount, row, column);
                    zoneCount++;
                }
            }
        }
        zoneCount--;
        zonesList = new ArrayList<>();
        for (int i = 0; i < zoneCount; i++) {
            zonesList.add(new ArrayList<>());
        }

        for (int row = 0; row < this.size_y; row++) {
            for (int column = 0; column < this.size_x; column++) {
                zonesList.get(markedCell[row][column] - 1).add(this.cells[row][column]);
            }
        }
        return zonesList;
    }

    private void breakWallByZone(List<List<Cell>> zonesList) {
        Random random = new Random();
        List<Integer> usedZones = IntStream.range(0, zonesList.size())
                .boxed()
                .collect(Collectors.toList());

        for (int i = 0; i < this.breakByWave; i++) {
            if (usedZones.isEmpty()) {
                return;
            }
            int randomIndex = random.nextInt(usedZones.size());
            int randomZone = usedZones.remove(randomIndex);

            List<Cell> cellsInZone = zonesList.get(randomZone);
            Cell randomCell = cellsInZone.get(random.nextInt(cellsInZone.size()));

            Wall wallBreaked = randomCell.breakOneWall(this.size_x, this.size_y, random);
            while (wallBreaked == Wall.NONE) {
                randomCell = cellsInZone.get(random.nextInt(cellsInZone.size()));
                wallBreaked = randomCell.breakOneWall(this.size_x, this.size_y, random);
            }

            if (wallBreaked == Wall.LEFT) {
                this.cells[randomCell.y][randomCell.x - 1].reportBrokenWall(wallBreaked);
            } else if (wallBreaked == Wall.RIGHT) {
                this.cells[randomCell.y][randomCell.x + 1].reportBrokenWall(wallBreaked);
            } else if (wallBreaked == Wall.TOP) {
                this.cells[randomCell.y - 1][randomCell.x].reportBrokenWall(wallBreaked);
            } else if (wallBreaked == Wall.BOTTOM) {
                this.cells[randomCell.y + 1][randomCell.x].reportBrokenWall(wallBreaked);
            }
        }
    }

    public int generate(int x, int y) {
        if (x < 1 || y < 1) {
            return -1;
        }

        this.size_x = x;
        this.size_y = y;
        this.cells = new Cell[this.size_y][this.size_x];

        // First generation with all cells with all walls modulated by startWallPercent
        for (int row = 0; row < y; row++) {
            for (int column = 0; column < x; column++) {
                this.cells[row][column] = new Cell(this.graphicEntityModule,
                        column == 0 || this.cells[row][column - 1].rightWall,
                        this.startWallPercent > Math.random() || column == x - 1,
                        row == 0 || this.cells[row - 1][column].bottomWall,
                        this.startWallPercent > Math.random() || row == y - 1,
                        column,
                        row
                );
            }
        }

        // Get all different zones, if zone != 1, break a wall
        List<List<Cell>> zonesList = this.getMazeZones();

        while (zonesList.size() != 1) {
            this.breakWallByZone(zonesList);
            zonesList = this.getMazeZones();
        }
        generateBombs(Constants.START_BOMB_COUNT, Constants.ROBOT_START_POSITION);
        return 0;
    }

    public void generateBombs(int nbNewBombs, Coord playerPosition) {
        Random random = new Random();

        int count = 0;

        while (count < nbNewBombs) {
            int random_value = random.nextInt(this.size_x * this.size_y);
            int random_x = random_value % this.size_x;
            int random_y = random_value / this.size_x;

            if (random_x == playerPosition.x && random_y == playerPosition.y) {
                continue ;
            }

            this.cells[random_y][random_x].generateBomb();
            count ++;
        }
    }

    public boolean playerTryToGetBomb(Coord playerPosition) {
        return this.cells[playerPosition.y][playerPosition.x].tryGetBomb();
    }

    public void showMaze() {
        for (int row = 0; row < this.size_y; row++) {
            for (int column = 0; column < this.size_x; column++) {
                this.cells[row][column].ShowWalls();
            }
        }
    }
}
