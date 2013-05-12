package us.rddt.IRCBot.Services.General;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import us.rddt.IRCBot.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Downloadable {
	
	private static final ObjectMapper jacksonObjectMapper = new ObjectMapper();
	
	/**
     * Downloads metadata using a given URL and deserializes it to the provided class
     * @return a new instance of the class with the deserialized JSON data
     * @param url the URL to retrieve the JSON from
     * @param toBind the Java class to deserialize to
     * @throws IOException if the download fails
     */
    public static Object downloadMetadata(URL url, Class<?> toBind) throws IOException {
        /*
         * Variables
         */
        Object deserialized;

        /*
         * Opens a connection to the provided URL, and downloads the data into a temporary variable.
         */
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("User-Agent", Configuration.getUserAgent());
        if(conn.getResponseCode() >= 400) {
            throw new IOException("Server returned response code: " + conn.getResponseCode());
        }
        
        /*
         * Deserialize the input stream.
         */
        deserialized = jacksonObjectMapper.readValue(conn.getInputStream(), toBind);

        /*
         * Disconnect from the server.
         */
        conn.disconnect();
        
        return deserialized;
    }
}
