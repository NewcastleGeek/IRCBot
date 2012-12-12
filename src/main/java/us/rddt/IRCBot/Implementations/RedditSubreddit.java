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
import java.text.NumberFormat;

import org.json.JSONException;
import org.json.JSONObject;

import us.rddt.IRCBot.Configuration;

/**
 * Using the reddit API, this class can return information about a provided reddit
 * subreddit including a short description and subscriber count.
 * 
 * @see us.rddt.IRCBot.Implementations.URLGrabber
 * @author Ryan Morrison
 */
public class RedditSubreddit {
    /*
     * Variables
     */
     private String display_name;
     private String title;
     private long subscribers;
     private String public_description;
     private boolean over18;
     
     /**
      * Class constructor
      */
     public RedditSubreddit() {
     }
     
     /**
      * Class constructor
      * @param display_name the displayed name of the subreddit
      * @param title the title of the subreddit
      * @param subscribers the number of subscribers
      * @param public_description the short public description of the subreddit
      * @param over18 true if the subreddit is marked as NSFW
      */
     public RedditSubreddit(String display_name, String title, long subscribers, String public_description, boolean over18) {
         this.display_name = display_name;
         this.title = title;
         this.subscribers = subscribers;
         this.public_description = public_description;
         this.over18 = over18;
     }
     
     public static RedditSubreddit getSubreddit(URL link) throws IOException, JSONException {
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
         JSONObject redditSubreddit = new JSONObject(jsonToParse.toString()).getJSONObject("data");
         return new RedditSubreddit(redditSubreddit.getString("display_name"),
                 redditSubreddit.getString("title"),
                 redditSubreddit.getLong("subscribers"),
                 redditSubreddit.getString("public_description"),
                 redditSubreddit.getBoolean("over18"));
     }

    /**
     * Returns the displayed name of the subreddit
     * @return the displayed name of the subreddit
     */
    public String getDisplayName() {
        return display_name;
    }

    /**
     * Returns the title of the subreddit
     * @return the title of the subreddit
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the number of subscribers
     * @return the number of subscribers
     */
    public long getSubscribers() {
        return subscribers;
    }
    
    /**
     * Returns the number of subscribers in a formatted output string
     * @return the number of subscribers in a formatted output string
     */
    public String getFormattedSubscribers() {
        return NumberFormat.getInstance().format(subscribers);
    }

    /**
     * Returns the short public description of the subreddit
     * @return the short public description of the subreddit
     */
    public String getPublicDescription() {
        return public_description;
    }

    /**
     * Returns whether the subreddit is an 18+ subreddit
     * @return true if the subreddit is an 18+ subreddit
     */
    public boolean isOver18() {
        return over18;
    }
}
