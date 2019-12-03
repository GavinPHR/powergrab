package uk.ac.ed.inf.powergrab;

import java.util.Set;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Collections;

/*
 * The StatefulDrone class extends the statelessDrone class
 * because the state-ful drone is implemented to exibit 
 * state-less behaviour once all the coins are collected
 */
public class StatefulDrone extends StatelessDrone {
    // An reference to positive stations for the drone to select
    private Station[] positiveStations;
    // The target station the drone is aiming to reach
    private Station target;
    // Keep track of the visited stations
    private Set<Station> visitedStations = new HashSet<Station>(50);
    // Keep track of the previous move so to not repeat it
    private Direction lastMove = Direction.N;
    // Keep track of how many moves are made since last charge
    private int segmentCount = 0;
    
    // Constructor
    public StatefulDrone(Position pos, GameMap map, long seed) {
        super(pos, map, seed);
        positiveStations = map.positiveStations;
        target = nearestUnvisited();
    }
    
    // Select the nearest unvisited positive station
    private Station nearestUnvisited() {
        // Sort the positive stations according to its distance from the drone
        Arrays.sort(positiveStations, Comparator.comparingDouble
                (x -> Position.distance(x.pos, currentPosition)));
        // Select the closest one that is not visited
        for (Station station : positiveStations) {
            if (!visitedStations.contains(station)) {
                return station;
            }
        }
        // If all visited, return null
        return null;
    }
    
    // Select a random unvisited positive station
    private Station randomUnvisited() {
        List<Station> stationList = Arrays.asList(positiveStations);
        Collections.shuffle(stationList);
        for (Station station : stationList) {
            if (!visitedStations.contains(station)) {
                return station;
            }
        }
        return null;
    }
    
    // Override superclass (state-less) selectMove
    // Now using global knowledge
    @Override
    protected Direction selectMove() {
        // If all positive stations are visited, fallback to state-less move
        if (target == null) {
            return super.selectMove();
        }
        
        // If the target is visited, switch target
        if (target.coins == 0) {
            // Reset segmentCount
            segmentCount = 0;
            // Add current target to visitedStations
            visitedStations.add(target);
            Station newTarget = nearestUnvisited();
            if (newTarget == null) {
                return super.selectMove();
            } else {
                // Switch to a new target
                target = newTarget;
            }
        }
        
        // If the drone moved 20 times and didn't succeed in reaching the target
        // get a random target and resume
        if (segmentCount > 20) {
            segmentCount = 0;
            target = randomUnvisited();
        }
        
        // Determine what the best direction to move in
        Direction bestMove = Position.roughDirectionTo(currentPosition, target.pos);
        // Find positive and negative directions
        Map<Integer, Direction> positive, negative;
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
        
        // Increase the segmentCount before returning
        segmentCount++;
        // If all the moves are negative, choose a random one
        if (positive.isEmpty()) {
            return negative.get(rand.nextInt(negative.size()));
        } else if (lastMove == bestMove.complement()) {
            // If the best move is the complement of the previous move
            // choose a random positive move
            lastMove = positive.get(rand.nextInt(positive.size()));
            return lastMove;
        } else if (positive.values().contains(bestMove)) {
            // If positive moves contain the best move
            // return the best move
            lastMove = bestMove;
            return lastMove;
        } else {
            // If none of the conditions above are met
            // Return a random positive move
            lastMove = positive.get(rand.nextInt(positive.size()));
            return lastMove;
        }
    }   
}
