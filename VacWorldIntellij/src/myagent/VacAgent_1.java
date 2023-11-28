/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.HashMap;
import java.util.Map;

/* Change the code as appropriate.  This code
   is here to help you understand the mechanism
   of the simulator. 
*/

public class VacAgent_1 extends Agent {

    private final String ID = "1";
    // Think about locations you already visited.  Remember those
    private boolean dirtStatus = true;
    private boolean obstacleInFront = true;
    private int moves = 0;
   // Use an integer map to store exploration values
   private Map<String, Integer> memory = new HashMap<>();
    private int currentX = 0;
    private int currentY = 0;
    private String currentDirection = "NORTH";

    public void see(Percept p) {
        VacPercept vp = (VacPercept) p;

        dirtStatus = vp.seeDirt();
        obstacleInFront = vp.seeObstacle();

        String currentPosition = getCurrentPosition();
        if (!memory.containsKey(currentPosition)) {
            memory.put(currentPosition, 0); // Set initial exploration value to 0
        }

        // Increment the exploration value when the agent moves
        int explorationValue = memory.get(currentPosition);
        memory.put(currentPosition, explorationValue + 1);

        if (obstacleInFront) {
            // Increment the exploration value for the next position
            String nextPosition = getNextPosition();
            memory.put(nextPosition, memory.getOrDefault(nextPosition, 0) + 1);
        }
    }
    public Action selectAction() {
        Action action;

        // Logic rules for making decisions
        if (dirtStatus) {
            action = new SuckDirt();
        } else if (obstacleInFront) {
            // Handle obstacle by turning right
            if (shouldGoRight()) {
                action = new TurnRight();
                updateDirection("RIGHT");
            } else {
                action = new TurnLeft();
                updateDirection("LEFT");
            }

        } else {
            // Move forward to the next unexplored or cleaned position
            action = new GoForward();
            moves++;
            updatePosition();
        }

        // If there are no possible moves, shut off
        if (moves >= 40 || !canMove()) {
            action = new ShutOff();
        }
        System.out.println("Memory content: " + memory);
        printMemoryMap();
        return action;
    }

    public String getId() {
        return ID;
    }

    private void updateDirection(String way) {
        if(way == "RIGHT") {
            switch (currentDirection) {
                case "NORTH":
                    currentDirection = "EAST";
                    break;
                case "EAST":
                    currentDirection = "SOUTH";
                    break;
                case "SOUTH":
                    currentDirection = "WEST";
                    break;
                case "WEST":
                    currentDirection = "NORTH";
                    break;
            }
        }else{
            switch (currentDirection) {
                case "NORTH":
                    currentDirection = "WEST";
                    break;
                case "EAST":
                    currentDirection = "NORTH";
                    break;
                case "SOUTH":
                    currentDirection = "EAST";
                    break;
                case "WEST":
                    currentDirection = "SOUTH";
                    break;
            }
        }
    }

    private void updatePosition() {
        switch (currentDirection) {
            case "NORTH":
                currentY++;
                break;
            case "SOUTH":
                currentY--;
                break;
            case "EAST":
                currentX++;
                break;
            case "WEST":
                currentX--;
                break;
        }
    }
    private String getCurrentPosition() { //it is a relative position
        return currentX + "," + currentY;
    }
    private String getNextPosition() { //it is a relative position
         switch (currentDirection){
             case "NORTH":
                return currentX + "," + (currentY+1);
             case "SOUTH":
                return currentX + "," + (currentY-1);
             case "EAST":
                return (currentX+1) + "," + currentY;
             case "WEST":
                return (currentX-1) + "," + currentY;
              default:
                return null;
         }
    }

    private boolean shouldGoRight() {
        // Implement logic to decide whether to go right or left based on the current memory
        // You can use information from the memory map to make this decision
        // For example, you can compare the exploration values of the right and left positions

        String currentPosition = getCurrentPosition();
        String rightPosition = getNextPosition(true);
        String leftPosition = getNextPosition(false);

        int explorationValueRight = memory.getOrDefault(rightPosition, 0);
        int explorationValueLeft = memory.getOrDefault(leftPosition, 0);

        // Check if turning right would lead to an already visited position
        if (explorationValueRight > 0) {
            // Check if turning right would lead to a less explored area than turning left
            return explorationValueRight < explorationValueLeft;
        } else {
            // Turn right if the right position is unexplored
            return true;
        }
    }
    private String getNextPosition(boolean goRight) {
        switch (currentDirection) {
            case "NORTH":
                return currentX + "," + (goRight ? (currentY + 1) : (currentY - 1));
            case "SOUTH":
                return currentX + "," + (goRight ? (currentY - 1) : (currentY + 1));
            case "EAST":
                return (goRight ? (currentX + 1) : (currentX - 1)) + "," + currentY;
            case "WEST":
                return (goRight ? (currentX - 1) : (currentX + 1)) + "," + currentY;
            default:
                return null;
        }
    }

    private boolean canMove() {
        // Implement logic to check if there are possible moves
        // For example, check if there are unexplored or cleaned positions nearby
        // You can use the memory to make this decision
        // This is a placeholder, you need to implement the actual logic
        return true;
    }

    private void printMemoryMap() {
        // Find the dimensions of the map
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
    
        for (String position : memory.keySet()) {
            String[] coordinates = position.split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
    
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }
    
        // Print the map with exploration values
        for (int y = maxY; y >= minY; y--) {
            System.out.print("|");
            for (int x = minX; x <= maxX; x++) {
                String position = x + "," + y;
                int explorationValue = memory.getOrDefault(position, 0);
                System.out.printf("%-4s|", explorationValue);
            }
            System.out.println();
            System.out.print("+");
            for (int x = minX; x <= maxX; x++) {
                System.out.print("----+");
            }
            System.out.println();
        }
    
        // Print the column indices
        System.out.print(" ");
        for (int x = minX; x <= maxX; x++) {
            System.out.printf("%-4s", x);
        }
        System.out.println();
    }
    
}
