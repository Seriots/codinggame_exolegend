import com.codingame.gameengine.runner.SoloGameRunner;

public class Main {
    public static void main(String[] args) {
        SoloGameRunner gameRunner = new SoloGameRunner();

        // Sets the player
//        gameRunner.setAgent(Solution.class);
        gameRunner.setAgent("/home/lgiband/work/exolegend/codinggame/codinggame_exolegend/SolucePython/exo3.py");

        // Sets a test case
        gameRunner.setTestCase("test7.json");

        gameRunner.start();
    }
}
