package us.rddt.IRCBot.Implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.IRCUtils;

/**
 * Queries the Urban Dictionary for a definition for a provided string. The results
 * are provided in JSON format, which is then parsed and the definition returned
 * to the user who requested it.
 * 
 * @see us.rddt.IRCBot.Handlers.Define
 * @author Ryan Morrison
 */
public class UrbanLookup {
    /*
     * Class variables
     */
    private boolean hasResult;
    private String word;
    private String definition;
    private String example;
    
    /**
     * Class constructor
     */
    public UrbanLookup() {  
    }
    
    /**
     * Class constructor
     * @param hasResult if the lookup returned a result or not
     * @param word the defined word
     * @param definition the word's definition
     * @param example an example of the word
     */
    public UrbanLookup(boolean hasResult, String word, String definition, String example) {
        this.hasResult = hasResult;
        this.word = word;
        this.definition = definition;
        this.example = example;
    }
    
    /**
     * Gets the definition of a provided word
     * @param toDefine the word to define
     * @return a new instance of the class with the definition
     * @throws IOException if the download fails
     * @throws JSONException if the JSON cannot be parsed
     */
    public static UrbanLookup getDefinition(String toDefine) throws IOException, JSONException {
        URL lookupURL = null;
        StringBuilder jsonToParse = new StringBuilder();
        String buffer;
        
        try {
            lookupURL = new URL("http://www.urbandictionary.com/iphone/search/define?term=" + toDefine);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        
        /*
         * Opens a connection to the provided URL, and downloads the data into a temporary variable.
         */
        HttpURLConnection conn = (HttpURLConnection)lookupURL.openConnection();
        conn.setRequestProperty("User-Agent", Configuration.getUserAgent());
        if(conn.getResponseCode() >= 400) {
            throw new IOException(IRCUtils.getHttpStatusErrorString(conn.getResponseCode()) + " (" + conn.getResponseCode() + ")");
        }

        BufferedReader buf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while((buffer = buf.readLine()) != null) {
            jsonToParse.append(buffer);
        }

        /*
         * Disconnect from the server.
         */
        conn.disconnect();
        
        JSONObject lookupResult = new JSONObject(jsonToParse.toString());
        if(!lookupResult.getString("result_type").equals("exact")) {
            return new UrbanLookup(false, null, null, null);
        } else {
            return new UrbanLookup(true,
                    lookupResult.getJSONArray("list").getJSONObject(0).getString("word"),
                    lookupResult.getJSONArray("list").getJSONObject(0).getString("definition"),
                    lookupResult.getJSONArray("list").getJSONObject(0).getString("example"));
        }
    }

    /**
     * Returns true if the definition lookup succeeded, false if not
     * @return true if the definition lookup succeeded, false if not
     */
    public boolean hasResult() {
        return hasResult;
    }

    /**
     * Returns the defined word
     * @return the defined word
     */
    public String getWord() {
        return word;
    }

    /**
     * Returns the word's definition
     * @return the word's definition
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Returns the word's example
     * @return the word's example
     */
    public String getExample() {
        return example;
    }
}
