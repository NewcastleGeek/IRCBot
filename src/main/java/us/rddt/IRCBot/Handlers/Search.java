package us.rddt.IRCBot.Handlers;

import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import us.rddt.IRCBot.Implementations.GoogleResult;
import us.rddt.IRCBot.Implementations.GoogleSearch;

/**
 * Searches Google for a provided query string and returns the result to the channel.
 * 
 * @author Ryan Morrison
 */
public class Search implements Runnable {
    /*
     * Class variables
     */
    private MessageEvent<PircBotX> event;
    private List<Object> result;
    private StringBuilder resultText = new StringBuilder();
    private List<GoogleResult> searchResults;
    
    /**
     * Class constructor
     * @param event the MessageEvent that triggered this class
     */
    public Search(MessageEvent<PircBotX> event) {
        this.event = event;
    }
    
    /**
     * Method that executes upon thread start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @SuppressWarnings("unchecked")
    public void run() {
        try {
            // Retrieves the search results
            result = GoogleSearch.performSearch(event.getMessage().substring(3));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // The second value in the ArrayList should contain our list of results
        searchResults = (List<GoogleResult>)result.get(1);
        for(GoogleResult gr : searchResults) {
            resultText.append(gr.getTitle() + ": " + gr.getUrl() + " | ");
        }
        resultText.append("+" + result.get(0) + " more results");
        event.respond(resultText.toString());
    }
}
