package uk.ac.ed.inf.powergrab;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.JsonElement;
import com.mapbox.geojson.*;

// 16x16 grid
public class Grid {
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
    public HashMap<Integer, ArrayList<Station>> grid = new HashMap<Integer, ArrayList<Station>>(16);
    
    public Grid(FeatureCollection fc) {
        List<Feature> fs = fc.features();
        Iterator<Feature> iterator = fs.iterator();
        while (iterator.hasNext()) {
            Feature f = iterator.next();
            // Get coins value
            JsonElement elem = f.getProperty("coins");
            float coins = elem.getAsFloat();
            // Get poewr value
            elem = f.getProperty("power");
            float power = elem.getAsFloat();
            // Get position 
            Point g = (Point) f.geometry();
            Position pos = new Position(g.latitude(), g.longitude());
            // Make a new station using retrieved information
            Station station = new Station(pos, coins, power);
            // Get the region of the station
            int key = this.regionOf(station);
            // Put the station into the region
            if (grid.get(key) == null) {
                ArrayList<Station> list = new ArrayList<Station>();
                list.add(station);
                grid.put(key, list);
            } else {
                grid.get(key).add(station);
            }
        }
    }
    
    private int regionOf(Station station) {
        double latitude = station.pos.latitude;
        double longitude = station.pos.longitude;
        int key;
        
        if (latitude > 55.945329) key = 1;
        else if (latitude > 55.9444245) key = 5;
        else if (latitude > 55.943521) key = 9;
        else key = 13;
        
        if (longitude < -3.1904345) key += 0;
        else if (longitude < -3.188396) key += 1;
        else if (longitude < -3.1863575) key += 2;
        else key += 3;
        
        return key;
    }
    
    public static void main(String[] args) throws Exception {
        String url = "http://homepages.inf.ed.ac.uk/stg/powergrab/2019/01/01/powergrabmap.geojson";
        MapIO m = new MapIO();
        Grid g = new Grid(m.retrieveMap(url));
        int count = 1;
        for (int i = 1; i < 17; i++) {
            ArrayList<Station> s = g.grid.get(i);
            for (Iterator iterator = s.iterator(); iterator.hasNext();) {
                Station station = (Station) iterator.next();
                System.out.println(station.coins + " " + station.isPositive);
                count++;
            }
            System.out.println();
        }
    }
}
