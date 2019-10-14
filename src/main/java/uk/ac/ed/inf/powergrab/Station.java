package uk.ac.ed.inf.powergrab;

public class Station {
    Position pos;
    boolean isPositive = true;
    float coins;
    float power;
    public Station(Position pos, float coins, float power) {
        this.pos = pos;
        this.coins = coins;
        this.power = power;
        if (coins < 0 || power < 0) this.isPositive = false;
    }
    
    public float[] discharge() {
        float[] values = {coins, power};
        coins = 0;
        power = 0;
        isPositive = true;
        return values;
    }
}
