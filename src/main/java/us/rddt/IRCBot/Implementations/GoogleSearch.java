package us.rddt.IRCBot.Implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.IRCUtils;

/**
 * Performs a Google search with a provided query string. The results are returned
 * in JSON format, which is then parsed and a string is built to return to the user
 * who requested the search.
 * 
 * @see us.rddt.IRCBot.Handlers.Search
 * @author Ryan Morrison
 */
public class GoogleSearch {
    /**
     * Performs a Google Search based on the provided query and returns the results
     * @param query the search query
     * @return the total number of results and the results as a List<GoogleResult>
     * @throws IOException if the download fails
     * @throws JSONException if the JSON cannot be parsed
     */
    public static List<Object> performSearch(String query) throws IOException, JSONException {
        /*
         * Variables.
         */
        StringBuilder jsonToParse = new StringBuilder();
        String buffer;
        URL searchUrl = new URL("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + query.replace(" ", "%20"));
        
        String resultCount = null;
        
        List<GoogleResult> results = new ArrayList<GoogleResult>();
        List<Object> toReturn = new ArrayList<Object>();

        /*
         * Opens a connection to the provided URL, and downloads the data into a temporary variable.
         */
        HttpURLConnection conn = (HttpURLConnection)searchUrl.openConnection();
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
        
        /*
         * Parses the array and prepares the ArrayLists to be returned
         */
        JSONObject object = new JSONObject(jsonToParse.toString());
        JSONArray parsedArray = object.getJSONObject("responseData").getJSONArray("results");
        for(int i = 0; i < parsedArray.length(); i++) {
            results.add(new GoogleResult(parsedArray.getJSONObject(i).getString("url"), parsedArray.getJSONObject(i).getString("titleNoFormatting")));
        }
        resultCount = object.getJSONObject("responseData").getJSONObject("cursor").getString("resultCount");

        toReturn.add(resultCount);
        toReturn.add(results);
        
        return toReturn;
    }
}
