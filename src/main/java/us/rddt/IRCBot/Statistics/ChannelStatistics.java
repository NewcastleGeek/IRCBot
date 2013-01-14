/*
 * This file is part of IRCBot.
 * Copyright (c) 2011-2013 Ryan Morrison
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
