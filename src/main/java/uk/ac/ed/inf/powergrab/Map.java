package uk.ac.ed.inf.powergrab;
import java.util.Iterator;
import java.util.List;
import com.google.gson.JsonElement;
import com.harium.storage.kdtree.KDTree;
import com.mapbox.geojson.*;

public class Map {
    private KDTree<Station> stations;
    
    // Station class instead of JSON, easier to deal with
    private class Station {
        Position pos;
        boolean isPositive = true;
        float coins;
        float power;
        private Station(Position pos, float coins, float power) {
            this.pos = pos;
            this.coins = coins;
            this.power = power;
            if (coins < 0 || power < 0) this.isPositive = false;
        }
    }
    
    public Map(String url) throws Exception {
        IO io = new IO();
        String json = io.retrieveJson(url);
        this.stations = this.makeTree(this.jsonToStations(json));
    }
    
    public Station nearestStation(Position pos) {
        double[] co = {pos.latitude, pos.longitude};
        return this.stations.nearest(co);
    }
    
    private Station[] jsonToStations(String json) {
        FeatureCollection fc = FeatureCollection.fromJson(json);
        List<Feature> fs = fc.features();
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
        Map m = new Map(url);
        System.out.println("map done");
        Position p = new Position(55.94605783989288,-3.1842541694641113);
        Station n = m.nearestStation(p);
        System.out.println(n.pos.latitude+" "+n.pos.longitude);
    }
}
