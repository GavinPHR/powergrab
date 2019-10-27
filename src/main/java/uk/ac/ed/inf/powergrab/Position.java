package uk.ac.ed.inf.powergrab;
import java.lang.Math;

public class Position {
    public double latitude;
    public double longitude;

    // The edges of play area 
    private final double top = 55.946233;
    private final double bottom = 55.942617;
    private final double left = -3.192473;
    private final double right = -3.184319;
    
    public static double distance(Position a, Position b) {
        return Math.sqrt(Math.pow((a.latitude - b.latitude), 2) 
                       + Math.pow((a.longitude - b.longitude), 2));
    }
    
    public static boolean withinRange(Position a, Position b) {
        double distance = distance(a, b);
        return distance < 0.00025;
    }
    
    public static Direction roughDirectionTo(Position start, Position destination) {
        // cos formula with reference [1,0]
        double x = destination.longitude - start.longitude;
        double y = destination.latitude - start.latitude;
        double deg = Math.acos(x/Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))) / Math.PI * 180;
        if (y < 0) {
            deg = 360 - deg;
        }
        int i = 0;
        while (i * 22.5 < deg) {
            i++;
        }
        if (Math.abs(deg - i * 22.5) < (deg - i * 22.5 + 22.5)) {
            return Direction.values()[i % 16];
        }
        return Direction.values()[i - 1];
    }
    
    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public Position nextPosition(Direction direction) {
        if (direction == null) return this;
        return new Position(latitude + direction.getVertical(),
                            longitude + direction.getHorizontal());
    }
    
    public boolean inPlayArea() {
        return latitude > bottom && latitude < top && 
               longitude < right && longitude > left;
    }
    
}