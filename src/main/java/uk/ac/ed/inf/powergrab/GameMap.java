package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.gson.JsonElement;
import com.harium.storage.kdtree.KDTree;
import com.mapbox.geojson.*;

/*
 * Class that constructs the map of a game
 * The map is converted into a 2-Dimensional Tree for quick nearest neighbour lookup
 * It keeps track of the drone's status (flight path, coins, power)
 */
public class GameMap {
    private KDTree<Station> stations;  
    private List<Feature> fs;
    // An array of positive stations (with coins >=0)
    public Station[] positiveStations;
    // Below are the logs for the drone to write in
    // The logs will be written out at the end
    private final int totalMove = 250;
    public String[] mainLog = new String[totalMove];
    public double[] longitudeHistory = new double[totalMove + 1];
    public double[] latitudeHistory = new double[totalMove + 1];
    
    // Log an entry of the mainLog and longitude/latitude History
    public void log(int index, String logEntry, double latitude, double longitude) throws ArrayIndexOutOfBoundsException { 
        mainLog[index] = logEntry;
        latitudeHistory[index + 1] = latitude;
        longitudeHistory[index + 1] = longitude;
    }
    // Construct a map given the map URL 
    public GameMap(String url) throws IOException {
        IO io = new IO();
        String json = io.retrieveJson(url);
        Station[] allStations = this.jsonToStations(json);
        this.stations = this.makeTree(allStations);
        this.positiveStations = this.getPositiveStations(allStations);
    }
    
    // Getter for nearest station from a position
    public Station nearestStation(Position pos) {
        double[] co = {pos.latitude, pos.longitude};
        return this.stations.nearest(co);
    }
    
    // Write out the required txt and geojson files
    public void writeOut(String filename) throws UnsupportedEncodingException, FileNotFoundException {
        List<Point> points = new ArrayList<Point>(251);
        for (int i = 0; i < 251; i++) {
            Point p = Point.fromLngLat(longitudeHistory[i], latitudeHistory[i]);
            points.add(p);
        }
        // Add the flight path to the FeatureCollection
        LineString ls = LineString.fromLngLats(points);
        fs.add(Feature.fromGeometry(ls));
        // Write out the files
        IO io = new IO();
        io.writeToFile(FeatureCollection.fromFeatures(fs).toJson(), filename + ".geojson");
        io.writeToFile(String.join("\n", mainLog), filename + ".txt");
        return;
    }
    
    // Turn a json string into an array of Station
    private Station[] jsonToStations(String json) {
        FeatureCollection fc = FeatureCollection.fromJson(json);
        fs = fc.features();
        Iterator<Feature> iterator = fs.iterator();
        Station[] stations = new Station[50];
        int i = 0;
        // Iterate all the Feature from the FeatureCollection
        // Turning each of them into a Station object
        while (iterator.hasNext()) {
            Station s = makeStation(iterator.next());
            stations[i] = s;
            i++;
        }
        return stations;
    }
    
    // Input all the stations, return the ones that are positive (with coins >= 0)
    private Station[] getPositiveStations(Station[] allStations) {
        List<Station> positiveStations = new ArrayList<Station>(50);
        for (Station station : allStations) {
            if (station.isPositive) {
                positiveStations.add(station);
            }
        }
        return positiveStations.toArray(new Station[positiveStations.size()]);
    }
    
    // Input a Feature, return a corresponding Station object
    private Station makeStation(Feature f) {
        // Get coins value
        JsonElement elem = f.getProperty("coins");
        float coins = elem.getAsFloat();
        // Get poewr value
        elem = f.getProperty("power");
        float power = elem.getAsFloat();
        // Get position 
        Point g = (Point) f.geometry();
        Position pos = new Position(g.latitude(), g.longitude());
        // Return a new station using feature information
        return new Station(pos, coins, power);
    }
    
    // Make a 2-Dimensional Tree of Stations from an array of Stations
    private KDTree<Station> makeTree(Station[] stations) {
        KDTree<Station> kd = new KDTree<Station>(2);
        double[] co = new double[2];
        for (Station station : stations) {
            try {
                co[0] = station.pos.latitude;
                co[1] = station.pos.longitude;
                kd.insert(co, station);
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return kd;
    }
}
