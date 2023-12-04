package myagent;

import agent.Action;
import agent.Agent;
import agent.Percept;
import vacworld.ShutOff;
import vacworld.SuckDirt;
import vacworld.TurnLeft;
import vacworld.TurnRight;
import vacworld.VacPercept;
import vacworld.GoForward;
import vacworld.Direction;

import java.util.Random;

public class VacAgent extends Agent {

    private boolean seeDirt = false;
    private boolean seeObst = false;
    private boolean shutDown = false;

    private int[][] map = new int[12][12]; // internal map
    private static final int VISITED = 1;
    private static final int OBSTACLE = 99;
    private static final int DIRT = 11;
    private int X = 5;
    private int Y = 5;
    private int direct = Direction.NORTH;
    private static final int CELL_LIMIT = 3; // limits how many times a vacuum can visit a cell

    // constants for the vacuum to "think" what's around it
    private int up;
    private int right;
    private int down;
    private int left;

    public void see(Percept p) {
        VacPercept agent = (VacPercept) p;

        up = map[X - 1][Y];
        right = map[X][Y + 1];
        down = map[X + 1][Y];
        left = map[X][Y - 1];

        if (agent.seeObstacle()) {
            seeObst = true;
            handleObstacle();
        } else if (agent.seeDirt()) {
            seeDirt = true;
            handleDirt();
        } else {
            handleEmptyCell();
        }

        printMap();
    }

    public Action selectAction() {
        if (seeObst) {
            seeObst = false;
            return seeObject(map);
        } else if (seeDirt) {
            seeDirt = false;
            return new SuckDirt();
        } else if (shutDown) {
            return new ShutOff();
        } else {
            Action explore = checkSurround(map);

            if (explore.toString().equals("GO FORWARD")) {
                moveForward();
            }

            return explore;
        }
    }

    private void handleObstacle() {
        switch (direct) {
            case Direction.NORTH:
                map[X - 1][Y] = OBSTACLE;
                break;
            case Direction.EAST:
                map[X][Y + 1] = OBSTACLE;
                break;
            case Direction.SOUTH:
                map[X + 1][Y] = OBSTACLE;
                break;
            case Direction.WEST:
                map[X][Y - 1] = OBSTACLE;
        }
    }

    private void handleDirt() {
        map[X][Y] = 0;
    }

    private void handleEmptyCell() {
        map[X][Y] += VISITED;
        if (map[X][Y] == CELL_LIMIT) {
            shutDown = true;
        }
    }

    private void printMap() {
        System.out.println("Vacuum Cleaner Internal Map:");

        // Print column
        System.out.print("   ");
        for (int i = 0; i < map[0].length; i++) {
        }
        System.out.println();

        // Print rows
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.printf("%-4d", map[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    private void moveForward() {
        switch (direct) {
            case Direction.NORTH:
                X -= 1;
                break;
            case Direction.EAST:
                Y += 1;
                break;
            case Direction.SOUTH:
                X += 1;
                break;
            case Direction.WEST:
                Y -= 1;
                break;
        }
    }

    public String getId() {
        return "1";
    }

    public Action checkSurround(int[][] map) {
        Action decision = new GoForward();

        Random rand = new Random();
        int prob = rand.nextInt(2);

        switch (direct) {
            case Direction.NORTH:
                if (left == right && left < up) {
                    if (prob == 1) {
                        decision = new TurnRight();
                        direct = Direction.EAST;
                    } else {
                        decision = new TurnLeft();
                        direct = Direction.WEST;
                    }
                } else if (left > right) {
                    if (right < up) {
                        decision = new TurnRight();
                        direct = Direction.EAST;
                    }
                } else if (left < right) {
                    if (left < up) {
                        decision = new TurnLeft();
                        direct = Direction.WEST;
                    }
                }
                break;
            case Direction.EAST:
                if (up == down && up < right) {
                    if (prob == 1) {
                        decision = new TurnRight();
                        direct = Direction.SOUTH;
                    } else {
                        decision = new TurnLeft();
                        direct = Direction.NORTH;
                    }
                } else if (up > down) {
                    if (down < right) {
                        decision = new TurnRight();
                        direct = Direction.SOUTH;
                    }
                } else if (down > up) {
                    if (up < right) {
                        decision = new TurnLeft();
                        direct = Direction.NORTH;
                    }
                }
                break;
            case Direction.SOUTH:
                if (left == right && left < down) {
                    if (prob == 1) {
                        decision = new TurnRight();
                        direct = Direction.WEST;
                    } else {
                        decision = new TurnLeft();
                        direct = Direction.EAST;
                    }
                } else if (left > right) {
                    if (right < down) {
                        decision = new TurnLeft();
                        direct = Direction.EAST;
                    }
                } else if (left < right) {
                    if (left < down) {
                        decision = new TurnRight();
                        direct = Direction.WEST;
                    }
                }
                break;
            default:
                if (up == down && up < left) {
                    if (prob == 1) {
                        decision = new TurnRight();
                        direct = Direction.SOUTH;
                    } else {
                        decision = new TurnLeft();
                        direct = Direction.NORTH;
                    }
                } else if (down > up) {
                    if (up < left) {
                        decision = new TurnRight();
                        direct = Direction.NORTH;
                    }
                } else if (up > down) {
                    if (down < left) {
                        decision = new TurnLeft();
                        direct = Direction.SOUTH;
                    }
                }
        }
        return decision;
    }

    public Action seeObject(int[][] map) {
        Action decision;
        int up = map[X - 1][Y];
        int right = map[X][Y + 1];
        int down = map[X + 1][Y];
        int left = map[X][Y - 1];

        Random rand = new Random();
        int picker = rand.nextInt(2);

        switch (direct) {
            case Direction.NORTH:
                if (right > left) {
                    decision = new TurnLeft();
                    direct = Direction.WEST;
                } else if (left > right) {
                    decision = new TurnRight();
                    direct = Direction.EAST;
                } else {
                    decision = (picker == 1) ? new TurnLeft() : new TurnRight();
                    direct = (picker == 1) ? Direction.WEST : Direction.EAST;
                }
                break;
            case Direction.EAST:
                if (up > down) {
                    decision = new TurnRight();
                    direct = Direction.SOUTH;
                } else if (down > up) {
                    decision = new TurnLeft();
                    direct = Direction.NORTH;
                } else {
                    decision = (picker == 1) ? new TurnRight() : new TurnLeft();
                    direct = (picker == 1) ? Direction.SOUTH : Direction.NORTH;
                }
                break;
            case Direction.SOUTH:
                if (right > left) {
                    decision = new TurnLeft();
                    direct = Direction.EAST;
                } else if (left > right) {
                    decision = new TurnRight();
                    direct = Direction.WEST;
                } else {
                    decision = (picker == 1) ? new TurnLeft() : new TurnRight();
                    direct = (picker == 1) ? Direction.EAST : Direction.WEST;
                }
                break;
            case Direction.WEST:
                if (up > down) {
                    decision = new TurnLeft();
                    direct = Direction.SOUTH;
                } else if (down > up) {
                    decision = new TurnRight();
                    direct = Direction.NORTH;
                } else {
                    decision = (picker == 1) ? new TurnLeft() : new TurnRight();
                    direct = (picker == 1) ? Direction.SOUTH : Direction.NORTH;
                }
                break;
            default:
                decision = new TurnLeft();
        }

        return decision;
    }
}
