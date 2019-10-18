package uk.ac.ed.inf.powergrab;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.gson.JsonElement;
import com.harium.storage.kdtree.KDTree;
import com.mapbox.geojson.*;

public class GameMap {
    private KDTree<Station> stations;  
    private List<Feature> fs;
    public Station[] positiveStations;
    public String[] mainLog = new String[250];
    public double[] longitudeHistory = new double[251];
    public double[] latitudeHistory = new double[251];
    
    public GameMap(String url) throws Exception {
        IO io = new IO();
        String json = io.retrieveJson(url);
        Station[] allStations = this.jsonToStations(json);
        this.stations = this.makeTree(allStations);
        this.positiveStations = this.getPositiveStations(allStations);
    }
    
    public Station nearestStation(Position pos) {
        double[] co = {pos.latitude, pos.longitude};
        return this.stations.nearest(co);
    }
    
    // currently using String for testing should be void
    // write to txt not yet implemented
    public void writeOut(String filename) throws UnsupportedEncodingException, FileNotFoundException {
        List<Point> points = new ArrayList<Point>(251);
        for (int i = 0; i < 251; i++) {
            Point p = Point.fromLngLat(longitudeHistory[i], latitudeHistory[i]);
            points.add(p);
        }
        LineString ls = LineString.fromLngLats(points);
        fs.add(Feature.fromGeometry(ls));
        IO io = new IO();
        io.writeToFile(FeatureCollection.fromFeatures(fs).toJson(), filename + ".geojson");
        io.writeToFile(String.join("\n", mainLog), filename + ".txt");
        return;
    }
    
    private Station[] jsonToStations(String json) {
        FeatureCollection fc = FeatureCollection.fromJson(json);
        fs = fc.features();
        Iterator<Feature> iterator = fs.iterator();
        Station[] stations = new Station[50];
        int i = 0;
        while (iterator.hasNext()) {
            Station s = this.makeStation(iterator.next());
            try {
                stations[i] = s;
                i++;
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        return stations;
    }
    
    // For stateful drone
    private Station[] getPositiveStations(Station[] allStations) {
        List<Station> positiveStations = new ArrayList<Station>(50);
        for (Station station : allStations) {
            if (station.isPositive) {
                positiveStations.add(station);
            }
        }
        return positiveStations.toArray(new Station[positiveStations.size()]);
    }
    
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
    
    public static void main(String[] args) throws Exception {
        String url = "http://homepages.inf.ed.ac.uk/stg/powergrab/2019/01/01/powergrabmap.geojson";
        GameMap m = new GameMap(url);
        System.out.println("map done");
        Position p = new Position(55.94605783989288,-3.1842541694641113);
        Station n = m.nearestStation(p);
        System.out.println(n.pos.latitude+" "+n.pos.longitude);
    }
}
