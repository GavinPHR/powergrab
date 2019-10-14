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
    
    public static boolean withinRange(Position a, Position b) {
        double distance = Math.sqrt(Math.pow((a.latitude - b.latitude), 2)
                        + Math.pow((a.longitude - b.longitude), 2));
        return distance < 0.00025;
    }
    
    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public Position(Position pos, double horizontalChange, double verticalChange) {
        this.latitude = pos.latitude + verticalChange;
        this.longitude = pos.longitude + horizontalChange;
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