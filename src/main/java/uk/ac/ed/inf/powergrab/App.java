package uk.ac.ed.inf.powergrab;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws Exception {
        for (int i = 1; i < 30; i++) {
            String url = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%d/%02d/%02d/powergrabmap.geojson", 2019, 9, i);
            GameMap map = new GameMap(url);
            Position pos = new Position(55.9444425, -3.188396);
            StatelessDrone sd = new StatelessDrone(pos, map, 5678);
            int j = 0;
            while (sd.move()) j++;
            System.out.println(j);
        }
        
    }
}
