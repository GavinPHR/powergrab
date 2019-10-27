package uk.ac.ed.inf.powergrab;

/**
 * Hello world!
 *
 */
public class App {
    // Change arg to args after testing
    private final int dd, mm, yyyy;
    private final double latitude, longitude;
    private long seed;
    private String state;
    private App(String[] args) {
        dd = Integer.parseInt(args[0]);
        mm = Integer.parseInt(args[1]);
        yyyy = Integer.parseInt(args[2]);
        latitude = Double.parseDouble(args[3]);
        longitude = Double.parseDouble(args[4]);
        seed = Long.parseLong(args[5]);
        state = args[6];
    };
    
    public static void main(String[] arg) throws Exception {
        String[] args = {"01", "02", "2019", "55.9444425", "-3.188396", "5678", "stateful"};
        App param = new App(args);
        String url = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/"
                + "%d/%02d/%02d/powergrabmap.geojson", param.yyyy, param.mm, param.dd);
        GameMap map = new GameMap(url);
        Position pos = new Position(param.latitude, param.longitude);
        if (param.state.equals("stateless")) {
            StatelessDrone sd = new StatelessDrone(pos, map, param.seed);
            while (sd.move());
        } else if (param.state.equals("stateful")) {
            StatefulDrone sd = new StatefulDrone(pos, map, param.seed);
            while (sd.move());
        } else {
            System.err.println("Only stateless and stateful drones are supported.");
        }
        map.writeOut(String.join("-", new String[] {param.state, args[0], args[1], args[2]}));
    }
}
