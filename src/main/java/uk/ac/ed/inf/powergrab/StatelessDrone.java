package uk.ac.ed.inf.powergrab;
import java.util.HashMap;
import java.util.Map;

public class StatelessDrone extends Drone {
    private final Direction[] directions = Direction.values();
    
    public StatelessDrone(Position pos, GameMap map, long seed) {
        super(pos, map, seed);
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
    
    
    
}
