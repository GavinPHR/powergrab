package uk.ac.ed.inf.powergrab;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class IO {
    // Download the map from informatics webserver and return it as a FeatureCollection
    public String retrieveJson(String url) throws Exception {
        URL mapURL = new URL(url);
        URLConnection conn = mapURL.openConnection();
        conn.setReadTimeout(10000); //10s
        conn.setConnectTimeout(15000);
        
        ArrayList<Byte> contentList = new ArrayList<Byte>();
        InputStream input = conn.getInputStream();
        int c = input.read();
        while (c != -1) {
            contentList.add((byte) c);
            c = input.read();
        }
        byte[] contentArray = new byte[contentList.size()];
        for (int i = 0; i < contentList.size(); i++) {
            contentArray[i] = contentList.get(i);
        }
        return new String(contentArray);
    }
        
    public static void main(String[] args) throws Exception {
        String url = "http://homepages.inf.ed.ac.uk/stg/powergrab/2019/01/01/powergrabmap.geojson";
        IO m = new IO();
        try {
            System.out.println(m.retrieveJson(url));
            System.out.println("done");
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
            
    }
}
