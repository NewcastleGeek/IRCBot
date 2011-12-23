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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;

import us.rddt.IRCBot.IRCUtils;

/*
 * @author Ryan Morrison
 */
public class RedditWatcher implements Runnable {
	/*
	 * Class variables
	 */
	private static volatile Map<String,String> currentLinks = Collections.synchronizedMap(new HashMap<String,String>());
	private PircBotX bot;
	private String[] subreddits;

	/*
	 * Method that executes upon thread start
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		for(int i = 0; i < subreddits.length; i++) {
			try {
				update(subreddits[i]);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/*
	 * Class constructor.
	 * @param bot the IRC bot to use
	 * @param subreddits the subreddit(s) to monitor
	 */
	public RedditWatcher(PircBotX bot, String[] subreddits) {
		this.bot = bot;
		this.subreddits = subreddits;
	}

	/*
	 * Checks to see and updates accordingly if there is a new submission in a subreddit.
	 * @param subreddit the subreddit to monitor
	 * @throws MalformedURLException if the subreddit URL cannot be formed
	 * @throws IOException if the download fails
	 * @throws JSONExceptions if the JSON cannot be parsed
	 */
	private void update(String subreddit) throws MalformedURLException, IOException, JSONException {
		/*
		 * Variables.
		 */
		StringBuilder jsonToParse = new StringBuilder();
		String buffer;
		URL link = new URL("http://www.reddit.com/r/" + subreddit + "/new/.json");

		/*
		 * Opens a connection to the provided URL, and downloads the data into a temporary variable.
		 */
		HttpURLConnection conn = (HttpURLConnection)link.openConnection();
		conn.setRequestProperty("User-Agent", IRCUtils.USER_AGENT);
		if(conn.getResponseCode() != 200) {
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
		 * Parse each submission into an ArrayList of RedditLink classes.
		 * Return the best possible submission.
		 * If there are no submissions at all, return null instead.
		 */
		JSONObject parsedArray = new JSONObject(jsonToParse.toString());
		if(parsedArray.getJSONObject("data").getJSONArray("children").length() > 0) {
			RedditLink newLink = new RedditLink(parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("id"),
					parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("title"),
					parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("author"),
					parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getString("subreddit"),
					parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getLong("created_utc"),
					parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getInt("score"),
					parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data").getBoolean("over_18"));
			if(currentLinks.get(newLink.getSubreddit()) == null) {
				currentLinks.put(newLink.getSubreddit(), newLink.getId());
			}
			else if(!currentLinks.get(newLink.getSubreddit()).equals(newLink.getId())) {
				currentLinks.put(newLink.getSubreddit(), newLink.getId());
				updateChannels(newLink);
			}
			return;
		} else {
			return;
		}
	}

	/*
	 * Updates all the channels the bot is connected to with the new submission.
	 * @param redditLink the reddit submission to update
	 */
	private void updateChannels(RedditLink redditLink) {
		Iterator<Channel> itr = bot.getChannels().iterator();
		while(itr.hasNext()) {
			if(redditLink.isOver18()) {
				bot.sendMessage(itr.next(), "[r/" + redditLink.getSubreddit() + "] " + redditLink.getTitle() + " (submitted by " + redditLink.getAuthor() + " about " +  redditLink.getCreatedReadableUTC() + " ago, " + redditLink.getScore() + " points: http://redd.it/" + redditLink.getId() + ") " + Colors.BOLD + Colors.RED + "[NSFW]");
			} else {
				bot.sendMessage(itr.next(), "[r/" + redditLink.getSubreddit() + "] " + redditLink.getTitle() + " (submitted by " + redditLink.getAuthor() + " about " +  redditLink.getCreatedReadableUTC() + " ago, " + redditLink.getScore() + " points: http://redd.it/" + redditLink.getId() + ")");
			}
		}
	}
}