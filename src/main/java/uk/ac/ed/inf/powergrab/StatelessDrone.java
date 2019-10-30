package uk.ac.ed.inf.powergrab;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StatelessDrone {
    Position currentPosition;
    float coins = 0, power = 250;
    final GameMap map;
    int moveCount = 0;
    public Random rand;
    private final Direction[] directions = Direction.values();

    
    public StatelessDrone(Position pos, GameMap map, long seed) {
        // Initial pos from command
        this.currentPosition = pos;
        this.map = map;
        map.longitudeHistory[0] = pos.longitude;
        map.latitudeHistory[0] = pos.latitude;
        rand = new Random(seed);
    }
    
    public Direction selectMove() {
        Map<Integer, Direction> positive, negative;
        // Instantiate null sets
        positive = new HashMap<Integer,Direction>(16);
        negative = new HashMap<Integer,Direction>(16);
        for (Direction d : directions) {
            int result = checkMove(d);
            if (result == 1) {
                positive.put(positive.size(), d);
            } else if (result == 0) {
                negative.put(negative.size(), d);
            } else {
                continue;
            }
        }
        if (positive.isEmpty()) {
            return negative.get(rand.nextInt(negative.size()));
        } else {
            return positive.get(rand.nextInt(positive.size()));
        }
     }
    
    public boolean move() {
        if (moveCount >= 250 || power < 1.25) return false;
        // Make log storage
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
        map.mainLog[moveCount] = String.join(",", logEntry);
        moveCount++;
        map.longitudeHistory[moveCount] = currentPosition.longitude;
        map.latitudeHistory[moveCount] = currentPosition.latitude;
        return true; 
    }
    
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
