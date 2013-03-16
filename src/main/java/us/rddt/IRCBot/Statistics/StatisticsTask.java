package us.rddt.IRCBot.Statistics;

import java.text.DecimalFormat;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.pircbotx.Channel;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.IRCUtils;

/**
 * Task that is executed automatically to update statistics.
 * @author Ryan Morrison
 */
public class StatisticsTask extends TimerTask {
    // Boolean value which is used to determine if the task is being run automatically
    // or is being executed from a manually given command.
    private boolean isManuallyExecuted = false;

    /**
     * Class constructor.
     */
    public StatisticsTask() {
    }

    /**
     * Class constructor.
     * @param isManuallyExecuted true if the task is being called via a bot command
     */
    public StatisticsTask(boolean isManuallyExecuted) {
        this.isManuallyExecuted = isManuallyExecuted;
    }

    /**
     * Method that executes upon thread start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        Twitter twitter = new TwitterFactory().getInstance();  
        twitter.setOAuthConsumer(Configuration.getTwitterConsumerKey(), Configuration.getTwitterConsumerSecret());
        twitter.setOAuthAccessToken(new AccessToken(Configuration.getTwitterAccessToken(), Configuration.getTwitterAccessSecret()));

        try {
            for(Entry<Channel, ChannelStatistics> cs : Statistics.getAllStatistics().entrySet()) {
                // Ensure the channel is allowed to be shown
                if(!Statistics.checkDisabledChannel(cs.getKey())) {
                    // Place the entries into their own variables
                    // This avoids needlessly recalculating the top values for each
                    Entry<String, Integer> mostLines = cs.getValue().getMostLines();
                    Entry<String, Integer> mostShouts = cs.getValue().getMostShouts();
                    Entry<String, Integer> mostURLs = cs.getValue().getMostUrls();
                    
                    // Rounding float values for display
                    DecimalFormat df = new DecimalFormat("00.#");
                    
                    // Post the update to Twitter!
                    // Sleep for 2 seconds after each tweet to avoid flooding Twitter and breaking API access limits.
                    twitter.updateStatus("Today in " + cs.getKey().getName() + ": " + cs.getValue().getTotalLines() + " total lines spoken, " + cs.getValue().getTotalShouts() + " total shouts and " + cs.getValue().getTotalUrls() + " total linked URLs.");
                    Thread.sleep(2000);

                    /*
                     * Some of these values can be null if nobody spoke, shout or entered a URL into the channel.
                     * We use a StringBuilder to build the string to send to Twitter, ensuring we don't
                     * add any values which would be null.
                     */
                    StringBuilder tweetBuilder = new StringBuilder();
                    if(mostLines != null) {
                        tweetBuilder.append(mostLines.getKey() + " was most chatty in " + cs.getKey().getName() + ", speaking " + mostLines.getValue() + " times (" + df.format((mostLines.getValue() * 100.0) / cs.getValue().getTotalLines()) + "% of total). ");
                    }
                    if(mostShouts != null) {
                        tweetBuilder.append(mostShouts.getKey() + " ANGRILY shouted " + mostShouts.getValue() + " times. ");
                    }

                    twitter.updateStatus(tweetBuilder.toString());
                    Thread.sleep(2000);
                    
                    // Most URLs has to go in its own tweet, since it causes the string to exceed Twitter's 140 character limit.
                    
                    if(mostURLs != null) {
                        twitter.updateStatus(mostURLs.getKey() + " went link crazy, pasting " + mostURLs.getValue() + " links into the channel. ");
                        Thread.sleep(2000);
                    }
                }
            }
        } catch (TwitterException te) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(te));
        } catch (InterruptedException ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
        }

        if(!isManuallyExecuted) {
            Statistics.clearStatistics();
        }
    }
}