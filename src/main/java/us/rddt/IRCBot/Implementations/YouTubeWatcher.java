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
import org.json.JSONObject;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;

import us.rddt.IRCBot.Configuration;

public class YouTubeWatcher implements Runnable {
    private static volatile Map<String,String> youtubeMap = Collections.synchronizedMap(new HashMap<String,String>());
    private PircBotX bot;
    private String user;
    
    public YouTubeWatcher(PircBotX bot, String user) {
        this.bot = bot;
        this.user = user;
    }
    
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
        JSONObject latestUpload = new JSONObject(jsonToParse.toString()).getJSONObject("feed").getJSONArray("entry").getJSONObject(0);
        
        if(!youtubeMap.containsKey(user)) {
            youtubeMap.put(user, latestUpload.getJSONObject("id").getString("$t"));
        } else if(!youtubeMap.get(user).equals(latestUpload.getJSONObject("id").getString("$t"))) {
            youtubeMap.put(user, latestUpload.getJSONObject("id").getString("$t"));
            updateChannels(new YouTubeVideo(latestUpload.getJSONObject("title").getString("$t"), latestUpload.getJSONArray("author").getJSONObject(0).getJSONObject("name").getString("$t"), latestUpload.getJSONObject("media$group").getJSONObject("yt$duration").getLong("seconds")));
        }
    }
    
    private void updateChannels(YouTubeVideo video) {
        Channel channelToBroadcast = bot.getChannel(Configuration.getMainChannel());
        if(bot.getChannels().contains(channelToBroadcast)) {
            bot.sendMessage(channelToBroadcast, "[YouTube Upload] " + Colors.BOLD + video.getTitle() + Colors.NORMAL + " (uploaded by " + video.getUploader() + ", " + video.getReadableDuration() + ")");
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
