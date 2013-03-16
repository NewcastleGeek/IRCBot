package us.rddt.IRCBot.Implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import us.rddt.IRCBot.Configuration;

/**
 * Using the reddit API, this class can return information about a provided reddit
 * user's page including their karma scores, account creation date and whether the
 * user has a subscription to reddit gold.
 * 
 * @see us.rddt.IRCBot.Implementations.URLGrabber
 * @author Ryan Morrison
 */
public class RedditUser {
    /*
     * Variables
     */
    private String name;
    private int link_karma;
    private int comment_karma;
    private long created;
    private boolean isGold;

    /**
     * Class constructor
     */
    public RedditUser() {
    }
    
    /**
     * Class constructor
     * @param name the user's name
     * @param link_karma the user's link karma
     * @param comment_karma the user's comment karma
     * @param created the date of the user's creation
     * @param isGold true if the user is a reddit gold member, false if they are not
     */
    public RedditUser(String name, int link_karma, int comment_karma, long created, boolean isGold) {
        this.name = name;
        this.link_karma = link_karma;
        this.comment_karma = comment_karma;
        this.created = created;
        this.isGold = isGold;
    }

    /**
     * Gets information about a provided link to a Reddit user page.
     * @param link the link to the user page
     * @return a new instance of the class with the user's details
     * @throws IOException if the download fails
     * @throws JSONException if the JSON cannot be parsed
     */
    public static RedditUser getUser(URL link) throws IOException, JSONException {
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
            throw new IOException("Server returned response code: " + conn.getResponseCode());
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
        JSONObject redditUser = new JSONObject(jsonToParse.toString()).getJSONObject("data");
        return new RedditUser(redditUser.getString("name"),
                redditUser.getInt("link_karma"),
                redditUser.getInt("comment_karma"),
                redditUser.getLong("created"),
                redditUser.getBoolean("is_gold"));
    }

    /**
     * Returns the user's name
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the user's link karma
     * @return the user's link karma
     */
    public int getLinkKarma() {
        return link_karma;
    }

    /**
     * Returns the user's comment karma
     * @return the user's comment karma
     */
    public int getCommentKarma() {
        return comment_karma;
    }

    /**
     * Returns the user's creation date
     * @return the user's creation date
     */
    public long getCreated() {
        return created;
    }

    /**
     * Returns the user's creation date in a readable string format
     * @return the user's creation date in a readable string format
     */
    public String getReadableCreated() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(new Date(getCreated() * 1000));
    }

    /**
     * Returns if the user is a Reddit Gold member or not
     * @return true if the user is a Reddit Gold member, false if the user is not
     */
    public boolean isGold() {
        return isGold;
    }
}
