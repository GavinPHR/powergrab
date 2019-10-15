package uk.ac.ed.inf.powergrab;

public abstract class Drone {
    Position currentPosition;
    float coins = 0, power = 250;
    final GameMap map;
    int moveCount = 0;
    
    public Drone(Position pos, GameMap map) {
        // Initial pos from command
        this.currentPosition = pos;
        this.map = map;
    }
    
    public abstract Direction selectMove();
    
    public boolean move() {
        System.out.println(currentPosition.latitude + " " + currentPosition.longitude + " "+ coins);
        Direction d = selectMove();
        currentPosition.longitude+= d.getHorizontal();
        currentPosition.latitude += d.getVertical();
        charge();
        moveCount++;
        if (moveCount >= 250 || power <= 0) return false;
        return true; 
    }
    
    public int checkMove(Direction d) {
        Position pos = new Position(currentPosition, d.getHorizontal(), d.getVertical());
        if (!pos.inPlayArea()) return -1;
        else if (map.nearestStation(pos).isPositive) return 1;
        else return 0;
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
