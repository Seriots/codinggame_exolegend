package com.codingame.game;

import java.util.Arrays;
import java.util.List;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    @Inject private SoloGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;

    private Robot robotPlayer;

    private Maze maze;

    private boolean crashed = false;

    private int targetPts;

    private Text pointsDisplay;

    @Override
    public void init() {
        gameManager.setFrameDuration(500);

        gameManager.setMaxTurns(1000);
        
        graphicEntityModule.createSprite().setImage(Constants.BACKGROUND_SPRITE);

        pointsDisplay = graphicEntityModule.createText("POINTS: 0")
                .setX(25)  // Position from left
                .setY(0)  // Position from top
                .setZIndex(20)  // Make sure it's above other elements
                .setFontSize(60)  // Larger text size
                .setFontFamily("Arial")  // Clean, readable font
                .setFillColor(0xDDDDDD)  // White text
                .setStrokeColor(0x111111)  // Black outline
                .setStrokeThickness(4)  // Thickness of the outline
                .setAnchor(0.0);

        String[] testInput = gameManager.getTestCaseInput().get(0).split(" ");
        int robot_start_x = Integer.parseInt(testInput[0]);
        int robot_start_y = Integer.parseInt(testInput[1]);
        boolean random_maze = Boolean.parseBoolean(testInput[2]);
        double start_wall_percent = Double.parseDouble(testInput[3]);
        int start_bomb_number = Integer.parseInt(testInput[4]);
        boolean respawn_bomb = Boolean.parseBoolean(testInput[5]);
        this.targetPts = Integer.parseInt(testInput[6]);

        robotPlayer = new Robot(graphicEntityModule, new Coord(robot_start_x, robot_start_y), 0);

        this.showPoints(robotPlayer);
        maze = new Maze(graphicEntityModule, start_wall_percent,3, random_maze, respawn_bomb);
        int result = maze.generate(Constants.COLUMNS, Constants.ROWS, start_bomb_number, new Coord(robot_start_x, robot_start_y));
        maze.showMaze();

        gameManager.getPlayer().sendInputLine(String.valueOf(Constants.ROWS));
        gameManager.getPlayer().sendInputLine(String.valueOf(Constants.COLUMNS));
        
        maze.sendMazeData(gameManager);
    }

    @Override
    public void gameTurn(int turn) {
        gameManager.getPlayer().sendInputLine(robotPlayer.toString());

        gameManager.getPlayer().execute();
        if (crashed) {
            gameManager.loseGame("Your robot crashed! Try again.");
            return ;
        }

        try {
            List<String> outputs = gameManager.getPlayer().getOutputs();

            String output = checkOutput(outputs);

            if (output != null) {
                Action action = Action.valueOf(output.toUpperCase());
                if (!robotPlayer.move(action, maze)) {
                    crashed = true;
                    return;
                }
                robotPlayer.tryGetbomb(maze);

                if (maze.updateAllBombs(robotPlayer)) {
                    gameManager.loseGame("The robot exploded !");
                }
                this.showPoints(robotPlayer);
            }

        } catch (TimeoutException e) {
            gameManager.loseGame("Timeout!");
        }

        if (robotPlayer.points >= this.targetPts) {
            gameManager.winGame("Congrats!");
        }
    }

    private void showPoints(Robot robot) {
        pointsDisplay.setText("POINTS: " + robot.points);
    }

    private String checkOutput(List<String> outputs) {
        if (outputs.size() != 1) {
            gameManager.loseGame("You did not send 1 output in your turn.");
        } else {
            String output = outputs.get(0);
            if (!Arrays.asList(Constants.ACTIONS).contains(output)) {
                gameManager
                    .loseGame(
                        String.format(
                            "Expected output: %s but received %s",
                                String.join(" | ", Constants.ACTIONS),
                            output
                        )
                    );
            } else {
                return output;
            }
        }
        return null;
    }

    private void checkInvalidAction(Action action) {
//        if ((fishPosition.y == 0 && action == Action.UP)
//            || (fishPosition.y == Constants.ROWS - 1 && action == Action.DOWN)
//            || fishPosition.add(Coord.RIGHT).x > Constants.COLUMNS - 1) {
//            gameManager.loseGame("Your fish swum out of the game zone.");
//        }
    }
}
