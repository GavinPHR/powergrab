package uk.ac.ed.inf.powergrab;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

/*
 * This class handles all input/output
 * In powergrab, IO actions consist of map retrieval and writing out files
 */
public class IO {
    // Download the map from Informatics webserver and return it as a String
    public String retrieveJson(String url) throws IOException {
        // Open Connection
        URL mapURL = new URL(url);
        URLConnection conn = mapURL.openConnection();
        conn.setReadTimeout(10000); 
        conn.setConnectTimeout(15000);
        
        // Retrieve content byte by byte
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
        return new String(contentArray);  // Convert bytes to a String
    }
    
    // Write a string to a file in current working directory
    public void writeToFile(String content, String filename) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(filename);
        writer.println(content);
        writer.close();
    }
        
}
