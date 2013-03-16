package us.rddt.IRCBot.Statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages a variety of statistics for a given channel. These statistics can
 * be used to return information to any channel, or to be displayed via external
 * sources (e.g. webpages, Twitter).
 * 
 * @author Ryan Morrison
 */
public class ChannelStatistics {
    private int totalLines;
    private int totalShouts;
    private int totalUrls;
    
    private Map<String, Integer> linesMap = new HashMap<String, Integer>();
    private Map<String, Integer> shoutsMap = new HashMap<String, Integer>();
    private Map<String, Integer> urlsMap = new HashMap<String, Integer>();
    
    /**
     * Class constructor
     */
    public ChannelStatistics() {
    }
    
    /**
     * Returns the user with the most lines spoken in the channel.
     * @return the user with the most lines spoken in the channel
     */
    public Map.Entry<String, Integer> getMostLines() {
        Map.Entry<String, Integer> maximum = null;
        
        for(Map.Entry<String, Integer> entry : linesMap.entrySet()) {
            if(maximum == null || entry.getValue() > maximum.getValue()) {
                maximum = entry;
            }
        }
        
        return maximum;
    }
    
    /**
     * Returns the user with the most shouts in the channel.
     * @return the user with the most shouts in the channel
     */
    public Map.Entry<String, Integer> getMostShouts() {
        Map.Entry<String, Integer> maximum = null;
        
        for(Map.Entry<String, Integer> entry : shoutsMap.entrySet()) {
            if(maximum == null || entry.getValue() > maximum.getValue()) {
                maximum = entry;
            }
        }
        
        return maximum;
    }
    
    /**
     * Returns the user with the most URLs in the channel.
     * @return the user with the most URLs in the channel
     */
    public Map.Entry<String, Integer> getMostUrls() {
        Map.Entry<String, Integer> maximum = null;
        
        for(Map.Entry<String, Integer> entry : urlsMap.entrySet()) {
            if(maximum == null || entry.getValue() > maximum.getValue()) {
                maximum = entry;
            }
        }
        
        return maximum;
    }
    
    /**
     * Returns the total number of lines spoken in the channel.
     * @return the total number of lines spoken in the channel
     */
    public int getTotalLines() {
        return totalLines;
    }
    
    /**
     * Returns the total number of shouts in the channel.
     * @return the total number of shouts in the channel
     */
    public int getTotalShouts() {
        return totalShouts;
    }
    
    /**
     * Returns the total number of URLs in the channel.
     * @return the total number of URLs in the channel
     */
    public int getTotalUrls() {
        return totalUrls;
    }
    
    /**
     * Adds a line.
     * @param username the user who spoke in the channel
     */
    public void addLine(String username) {
        if(linesMap.containsKey(username)) {
            linesMap.put(username, (int)linesMap.get(username) + 1);
        } else {
            linesMap.put(username, 1);
        }
        totalLines++;
    }
    
    /**
     * Adds a shout.
     * @param username the user who shouted in the channel
     */
    public void addShout(String username) {
        if(shoutsMap.containsKey(username)) {
            shoutsMap.put(username, (int)shoutsMap.get(username) + 1);
        } else {
            shoutsMap.put(username, 1);
        }
        totalShouts++;
    }
    
    /**
     * Adds a URL.
     * @param username the user who entered a URL in the channel
     */
    public void addUrl(String username) {
        if(urlsMap.containsKey(username)) {
            urlsMap.put(username, (int)urlsMap.get(username) + 1);
        } else {
            urlsMap.put(username, 1);
        }
        totalUrls++;
    }
}
