package us.rddt.IRCBot.Handlers;

import java.util.regex.Pattern;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import us.rddt.IRCBot.Enums.TopicUpdates;

/**
 * Allows ops to update topics by appending or removing provided strings. IRC
 * doesn't provide a way to simply append or remove portions of a topic string
 * without needing to rewrite the whole topic string, which can be a major annoyance.
 * 
 * @author Ryan Morrison
 */
public class Topic implements Runnable {
    // Variables
    private MessageEvent<PircBotX> event;
    private TopicUpdates updateMode;
    
    /**
     * Class constructor
     * @param event the MessageEvent that triggered this class
     */
    public Topic(MessageEvent<PircBotX> event, TopicUpdates updateMode) {
        this.event = event;
        this.updateMode = updateMode;
    }
    
    /**
     * Appends a string to the current channel's topic
     * @param channel the channel to apply the topic update to
     * @param appendString the string to append to the topic
     */
    private void appendToTopic(Channel channel, String appendString) {
        if(!appendString.isEmpty()) event.getBot().setTopic(channel, channel.getTopic() + " " + appendString);
    }
    
    /**
     * Removes a string from the current channel's topic, if the string exists within the current topic
     * @param channel the channel to apply the topic update to
     * @param removeString the string to remove from the topic
     */
    private void removeFromTopic(Channel channel, String removeString) {
        if(!removeString.isEmpty()) {
            String currentTopic = channel.getTopic();
            String newTopic = currentTopic.replaceFirst(Pattern.quote(removeString), "");
            if(!currentTopic.equals(newTopic)) {
                event.getBot().setTopic(channel, newTopic);
            }
        }
    }
    
    /**
     * Method that executes upon thread start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        switch(updateMode) {
        case ADD_TO_TOPIC:
            appendToTopic(event.getChannel(), event.getMessage().substring(13));
            break;
        case REMOVE_FROM_TOPIC:
            removeFromTopic(event.getChannel(), event.getMessage().substring(13));
            break;
        case RESET_TOPIC:
            break;
        default:
            return;
        }
    }
}
