package com.codingame.game;

import java.util.Arrays;
import java.util.List;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.SoloGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    @Inject private SoloGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;

    private Robot robotPlayer;

    private Maze maze;

    private boolean crashed = false;

    @Override
    public void init() {
        gameManager.setFrameDuration(500);
        
        graphicEntityModule.createSprite().setImage(Constants.BACKGROUND_SPRITE);

        robotPlayer = new Robot(graphicEntityModule, new Coord(0, 0), 0);

        maze = new Maze(graphicEntityModule, 0.0,4);
        int result = maze.generate(Constants.COLUMNS, Constants.ROWS);
        maze.showMaze();

//        for (Cell[] row : cells) {
//            String line = "";
//            for (Cell cell : row) {
//                line = line.concat(cell.toString());
//            }
//            gameManager.getPlayer().sendInputLine(line);
//        }
    }

    @Override
    public void gameTurn(int turn) {
        gameManager.getPlayer().sendInputLine("yolo");

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
            }

        } catch (TimeoutException e) {
            gameManager.loseGame("Timeout!");
        }

        // Check win condition
//        if (fishPosition.equals(eggPosition)) {
//            gameManager.winGame("Congrats!");
//            eggSprite.setScale(0);
//        }
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
