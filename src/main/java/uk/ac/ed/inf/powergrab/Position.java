package uk.ac.ed.inf.powergrab;

public class Position {
    public double latitude;
    public double longitude;
    private final double top = 55.946233;
    private final double bottom = 55.942617;
    private final double left = -3.192473;
    private final double right = -3.184319;
            
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
    
//    public static void main(String[] args) {
//        Position p = new Position(55.944425,-3.188396);
//        System.out.println(p.nextPosition(null).latitude + "  " + p.nextPosition(Direction.SSE).longitude);
//    }
}