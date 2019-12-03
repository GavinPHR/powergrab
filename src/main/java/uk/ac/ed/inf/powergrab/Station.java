package uk.ac.ed.inf.powergrab;

/*
 * Class for charging stations
 * It characterises a station's status completely
*/
public class Station {
    public final Position pos;
    public boolean isPositive = true;  // A station is positive if coins >= 0
    public float coins;
    public float power;
    
    // Constructor for Station given positon, coins, and power
    public Station(Position pos, float coins, float power) {
        this.pos = pos;
        this.coins = coins;
        this.power = power;
        if (coins < 0 || power < 0) this.isPositive = false;
    }
    
    // This method is called when a drone charges this station.
    public float[] discharge() {
        float[] values = {coins, power};
        coins = 0;
        power = 0;
        isPositive = true;
        return values;
    }
}
