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

package us.rddt.IRCBot.Streaming;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.IRCUtils;

/**
 * Class to handle scheduling to poll Twitter for mentions
 * @author Ryan Morrison
 */
public class TwitterMentions {
    // Static variable to hold the last mention
    private static Status lastMention = null;
    
    /**
     * Begins the scheduler to poll Twitter once every 5 minutes for new mentions
     * @param bot the bot object to send tweets to
     */
    public static void listenForTweets(PircBotX bot) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(new TwitterListener(bot), 0, 5, TimeUnit.MINUTES);
    }
    
    /**
     * Returns the last mention.
     * @return the last mention
     */
    protected static Status getLastMention() {
        return lastMention;
    }
    
    /**
     * Sets the last mention.
     * @param lastMention the last mention to set
     */
    protected static void setLastMention(Status lastMention) {
        TwitterMentions.lastMention = lastMention;
    }
}

/**
 * Polls Twitter for new mentions towards the bot. If a new mention has been
 * received, then print it to all channels the bot is in.
 * @author Ryan Morrison
 */
class TwitterListener implements Runnable {
    // Variables
    private PircBotX bot;
    
    /**
     * Class constructor.
     * @param bot the bot object to send tweets to
     */
    public TwitterListener(PircBotX bot) {
        this.bot = bot;
    }
    
    /**
     * Method that executes upon thread start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        Twitter twitter = Configuration.getTwitterInstance();
        
        try {
            // Ensure that at least one mention is available
            if(twitter.getMentionsTimeline().size() > 0) {
                // If there are no last mentions, then the bot has just been started, so save it
                if(TwitterMentions.getLastMention() == null) {
                    TwitterMentions.setLastMention(twitter.getMentionsTimeline().get(0));
                    return;
                }
                // Otherwise, if the last mention equals the most recent mention, then there is nothing
                // new to report, so just return.
                else if(TwitterMentions.getLastMention().equals(twitter.getMentionsTimeline().get(0))) {
                    return;
                }
                // Otherwise, there are new mentions
                else {
                    // Hold a list of users who have already tweeted at the bot this session
                    List<User> alreadyTweeted = new ArrayList<User>();
                    // We want to print out every new mention
                    for(Status s : twitter.getMentionsTimeline()) {
                        // If the current mention equals our last known mention, then we've displayed
                        // everything new so just return.
                        if(s.equals(TwitterMentions.getLastMention())) break;
                        
                        // To prevent abuse and flooding, we only allow one tweet to be broadcast
                        // per update check. If the user has already tweeted, skip to the next tweet.
                        if(alreadyTweeted.contains(s.getUser())) continue;
                        
                        // Ensure that we are part of the channel to broadcast to
                        Channel channelToBroadcast = bot.getChannel(Configuration.getMainChannel());
                        if(bot.getChannels().contains(channelToBroadcast)) {
                            bot.sendMessage(channelToBroadcast, "[Twitter Mention] " + Colors.BOLD + "@" + s.getUser().getScreenName() + Colors.NORMAL + ": " + s.getText());
                            alreadyTweeted.add(s.getUser());
                        }
                    }
                    // Save the new most recent mention as the last one
                    TwitterMentions.setLastMention(twitter.getMentionsTimeline().get(0));
                }
            }
        } catch (TwitterException te) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(te));
        }
    }
}
