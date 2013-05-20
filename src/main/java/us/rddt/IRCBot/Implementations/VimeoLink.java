package us.rddt.IRCBot.Implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.IRCUtils;

/**
 * A simple class to define a data type for a Vimeo video.
 * 
 * @see us.rddt.IRCBot.Implementations.URLGrabber
 * @author Ryan Morrison
 */
public class VimeoLink {
    /*
     * Variables
     */
    private String title;
    private String uploader;
    private long duration;

    /**
     * Class constructor
     */
    public VimeoLink() {
    }
    
    /**
     * Class constructor
     * @param title the video's title
     * @param duration the video's duration
     */
    public VimeoLink(String title, String uploader, long duration) {
        this.title = title;
        this.uploader = uploader;
        this.duration = duration;
    }

    /**
     * Gets information about a provided link to a YouTube video
     * @return a new instance of the class with the video's details
     * @param link the link to the user page
     * @throws IOException if the download fails
     * @throws JSONException if the JSON cannot be parsed
     */
    public static VimeoLink getLink(URL link) throws IOException, JSONException {
        /*
         * Variables
         */
        StringBuilder jsonToParse = new StringBuilder();
        String buffer;

        /*
         * Opens a connection to the provided URL, and downloads the data into a temporary variable.
         */
        HttpURLConnection conn = (HttpURLConnection)link.openConnection();
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
         * Parse the JSON data.
         */
        JSONObject vimeoLink = new JSONArray(jsonToParse.toString()).getJSONObject(0);
        return new VimeoLink(IRCUtils.escapeHTMLEntities(vimeoLink.getString("title")), vimeoLink.getString("user_name"), vimeoLink.getLong("duration"));
    }

    /**
     * Returns the video's title
     * @return the video's title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Returns the video's uploader
     * @return the video's uploader
     */
    public String getUploader() {
        return uploader;
    }

    /**
     * Returns the video's duration
     * @return the video's duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Returns the video's duration in a readable string format
     * @return the video's duration in a readable string format
     */
    public String getReadableDuration() {
        return IRCUtils.toReadableMinutes(getDuration());
    }
}
