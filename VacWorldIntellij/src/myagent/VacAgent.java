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

public class VacAgent extends Agent {

    private final String ID = "1";
    // Think about locations you already visited.  Remember those
    private boolean dirtStatus = true;
    private boolean obstacleInFront = true;

    private Map<String, String> memory = new HashMap<>();
    private int currentX = 0;
    private int currentY = 0;
    private String currentDirection = "NORTH";

    public void see(Percept p) {
        VacPercept vp = (VacPercept) p;

        dirtStatus = vp.seeDirt();
        obstacleInFront = vp.seeObstacle();

        if (!memory.containsKey(getCurrentPosition())) {
            memory.put(getCurrentPosition(), "unexplored");
        }
        if (obstacleInFront) {
            memory.put(getNextPosition(), "obstacle"); //next forward block has an obstacle
        }
    }

    public Action selectAction() {
        Action action;

        // Logic rules for making decisions
        if (dirtStatus) {
            action = new SuckDirt();
            memory.put(getCurrentPosition(), "cleaned");
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
            updatePosition();
        }

        // If there are no possible moves, shut off
        if (!canMove()) {
            action = new ShutOff();
        }

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
        // This is a placeholder; you need to implement the actual logic
        // For example, you can check if the left side is less explored than the right side
        return true; // Placeholder, replace with your actual logic
    }

    private boolean canMove() {
        // Implement logic to check if there are possible moves
        // For example, check if there are unexplored or cleaned positions nearby
        // You can use the memory to make this decision
        // This is a placeholder, you need to implement the actual logic
        return true;
    }
}
