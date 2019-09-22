package uk.ac.ed.inf.powergrab;
import java.lang.StrictMath;

public class test {
    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            //System.out.println(i * 22.5);
            System.out.println("(" + 0.0003 * StrictMath.cos(i * Math.PI / 8) + ", " + 0.0003 * StrictMath.sin(i * Math.PI / 8) + ")");
        }
    }
}
