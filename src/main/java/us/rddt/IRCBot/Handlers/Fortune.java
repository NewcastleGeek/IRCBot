package us.rddt.IRCBot.Handlers;

import java.util.Random;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * A simple random number generator class used to predict or choose outcomes.
 * 
 * @author Ryan Morrison
 */
public class Fortune implements Runnable {
    // Variables
    private MessageEvent<PircBotX> event;

    /**
     * Class constructor
     * @param event the MessageEvent that triggered this class
     */
    public Fortune(MessageEvent<PircBotX> event) {
        this.event = event;
    }
    
    /**
     * Returns whether the contents of a string array are all equal.
     * @param values the values to compare
     * @return true if they are equal, false if they are not
     */
    private boolean isAllEqual(String[] values) {
        for(int i = 0; i < values.length; i++) {
            if(!values[i].equals(values[0])) return false;
        }
        return true;
    }

    /**
     * Parses and returns an appropriate fortune
     * @param message the fortune to parse
     * @return the user's fortune
     */
    private String parseFortune(String message) {
        // Split the message with the delimiter 'or'
        String[] splitMessage = message.split("\\s+or\\s+");
        // If the length of the new array is 1 or the options are the same, we assume the user only wants a Yes/No response
        if(splitMessage.length == 1 || isAllEqual(splitMessage)) {
            // Generate a random number and use it to return the fortune
            if(Math.random() >= 0.5) return "Yes";
            else return "No";
        } else {
            // Generate a random number and use it to return a decision
            Random generator = new Random();
            return splitMessage[generator.nextInt(splitMessage.length)].replaceAll("^\\s+", "");
        }
    }

    /**
     * Method that executes upon thread start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        try {
            event.respond(parseFortune(event.getMessage().substring(8)));
        } catch (IndexOutOfBoundsException ex) {
            return;
        }
    }
}
