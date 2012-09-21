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

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

import org.json.JSONException;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.IRCUtils;
import us.rddt.IRCBot.Implementations.SteamUser;

/**
 * Queries the Steam Web API to retrieve data for a provided user.
 * @author Ryan Morrison
 */
public class SteamUserQuery implements Runnable {
    // Variables
    private MessageEvent<PircBotX> event;

    /**
     * Class constructor
     * @param event the MessageEvent that triggered this class
     */
    public SteamUserQuery(MessageEvent<PircBotX> event) {
        this.event = event;
    }

    /**
     * Builds the string to return the user
     * @param user the Steam user being queried
     * @return a string containing the user's status
     */
    private String buildUserQueryString(SteamUser user) {
        StringBuilder builder = new StringBuilder();

        builder.append(user.getPersonaName() + " is ");
        if(user.getPersonaState() == 0) builder.append(SteamUser.personaStates[user.getPersonaState()] + " (last online: " + IRCUtils.toReadableTime(new Date(user.getLastLogOff() * 1000), false, true) + " ago)");
        else if(user.getPersonaState() > 4) builder.append("online and is " + SteamUser.personaStates[user.getPersonaState()]);
        else builder.append(SteamUser.personaStates[user.getPersonaState()]);
        if(user.getGameExtraInfo() != null) builder.append(", currently in-game: " + user.getGameExtraInfo());
        if(user.getGameServerIp() != null) builder.append(" (server IP: " + user.getGameServerIp() + ")");

        return builder.toString();
    }

    /**
     * Checks to see whether the passed Steam ID is a numeric ID or community ID (string)
     * @param id the ID 
     * @return true if the ID value is numeric
     */
    private boolean isNumericID(String id) {
        try {
            Long.parseLong(id);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Method that executes upon thread start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        SteamUser queriedUser;
        String givenId = event.getMessage().split(" ")[1];
        if(!Configuration.getSteamAPIKey().isEmpty()) {
            try {
                if(isNumericID(givenId)) {
                    queriedUser = new SteamUser(Long.parseLong(givenId), true);
                } else {
                    queriedUser = new SteamUser(givenId, true);
                }
                event.respond(buildUserQueryString(queriedUser));
            } catch (IOException ex) {
                if(ex.getMessage().equals("Server returned response code: 401")) {
                    event.respond("Steam Web API key is invalid. Please add a valid Steam Web API key and reload configuration.");
                    Configuration.getLogger().write(Level.WARNING, "Steam Web API key invalid");
                }
            } catch(JSONException ex) {
                if(ex.getMessage().equals("JSONArray[0] not found.")) {
                    event.respond("Could not retrieve Steam user details - user does not exist.");
                } else {
                    Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
                }
            } catch(NullPointerException ex) {
                event.respond("Could not convert Community Name to 64-bit Steam ID. (User may not exist)");
            } catch (Exception ex) {
                Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            }
        } else {
            event.respond("Steam Web API key has not been configured. Please add a valid Steam Web API key and reload configuration.");
            Configuration.getLogger().write(Level.WARNING, "Steam Web API key not configured");
        }
    }
}
