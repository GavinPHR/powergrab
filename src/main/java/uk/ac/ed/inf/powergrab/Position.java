package uk.ac.ed.inf.powergrab;

import java.lang.Math;

/*
 * Position class handles everything related to positions
 */
public class Position {
    public double latitude;
    public double longitude;

    // The edges of play area
    private static final double top = 55.946233;
    private static final double bottom = 55.942617;
    private static final double left = -3.192473;
    private static final double right = -3.184319;
    
    // Static method for calculating the distance between two positions
    public static double distance(Position a, Position b) {
        return Math.sqrt(Math.pow((a.latitude - b.latitude), 2) 
                       + Math.pow((a.longitude - b.longitude), 2));
    }
    
    // Method used to determine if the drone is in range of a charging station
    public static boolean withinRange(Position a, Position b) {
        double distance = distance(a, b);
        return distance < 0.00025;
    }
    
    // Determines (roughly) which direction the drone needs to travel to reach a position
    public static Direction roughDirectionTo(Position start, Position destination) {
        // cos formula with reference [1,0]
        double x = destination.longitude - start.longitude;
        double y = destination.latitude - start.latitude;
        double deg = Math.acos(x / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))) / Math.PI * 180;
        if (y < 0) {
            deg = 360 - deg;
        }
        // Round the degree to the closest possible direction
        int i = 0;
        while (i * 22.5 < deg) {
            i++;
        }
        if (Math.abs(deg - i * 22.5) < (deg - i * 22.5 + 22.5)) {
            return Direction.values()[i % 16];
        }
        return Direction.values()[i - 1];
    }
    
    // Constructor
    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Calculate the next position given a direction to move along
    public Position nextPosition(Direction direction) {
        if (direction == null) return this;
        return new Position(latitude + direction.getVertical(),
                            longitude + direction.getHorizontal());
    }
    
    // Check if the position is inside the play area (boundaries defined above)
    public boolean inPlayArea() {
        return latitude > bottom && latitude < top && 
               longitude < right && longitude > left;
    }  
}