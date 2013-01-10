/*
 * This file is part of IRCBot.
 * Copyright (c) 2011 Ryan Morrison
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions, and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions, and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of the author of this software nor the name of
 *  contributors to this software may be used to endorse or promote products
 *  derived from this software without specific prior written consent.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */

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
        JSONObject latestUpload = new JSONObject(jsonToParse.toString()).getJSONObject("feed").getJSONArray("entry").getJSONObject(0);
        
        /*
         * Check to see if the ID is different from the one we stored.
         * If it is, the video is considered new and should be broadcasted.
         */
        if(!youtubeMap.containsKey(user)) {
            youtubeMap.put(user, latestUpload.getJSONObject("id").getString("$t"));
        } else if(!youtubeMap.get(user).equals(latestUpload.getJSONObject("id").getString("$t"))) {
            youtubeMap.put(user, latestUpload.getJSONObject("id").getString("$t"));
            updateChannel(new YouTubeVideo(latestUpload.getJSONObject("title").getString("$t"), latestUpload.getJSONArray("author").getJSONObject(0).getJSONObject("name").getString("$t"), latestUpload.getJSONObject("media$group").getJSONObject("yt$duration").getLong("seconds"), latestUpload.getJSONObject("id").getString("$t").split("http://gdata.youtube.com/feeds/api/videos/")[1]));
        }
    }
    
    /**
     * Updates the main channel with the new video's details.
     * @param video the YouTubeVideo object of the newly uploaded video
     */
    private void updateChannel(YouTubeVideo video) {
        // Gets the channel to broadcast to
        Channel channelToBroadcast = bot.getChannel(Configuration.getMainChannel());
        // Don't bother trying to send the message if we're not joined to the main channel.
        if(bot.getChannels().contains(channelToBroadcast)) {
            bot.sendMessage(channelToBroadcast, "[YouTube Upload] " + Colors.BOLD + video.getTitle() + Colors.NORMAL + " (uploaded by " + video.getUploader() + ", " + video.getReadableDuration() + ") (http://youtu.be/" + video.getUrl() + ")");
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
