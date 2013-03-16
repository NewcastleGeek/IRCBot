package us.rddt.IRCBot.Statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pircbotx.Channel;

/**
 * Manages statistics for the various channels the bot is participating in.
 * 
 * @author Ryan Morrison
 */
public class Statistics {
    private static volatile Map<Channel, ChannelStatistics> statisticsMap = Collections.synchronizedMap(new HashMap<Channel, ChannelStatistics>());
    private static volatile List<Channel> disabledChannels = Collections.synchronizedList(new ArrayList<Channel>());
    
    /**
     * Adds a channel to not appear in statistics.
     * @param channel the channel to not appear
     * @return true if the channel was added, false if it already exists in the list
     */
    public static boolean addDisabledChannel(Channel channel) {
        if(!disabledChannels.contains(channel)) {
            disabledChannels.add(channel);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Checks if a channel is disabled from appearing in statistics.
     * @param channel the channel to check
     * @return true if the channel is disabled, false if it is not
     */
    public static boolean checkDisabledChannel(Channel channel) {
        return disabledChannels.contains(channel);
    }
    
    /**
     * Creates a new HashMap to clear the current statistics
     */
    public static void clearStatistics() {
        statisticsMap = Collections.synchronizedMap(new HashMap<Channel, ChannelStatistics>());
    }
    
    /**
     * Returns the entire map of statistics.
     * @return the entire map of statistics
     */
    public static Map<Channel, ChannelStatistics> getAllStatistics() {
        return statisticsMap;
    }
    
    /**
     * Returns the channel statistics of a provided channel.
     * @param channel the channel to receive statistics for
     * @return the statistics of the provided channel
     */
    public static ChannelStatistics getChannelStatistics(Channel channel) {
        if(!statisticsMap.containsKey(channel)) {
            statisticsMap.put(channel, new ChannelStatistics());
        }
        
        return statisticsMap.get(channel);
    }
    
    /**
     * Removes a channel from the disabled channels list.
     * @param channel the channel to remove
     * @return true if the channel was removed, false if it did not exist
     */
    public static boolean removeDisabledChannel(Channel channel) {
        if(disabledChannels.contains(channel)) {
            disabledChannels.remove(channel);
            return true;
        } else {
            return false;
        }
    }
}
