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
