package us.rddt.IRCBot.Implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.NoSuchElementException;

import org.json.JSONException;
import org.json.JSONObject;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.IRCUtils;

/**
 * A simple class to define a data type for a YouTube video.
 * 
 * @see us.rddt.IRCBot.Implementations.URLGrabber
 * @author Ryan Morrison
 */
public class YouTubeVideo {
    /*
     * Variables
     */
    private String title;
    private String uploader;
    private long duration;
    private String url;

    /**
     * Class constructor
     */
    public YouTubeVideo() {
    }
    
    /**
     * Class constructor
     * @param title the video's title
     * @param uploader the video's uploader
     * @param duration the video's duration
     */
    public YouTubeVideo(String title, String uploader, long duration) {
        this.title = title;
        this.uploader = uploader;
        this.duration = duration;
    }
    
    /**
     * Class constructor
     * @param title the video's title
     * @param uploader the video's uploader
     * @param duration the video's duration
     * @param url the URL of the video
     */
    public YouTubeVideo(String title, String uploader, long duration, String url) {
        this.title = title;
        this.uploader = uploader;
        this.duration = duration;
        this.url = url;
    }

    /**
     * Gets information about a provided link to a YouTube video
     * @return a new instance of the class with the video's details
     * @param link the link to the user page
     * @throws IOException if the download fails
     * @throws JSONException if the JSON cannot be parsed
     */
    public static YouTubeVideo getLink(URL link) throws IOException, JSONException {
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
        JSONObject parsedArray = new JSONObject(jsonToParse.toString());
        if(parsedArray.getJSONObject("data").getInt("totalItems") > 0) {
            JSONObject youtubeLink = parsedArray.getJSONObject("data").getJSONArray("items").getJSONObject(0);
            return new YouTubeVideo(IRCUtils.escapeHTMLEntities(youtubeLink.getString("title")), youtubeLink.getString("uploader"), youtubeLink.getLong("duration"));
        } else {
            throw new NoSuchElementException("YouTube video ID invalid or video is private.");
        }
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
     * Returns the video's URL
     * @return the video's URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the video's duration in a readable string format
     * @return the video's duration in a readable string format
     */
    public String getReadableDuration() {
        return IRCUtils.toReadableMinutes(getDuration());
    }
}
