package uk.ac.ed.inf.powergrab;
import java.util.Set;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class StatefulDrone extends StatelessDrone {
    Station[] positiveStations;
    Station target;
    Set<Station> visitedStations = new HashSet<Station>(50);
    private static final Direction[] directions = Direction.values();
    private int segmentCount = 0;
    
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
    
    @Override
    public Direction selectMove() {
        if (target == null) {
            return statelessMove();
        }
        if (target.coins == 0) {
            segmentCount = 0;
            visitedStations.add(target);
            Station tmp = nearestUnvisited();
            if (tmp == null) {
                return super.selectMove();
            } else {
                target = tmp;
            }
        }
        Direction bestMove = Position.roughDirectionTo(currentPosition, target.pos);
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
        segmentCount++;
        if (positive.isEmpty()) {
            return negative.get(rand.nextInt(negative.size()));
        } else if (segmentCount > 30) {
            segmentCount = 25;
            return positive.get(rand.nextInt(positive.size()));
        } else if (positive.values().contains(bestMove)) {
            return bestMove;
        } else {
            return positive.get(rand.nextInt(positive.size()));
        }
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
