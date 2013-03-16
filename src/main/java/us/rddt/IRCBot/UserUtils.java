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
