
import com.sun.org.apache.xerces.internal.xs.StringList;

import java.util.*;


public class Solution {

    private boolean target_selected = false;
    private int target_x;
    private int target_y;


    void findNearestBomb(boolean[][][] cases, int robot_x, int robot_y) {
        this.target_selected = true;
        this.target_x = 1;
        this.target_y = 0;
    }

    String getNextMoveToNearestBomb(boolean[][][] cases, int robot_x, int robot_y, double robot_a) {
        return "MOVE_FORWARD";
    }

    String randomMoveDodgeWall(boolean[][][] cases, int robot_x, int robot_y, int robot_a, int robot_bombs) {
        boolean[] cell = cases[robot_y][robot_x];

        if (robot_bombs > 0) {
            return "DROP";
        }

        if ((robot_a == 0 && !cell[0]) || (robot_a == 1 && !cell[1]) || (robot_a == 2 && !cell[2]) || (robot_a == 3 && !cell[3])) {
            int random = (int)(Math.random() * 8); // Generates 0, 1, or 2
            if (random <= 5)
                return "MOVE_FORWARD";
            else if (random == 6)
                return "TURN_RIGHT";
            else
                return "TURN_LEFT";
        }
        else {
            int random = (int)(Math.random() * 2);
            if (random == 0)
                return "TURN_RIGHT";
            else
                return "TURN_LEFT";
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        Scanner scanner = new Scanner(System.in);

        boolean[][][] cases = new boolean[6][10][5];
        int robot_x, robot_y, robot_a, robot_bombs;

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 10; j++) {
                String[] line = scanner.nextLine().split(" ");
                for (int k = 0; k < 5; k++) {
                    cases[i][j][k] = Boolean.parseBoolean(line[k]);
                }
            }
        }

//        ArrayList<String> moves = new ArrayList<>();
//        moves.add("MOVE_FORWARD");
//        moves.add("MOVE_FORWARD");
//        moves.add("MOVE_FORWARD");
//        moves.add("MOVE_FORWARD");
//        moves.add("MOVE_FORWARD");
//        moves.add("MOVE_FORWARD");
//        moves.add("TURN_RIGHT");
//        moves.add("MOVE_FORWARD");
//        moves.add("MOVE_FORWARD");
//        moves.add("MOVE_FORWARD");
//        moves.add("TURN_RIGHT");
//        moves.add("MOVE_FORWARD");



        while (true) {
            String[] robot_data = scanner.nextLine().split(" ");
            robot_x = Integer.parseInt(robot_data[0]);
            robot_y = Integer.parseInt(robot_data[1]);
            robot_a = Integer.parseInt(robot_data[2]);
            robot_bombs = Integer.parseInt(robot_data[3]);

            System.err.println(robot_bombs);
            if (!solution.target_selected)
                solution.findNearestBomb(cases, robot_x, robot_y);

            System.out.println(solution.randomMoveDodgeWall(cases, robot_x, robot_y, robot_a, robot_bombs));
//            System.out.println(moves.remove(0));
        }
    }
}
