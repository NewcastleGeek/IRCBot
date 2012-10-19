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
