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
