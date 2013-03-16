package us.rddt.IRCBot.Handlers;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import us.rddt.IRCBot.Enums.StatisticsModes;
import us.rddt.IRCBot.Statistics.Statistics;

/**
 * Handles disabling and enabling channels from appearing in the statistics list.
 * @author Ryan Morrison
 */
public class StatisticsHandler implements Runnable {
    private MessageEvent<PircBotX> event;
    private StatisticsModes mode;
    
    /**
     * Class constructor
     * @param event the MessageEvent that triggered this class
     * @param mode the mode this class should operate in
     */
    public StatisticsHandler(MessageEvent<PircBotX> event, StatisticsModes mode) {
        this.event = event;
        this.mode = mode;
    }
    
    /**
     * Disables a channel from appearing in the statistics list.
     */
    private void addDisabledChannel() {
        if(Statistics.addDisabledChannel(event.getChannel())) {
            event.respond(event.getChannel().getName() + " will not appear in statistics.");
        } else {
            event.respond(event.getChannel().getName() + " has already been disabled!");
        }
    }
    
    /**
     * Allows a channel previously disabled to appear in the statistics list.
     */
    private void removeDisabledChannel() {
        if(Statistics.removeDisabledChannel(event.getChannel())) {
            event.respond(event.getChannel().getName() + " will now appear in statistics.");
        } else {
            event.respond(event.getChannel().getName() + " has not been disabled!");
        }
    }
    
    /**
     * Method that executes upon thread start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        switch(mode) {
        case ADD:
            addDisabledChannel();
            break;
        case REMOVE:
            removeDisabledChannel();
            break;
        default:
            return;
        }
    }
}
