package us.rddt.IRCBot.Handlers;

import java.io.IOException;
import java.util.logging.Level;

import org.json.JSONException;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.IRCUtils;
import us.rddt.IRCBot.Implementations.UrbanLookup;

/**
 * Uses the Urban Dictionary to define words or phrases passed to the bot via a command.
 * 
 * @author Ryan Morrison
 */
public class Define implements Runnable {
    /*
     * Class variables
     */
    private MessageEvent<PircBotX> event;
    
    /**
     * Class constructor
     * @param event the MessageEvent that triggered this class
     */
    public Define(MessageEvent<PircBotX> event) {
        this.event = event;
    }
    
    /**
     * Formats a lookup into a string that can be inserted into a URL
     * @param phrase the phrase(s) to lookup in array form
     * @return the properly formatted string
     * @throws ArrayIndexOutOfBoundsException if the user did not define a phrase to look up
     */
    private String formatLookup(String[] phrase) throws ArrayIndexOutOfBoundsException {
        String temp = "";
        for(int i = 0; i < phrase.length; i++) {
            if(i == (phrase.length - 1)) temp += phrase[i];
            else temp += phrase[i] + "%20";
        }
        return temp;
    }
    
    /**
     * Method that executes upon thread-start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        /*
         * Variables
         */
        UrbanLookup lookupResult = null;
        String toDefine = event.getMessage().substring(4);
        
        /*
         * Attempts to define the phrase via UrbanDictionary. If an exception occurs,
         * return a proper error message.
         */
        try {
            lookupResult = UrbanLookup.getDefinition(formatLookup(toDefine.split(" ")));
        } catch (ArrayIndexOutOfBoundsException ex) {
        	Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            event.respond("Error while extracting definition: " + IRCUtils.trimString(toDefine, 50) + " (" + ex.getMessage() + ")");
            return;
        } catch (IOException ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            event.respond("Error while downloading definition: " + IRCUtils.trimString(toDefine, 50) + " (" + ex.getMessage() + ")");
            return;
        } catch (JSONException ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            event.respond("Error while parsing definition: " + IRCUtils.trimString(toDefine, 50) + " (" + ex.getMessage() + ")");
        }
        
        /*
         * Return the result to the user based upon whether the lookup was successful or not.
         */
        if(lookupResult.hasResult()) {
            /*
             * Don't display the result if the combined total of the definition and example is greater than 950 characters.
             * This prevents channel floods and server disconnections for excess flood.
             * Definitions printed to the channel shouldn't take up more than two messages.
             */
            if(lookupResult.getDefinition().length() + lookupResult.getExample().length() > 950) {
                event.respond("The definition for '" + lookupResult.getWord() + "' is too long to display in an IRC message. To view the definition online, follow this link: http://www.urbandictionary.com/define.php?term=" + lookupResult.getWord());
            } else {
                event.respond(lookupResult.getWord() + ": " + lookupResult.getDefinition() + " (Example: " + lookupResult.getExample() + ")");
            }
        } else {
            event.respond("The definition for '" + toDefine + "' does not exist.");
        }
    }
}
