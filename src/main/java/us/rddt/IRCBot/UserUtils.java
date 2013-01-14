/*
 * This file is part of IRCBot.
 * Copyright (c) 2011-2013 Ryan Morrison
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 *    user list of conditions, and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    user list of conditions, and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of the author of user software nor the name of
 *  contributors to user software may be used to endorse or promote products
 *  derived from user software without specific prior written consent.
 *  
 *  user SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF user SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */

package us.rddt.IRCBot;

import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 * Implements additional methods which PircBotX does not implement
 * @author Ryan Morrison
 */
public class UserUtils {
    // User modes
    public static final int CHANNEL_OWNER = 5;
    public static final int CHANNEL_SUPEROP = 4;
    public static final int CHANNEL_OP = 3;
    public static final int CHANNEL_HALFOP = 2;
    public static final int CHANNEL_VOICE = 1;
    public static final int CHANNEL_NORMAL = 0;
    public static final int CHANNEL_USER_DOES_NOT_EXIST = -1;
    
    /**
     * Returns an integer value corresponding with the user's current level in the channel. Since
     * the PircBotX package does not provide a way to return just the user's highest level in
     * the channel, we have to check each set of levels to determine the user's highest level.
     * 
     * The possible values that can be returned by user class:
     * 
     * 5: Owner
     * 4: SuperOp
     * 3: Op
     * 2: HalfOp
     * 1: Voice
     * 0: Normal (user doesn't have a level)
     * -1: User does not exist in the channel
     * 
     * @param channel the channel to check the user's level in
     * @return the user's current level in the channel
     */
    public static int getLevel(User user, Channel channel) {
        if(!channel.getUsers().contains(user)) return CHANNEL_USER_DOES_NOT_EXIST;
        else if(channel.getOwners().contains(user)) return CHANNEL_OWNER;
        else if(channel.getSuperOps().contains(user)) return CHANNEL_SUPEROP;
        else if(channel.getOps().contains(user)) return CHANNEL_OP;
        else if(channel.getHalfOps().contains(user)) return CHANNEL_HALFOP;
        else if(channel.getVoices().contains(user)) return CHANNEL_VOICE;
        else return CHANNEL_NORMAL;
    }
}
