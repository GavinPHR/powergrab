package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;

/*
 * Main class that start the application
 * App class also transform the arguments to correct types
 */
public class App {
    private final int dd, mm, yyyy;
    private final double latitude, longitude;
    private long seed;
    private String state;
    // Constructor that parses the input arguments
    private App(String[] args) {
        dd = Integer.parseInt(args[0]);
        mm = Integer.parseInt(args[1]);
        yyyy = Integer.parseInt(args[2]);
        latitude = Double.parseDouble(args[3]);
        longitude = Double.parseDouble(args[4]);
        seed = Long.parseLong(args[5]);
        state = args[6];
        if (!state.equals("stateless") && !state.equals("stateful")) {
            throw new IllegalArgumentException();
        }
    };
    
    public static void main(String[] args) throws Exception {
//        args = new String[] {"16", "09", "2019", "55.944425", "-3.188396", "5678", "stateless"};        
        try {
            // Parses parameter
            App param = new App(args);
            
            // Retrieve json and make the map 
            String url = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/"
                    + "%d/%02d/%02d/powergrabmap.geojson", param.yyyy, param.mm, param.dd);
            GameMap map;
            map = new GameMap(url);
            
            // Start to move the drone
            Position pos = new Position(param.latitude, param.longitude);  // Initial position
            // State-less drone
            StatelessDrone drone;
            if (param.state.equals("stateless")) {
                 drone = new StatelessDrone(pos, map, param.seed);
            } else {  
             // State-ful drone
                drone = new StatefulDrone(pos, map, param.seed);
            } 
            while (drone.move());
            
            // Write the files (txt/geojson) to current directory.
            map.writeOut(String.join("-", new String[] {param.state, args[0], args[1], args[2]}));
            
        } catch (NumberFormatException e) {
            System.out.println("Number is not formatted correctly.");           
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.out.println("Only stateless and stateful drones are supported.");           
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.out.println("Failed writing to current directory. "
                        + "The current working directory might not be writable or "
                        + "your disk space is full.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Failed retrieving json file from webserver. "
                    + "This might be because the address is invalid or "
                    + "your internet connection is unstable.");
            System.exit(1);
        }
    }
}
