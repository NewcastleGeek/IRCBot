package us.rddt.IRCBot.Implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.Services.YouTube.Channel.Entry;
import us.rddt.IRCBot.Services.YouTube.Channel.Upload;

/**
 * Monitors a configured set of YouTube channels for newly uploaded videos.
 * The bot will broadcast a notification of these videos to the main channel.
 * 
 * @author Ryan Morrison
 */
public class YouTubeWatcher implements Runnable {
    // Map of YouTube users and their latest known IDs
    private static volatile Map<String,String> youtubeMap = Collections.synchronizedMap(new HashMap<String,String>());
    
    // Variables
    private PircBotX bot;
    private String user;
    
    /**
     * Class constructor
     * @param bot the IRCBot to use
     * @param user the YouTube user to query
     */
    public YouTubeWatcher(PircBotX bot, String user) {
        this.bot = bot;
        this.user = user;
    }
    
    /**
     * Checks a YouTube user for a newly uploaded video. If a new video exists,
     * it is broadcasted to the configured main channel.
     * @param user the YouTube user to query
     * @throws IOException if the download fails
     * @throws JSONException if the JSON cannot be parsed
     */
    private void checkForUpload(String user) throws IOException, JSONException {
        /*
         * Variables.
         */
        StringBuilder jsonToParse = new StringBuilder();
        String buffer;
        URL apiUrl = new URL("https://gdata.youtube.com/feeds/api/users/" + user + "/uploads?alt=json");

        /*
         * Opens a connection to the provided URL, and downloads the data into a temporary variable.
         */
        HttpURLConnection conn = (HttpURLConnection)apiUrl.openConnection();
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
         * Get the latest upload by the user.
         */
        ObjectMapper mapper = new ObjectMapper();
        Entry latestUpload = mapper.readValue(jsonToParse.toString(), Upload.class).getFeed().getEntry().iterator().next();

        /*
         * Check to see if the ID is different from the one we stored.
         * If it is, the video is considered new and should be broadcasted.
         */
        if(!youtubeMap.containsKey(user)) {
        	youtubeMap.put(user, latestUpload.getId().getValue());
        } else if(!youtubeMap.get(user).equals(latestUpload.getId().getValue())) {
        	youtubeMap.put(user, latestUpload.getId().getValue());
        	updateChannel(latestUpload);
        }
    }
    
    /**
     * Updates the main channel with the new video's details.
     * @param video the YouTubeVideo object of the newly uploaded video
     */
    private void updateChannel(Entry entry) {
        // Gets the channel to broadcast to
        Channel channelToBroadcast = bot.getChannel(Configuration.getMainChannel());
        // Don't bother trying to send the message if we're not joined to the main channel.
        if(bot.getChannels().contains(channelToBroadcast)) {
            bot.sendMessage(channelToBroadcast, "[YouTube Upload] " + Colors.BOLD + entry.getTitle().getValue() + Colors.NORMAL + " (uploaded by " + entry.getAuthor().iterator().next().getName().getValue() + ", " + entry.getMediaGroup().getDuration().getSeconds() + ") (http://youtu.be/" + entry.getId().getValue().split("http://gdata.youtube.com/feeds/api/videos/")[1] + ")");
        }
    }
    
    /**
     * Method that executes upon thread start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            checkForUpload(user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
