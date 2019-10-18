package uk.ac.ed.inf.powergrab;
import java.lang.StrictMath;

public class test {
    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            Position ref = new Position(1.0, 0.0);
            ref.latitude = Math.sin(i * Math.PI / 8);
            ref.longitude = Math.cos(i * Math.PI / 8);
            System.out.println(Position.directionTo(new Position(0.0, 0.0), ref));
///            System.out.println("(" + 0.0003 * StrictMath.cos(i * Math.PI / 8) + ", " + 0.0003 * StrictMath.sin(i * Math.PI / 8) + ")");
        }
    }
}
