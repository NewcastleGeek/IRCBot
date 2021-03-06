package us.rddt.IRCBot.Handlers;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import us.rddt.IRCBot.UserUtils;
import us.rddt.IRCBot.Enums.UserModes;

/**
 * Allows ops to use shorthand commands for rapidly performing actions on a user.
 * This attempts to mimic how Anope's BotServ uses shorthand, allowing for
 * ops to use commands such as !kick/!k or !ban/!b, instead of having to provide
 * commands like /mode +b to the ircd.
 * 
 * @author Ryan Morrison
 */
public class UserMode implements Runnable {
    /*
     * Class variables.
     */
    private MessageEvent<PircBotX> event;
    private UserModes mode;

    /**
     * Class constructor
     * @param event the MessageEvent that triggered this class
     * @param mode the mode to enforce on the user
     */
    public UserMode(MessageEvent<PircBotX> event, UserModes mode) {
        this.event = event;
        this.mode = mode;
    }

    /**
     * Changes the user mode if the request is in accordance with IRC rules
     * @param mode the user mode to apply
     */
    private void changeMode(UserModes mode) {
        String modeUser = event.getMessage().split(" ")[1];
        if(isAllowable(event.getChannel(), event.getUser(), event.getBot().getUser(modeUser))) {
            switch(mode) {
            case OWNER:
                event.getBot().owner(event.getChannel(), event.getBot().getUser(modeUser));
                break;
            case DEOWNER:
                event.getBot().deOwner(event.getChannel(), event.getBot().getUser(modeUser));
                break;
            case SUPEROP:
                event.getBot().superOp(event.getChannel(), event.getBot().getUser(modeUser));
                break;
            case DESUPEROP:
                event.getBot().deSuperOp(event.getChannel(), event.getBot().getUser(modeUser));
                break;
            case OP:
                event.getBot().op(event.getChannel(), event.getBot().getUser(modeUser));
                break;
            case DEOP:
                event.getBot().deOp(event.getChannel(), event.getBot().getUser(modeUser));
                break;
            case HALFOP:
                event.getBot().halfOp(event.getChannel(), event.getBot().getUser(modeUser));
                break;
            case DEHALFOP:
                event.getBot().deHalfOp(event.getChannel(), event.getBot().getUser(modeUser));
                break;
            case VOICE:
                event.getBot().voice(event.getChannel(), event.getBot().getUser(modeUser));
                break;
            case DEVOICE:
                event.getBot().deVoice(event.getChannel(), event.getBot().getUser(modeUser));
                break;
            default:
                break;
            }
        }
    }

    /**
     * Returns the provided reason for the kick/ban
     * @return the provided reason for the kick/ban
     */
    private String getReason() {
        String[] split = event.getMessage().split(" ");
        String reason = "";
        if(split.length > 1) {
            for(int i = 2; i < split.length; i++) {
                reason += split[i] + " ";
            }
        }
        return reason.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
    }

    /**
     * Ensure the kick/ban operation is in accordance to IRC rules
     * @param channel the channel the operation is being performed on
     * @param requester the user requesting the mode change
     * @param toChange the user to receive the mode change
     */
    private boolean isAllowable(Channel channel, User requester, User toChange) {
        if(UserUtils.getLevel(requester, channel) >= UserUtils.CHANNEL_HALFOP && UserUtils.getLevel(requester, channel) > UserUtils.getLevel(toChange, channel)) return true;
        else return false;
    }

    /**
     * Kicks (and bans) a user from the channel
     * @param isBan true if the user should be banned as well, false if kicking only
     */
    private void kickUser(boolean isBan) {
        // Temporary variables
        String kickUser = event.getMessage().split(" ")[1];
        String kickReason = getReason();
        // Ensure that the kick command is allowable (user is an op and is kicking someone below their level)
        if(isAllowable(event.getChannel(), event.getUser(), event.getBot().getUser(kickUser))) {
            // Don't allow users to kick the bot
            if(!kickUser.equals(event.getBot().getNick())) {
                // Kick the offending user! (Reason optional)
                if(kickReason != "") {
                    event.getBot().kick(event.getChannel(), event.getBot().getUser(kickUser), kickReason + " (" + event.getUser().getNick() + ")");
                } else {
                    event.getBot().kick(event.getChannel(), event.getBot().getUser(kickUser), "Requested (" + event.getUser().getNick() + ")");
                }
                // If we're also to ban the user, and the op is not a half op, ban the user and log it as well
                if(isBan && !event.getUser().getChannelsHalfOpIn().contains(event.getChannel())) {
                    event.getBot().ban(event.getChannel(), event.getBot().getUser(kickUser).getHostmask());
                }
            } else {
                event.respond("Why are you trying to kick me? What did I do wrong? :'(");
            }
        }
    }

    /**
     * Method that executes upon thread start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        // Execute the appropriate actions based on the mode to change.
        switch(mode) {
        case KICK:
            kickUser(false);
            break;
        case BAN:
            kickUser(true);
            break;
        default:
            changeMode(mode);
            break;
        }
    }
}
