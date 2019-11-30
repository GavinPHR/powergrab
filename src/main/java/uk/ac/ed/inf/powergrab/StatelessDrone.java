package uk.ac.ed.inf.powergrab;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
 * The statelessDrone class contains methods that:
 *   - move the drone
 *   - charge the drone
 *   - select the next move using a state-less strategy
 *   
 * Terminologies:
 *   - Positive stations are the ones with coins >= 0
 *   - Negative stations are the ones with coins < 0
 *   - Negative moves are the ones where there's a negative stations in range 
 *   - Positive moves are the ones that aren't negative
*/
public class StatelessDrone {
    // The drone's status, not used for move strategy
    Position currentPosition;
    float coins = 0, power = 250;
    // The map of this game
    final GameMap map;
    // moveCount, used for
    //   - stopping the game when it reaches 250
    //   - logging the flight path
    int moveCount = 0;
    // Random number generator for state-less move strategy
    public Random rand;

    // Constructor given initial position, the game's map, and a random seed
    public StatelessDrone(Position pos, GameMap map, long seed) {
        this.currentPosition = pos;
        this.map = map;
        map.longitudeHistory[0] = pos.longitude;
        map.latitudeHistory[0] = pos.latitude;
        rand = new Random(seed);
    }
    
    // Select the next move (a direction) using a state-less strategy
    public Direction selectMove() {
        Map<Integer, Direction> positive, negative;
        // Add the directions that contains negative stations in a negative set
        // And every other direction in a positive set
        positive = new HashMap<Integer,Direction>(16);
        negative = new HashMap<Integer,Direction>(16);
        for (Direction d : Direction.directions) {
            int result = checkMove(d);
            if (result == 1) {
                positive.put(positive.size(), d);
            } else if (result == 0) {
                negative.put(negative.size(), d);
            } else {
                continue;
            }
        }
        // If there is no positive move, choose a RANDOM negative move
        if (positive.isEmpty()) {
            return negative.get(rand.nextInt(negative.size()));
        } else {  // Otherwise choose a RANDOM positive move
            return positive.get(rand.nextInt(positive.size()));
        }
     }
    
    public boolean move() {
        if (moveCount >= 250 || power < 1.25) return false;
        // An array to store log information
        String[] logEntry = new String[7]; 
        Direction d = selectMove();
        logEntry[0] = Double.toString(currentPosition.latitude);
        logEntry[1] = Double.toString(currentPosition.longitude);
        logEntry[2] = d.toString();
        currentPosition = currentPosition.nextPosition(d);
        logEntry[3] = Double.toString(currentPosition.latitude);
        logEntry[4] = Double.toString(currentPosition.longitude);
        power -= 1.25;
        charge();
        logEntry[5] = Float.toString(coins);
        logEntry[6] = Float.toString(power);
        // Log the flight path/status in the GameMap object
        map.log(moveCount, String.join(",", logEntry), currentPosition.latitude, currentPosition.longitude);
        moveCount++;
        return true; 
    }
    
    // If the drone is in range of a negative stations (coins < 0)
    // after a move (given a direction to move in), return 0
    // Otherwise return 1
    public int checkMove(Direction d) {
        Position pos = currentPosition.nextPosition(d); 
        if (!pos.inPlayArea()) return -1;
        Station nS = map.nearestStation(pos);
        if (Position.withinRange(nS.pos, pos) && !nS.isPositive) {
            return 0;
        }
        else return 1;
    }
    
    // Charges if there is a station in range
    public void charge() {
        Station nearestStation = map.nearestStation(currentPosition);
        if (Position.withinRange(nearestStation.pos, currentPosition)) {
            float[] values = nearestStation.discharge();
            coins += values[0];
            power += values[1];
        }
    }
}
