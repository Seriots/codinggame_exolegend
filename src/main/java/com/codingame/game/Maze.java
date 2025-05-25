package com.codingame.game;

import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public class Maze {

    private final double startWallPercent;
    private final int breakByWave;

    private final boolean randomMaze;
    private final boolean respawnBomb;

    private final GraphicEntityModule graphicEntityModule;

    public Cell[][] cells;
    private ArrayList<Cell> bombDroppedCells = new ArrayList<>();

    private int size_x;
    private int size_y;

    public Maze(GraphicEntityModule graphicEntityModule, double startWallPercent, int breakByWave, boolean randomMaze, boolean respawnBomb) {
        this.graphicEntityModule = graphicEntityModule;
        this.startWallPercent = startWallPercent;
        this.breakByWave = breakByWave > 0 ? breakByWave : 1;
        this.randomMaze = randomMaze;
        this.respawnBomb = respawnBomb;
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

    public int generateRandomMaze(int x, int y, int bombToGenerate, Coord robotPosition) {
        if (x < 1 || y < 1) {
            return -1;
        }

        this.size_x = x;
        this.size_y = y;
        this.cells = new Cell[this.size_y][this.size_x];

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

        List<List<Cell>> zonesList = this.getMazeZones();

        while (zonesList.size() != 1) {
            this.breakWallByZone(zonesList);
            zonesList = this.getMazeZones();
        }
        this.generateBombs(bombToGenerate, robotPosition);
        return 0;
    }

    public int generateBasicMaze() {
        this.size_x = 10;
        this.size_y = 6;
        this.cells = new Cell[this.size_y][this.size_x];

        String[][] maze = new String[6][10];
        maze[0] = "5 4 4 4 4 4 4 4 4 6 ".split(" ");
        maze[1] = "1 0 0 0 0 0 0 0 0 2".split(" ");
        maze[2] = "1 0 0 0 10 9 0 0 0 2".split(" ");
        maze[3] = "1 0 0 0 6 5 0 0 0 2".split(" ");
        maze[4] = "1 0 0 0 0 0 0 0 0 2".split(" ");
        maze[5] = "9 8 8 8 8 8 8 8 8 10 ".split(" ");

        for (int row = 0; row < this.size_y; row++) {
            for (int column = 0; column < this.size_x; column++) {
                int value = Integer.parseInt(maze[row][column]);
                this.cells[row][column] = new Cell(this.graphicEntityModule,
                        (value & 1) > 0,
                        (value & 2) > 0,
                        (value & 4) > 0,
                        (value & 8) > 0,
                        column,
                        row
                );
            }
        }
        this.cells[3][5].generateBomb();
        return 0;
    }

    public int generate(int x, int y, int bombToGenerate, Coord robotPosition) {
        if (this.randomMaze)
            return this.generateRandomMaze(x, y, bombToGenerate, robotPosition);
        else
            return this.generateBasicMaze();
    }

    public void generateBombs(int nbNewBombs, Coord playerPosition) {
        Random random = new Random();

        int count = 0;
        int retry = 0;

        while (count < nbNewBombs) {
            int random_value = random.nextInt(this.size_x * this.size_y);
            int random_x = random_value % this.size_x;
            int random_y = random_value / this.size_x;

            if (random_x == playerPosition.x && random_y == playerPosition.y) {
                continue ;
            }

            if (this.cells[random_y][random_x].generateBomb()) {
                count++;
                retry = 0;
            }
            else {
                retry++;
                if (retry >= 50)
                    break;
            }
        }
    }

    public boolean canDropBomb(Coord position) {
        return !cells[position.y][position.x].droppedBomb;
    }

    public boolean dropBomb(Coord position) {
        if (this.canDropBomb(position)) {
            cells[position.y][position.x].dropBomb();
            this.bombDroppedCells.add(cells[position.y][position.x]);
            return true;
        }
        return false;
    }

    public boolean updateAllBombs(Robot robot) {
        boolean[] exploded = {false};
        this.bombDroppedCells.removeIf(cell -> {
            if (cell.updateBomb(this, robot)) {
                if ((robot.position.x == cell.x && robot.position.y >= cell.y - 1 && robot.position.y <= cell.y + 1) || (
                        robot.position.y == cell.y && robot.position.x >= cell.x - 1 && robot.position.x <= cell.x + 1)) {
                    exploded[0] = true;
                    robot.explode();
                }
                cell.colorBackground(robot);
                if (cell.x > 0 && !cell.leftWall)
                    cells[cell.y][cell.x - 1].colorBackground(robot);
                if (cell.x < Constants.COLUMNS - 1 && !cell.rightWall)
                    cells[cell.y][cell.x + 1].colorBackground(robot);
                if (cell.y > 0 && !cell.topWall)
                    cells[cell.y - 1][cell.x].colorBackground(robot);
                if (cell.y < Constants.ROWS - 1 && !cell.bottomWall)
                    cells[cell.y + 1][cell.x].colorBackground(robot);
                return true;
            }
            return false;
        });
        return exploded[0];
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

    public void sendMazeData(SoloGameManager<Player> gameManager) {
        for (int row = 0; row < this.size_y; row++) {
            for (int column = 0; column < this.size_x; column++) {
                gameManager.getPlayer().sendInputLine(this.cells[row][column].toString());
            }
        }
    }
}
