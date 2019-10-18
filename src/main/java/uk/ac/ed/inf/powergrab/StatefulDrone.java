package uk.ac.ed.inf.powergrab;
import java.util.Set;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class StatefulDrone extends Drone {
    Station[] positiveStations;
    Station target;
    Set<Station> visitedStations = new HashSet<Station>(50);
    private static final Direction[] directions = Direction.values();
    
    public StatefulDrone(Position pos, GameMap map, long seed) {
        super(pos, map, seed);
        positiveStations = map.positiveStations;
        target = nearestUnvisited();
    }
    
    private Station nearestUnvisited() {
        Arrays.sort(positiveStations, Comparator.comparingDouble
                (x -> Position.distance(x.pos, currentPosition)));
        for (Station station : positiveStations) {
            if (!visitedStations.contains(station)) {
                return station;
            }
        }
        return null;
    }
    
    public Direction selectMove() {
        if (target == null) {
            return statelessMove();
        }
        System.out.println(target.coins);
        if (target.coins == 0) {
            System.out.println("hahah");
            visitedStations.add(target);
            Station tmp = nearestUnvisited();
            if (tmp == null) {
                return statelessMove();
            } else {
                target = tmp;
            }
        }
        return Position.roughDirectionTo(currentPosition, target.pos);
    }
    
    public Direction statelessMove() {
        Map<Integer, Direction> positive, negative;
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
