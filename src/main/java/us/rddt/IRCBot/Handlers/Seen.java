package us.rddt.IRCBot.Handlers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.Database;
import us.rddt.IRCBot.IRCUtils;

/**
 * Since the services we use on the ircd doesn't support the !seen command, our bot
 * will reimplement it. On a user quitting or leaving the IRC channel, a database
 * is updated and maintained with the user's nick and when they were last in the
 * channel. The database can be queried via a !seen command to determine when a user
 * was last in the channel.
 * 
 * @author Ryan Morrison
 */
public class Seen implements Runnable {
    private Database database;
    /*
     * Class variables.
     */
    private MessageEvent<PircBotX> event;
    private boolean hasParted;
    private KickEvent<PircBotX> kEvent;
    private PartEvent<PircBotX> pEvent;
    private QuitEvent<PircBotX> qEvent;

    private String seenUser;

    /**
     * Class constructor
     * @param event the event that triggered this class
     */
    public Seen(MessageEvent<PircBotX> event) {
        this.event = event;
    }
    
    /**
     * Overloadable class constructor
     * @param kEvent the kicking event that triggered this class
     */
    public Seen(KickEvent<PircBotX> kEvent) {
        this.kEvent = kEvent;
        this.hasParted = true;
    }

    /**
     * Overloadable class constructor
     * @param pEvent the parting event that triggered this class
     */
    public Seen(PartEvent<PircBotX> pEvent) {
        this.pEvent = pEvent;
        this.hasParted = true;
    }

    /**
     * Overloadable class constructor
     * @param qEvent the server quit event that triggered this class
     */
    public Seen(QuitEvent<PircBotX> qEvent) {
        this.qEvent = qEvent;
        this.hasParted = true;
    }

    /**
     * Method that executes upon thread start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        // We've received a direct !seen command
        if(!hasParted) {
            searchUser();
            return;
        }
        // We've received a notification that a user has left a channel
        else {
            if(pEvent != null) {
                updateSeen(pEvent.getUser().getNick(), pEvent.getChannel().getName());
                return;
            } else if(kEvent != null) {
                updateSeen(kEvent.getRecipient().getNick(), kEvent.getChannel().getName());
                return;
            } else if(qEvent != null) {
                for(Channel c : qEvent.getUser().getChannels()) {
                    updateSeen(qEvent.getUser().getNick(), c.getName());
                }
                return;
            }
            return;
        }
    }

    /**
     * Searches the database for a user
     */
    private void searchUser() {
        // Extract the user name from the command and remove any unnecessary whitespace
        try {
            seenUser = event.getMessage().substring(6).replaceAll("^\\s+", "").replaceAll("\\s+$", "");
        } catch (IndexOutOfBoundsException ex) {
            return;
        }
        // The user is performing the command on themselves?
        if(seenUser.equals(event.getUser().getNick())) {
            event.respond("What are you doing?");
            return;
            // The user is performing the command on the bot?
        } else if (seenUser.equals(event.getBot().getNick())) {
            event.respond("I don't think that command means what you think it means.");
            return;
            // Make sure the user isn't in the channel, if they are then just return that they are
        } else if (event.getBot().getUsers(event.getChannel()).contains(event.getBot().getUser(seenUser))) {
            event.respond(seenUser + " is currently in the channel.");
            return;
            // Make sure we don't have a blank request
        } else if(seenUser.equals("")) {
            event.respond("I can't see when a user was last here if you don't give me one!");
            return;
            // If all else fails, we have a valid request
        } else {
            // Create a new instance of the database
            database = new Database();
            try {
                // Connect to the database and execute our select query
                database.connect();
                PreparedStatement statement = database.getPreparedStatement();
                statement = database.getConnection().prepareStatement("SELECT Date FROM Seen WHERE Nick = ? AND Channel = ?");
                statement.setString(1, seenUser);
                statement.setString(2, event.getChannel().getName());
                ResultSet resultSet = statement.executeQuery();
                // Respond appropriately should our user exist/not exist in the database
                if(resultSet.next()) {
                    event.respond(seenUser + " was last seen about " + IRCUtils.toReadableTime(resultSet.getTimestamp("Date"), false, true) + " ago.");
                } else {
                    event.respond("I haven't seen " + seenUser + ".");
                }
                // Disconnect from the database
                database.disconnect();
            } catch (Exception ex) {
                Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            }
        }
    }

    /**
     * Updates the database upon user leaving a channel or disconnecting from the network
     * @param userToUpdate the user that left/disconnected
     * @param channelToUpdate the channel we saw the user disconnect in
     */
    private void updateSeen(String userToUpdate, String channelToUpdate) {
        // Create a new instance of the database
        database = new Database();
        try {
            // Connect to the database and execute our select query to see whether to insert or update
            database.connect();
            PreparedStatement statement = database.getPreparedStatement();
            statement = database.getConnection().prepareStatement("SELECT Date FROM Seen WHERE Nick = ? AND Channel = ?");
            statement.setString(1, userToUpdate);
            statement.setString(2, channelToUpdate);
            ResultSet resultSet = statement.executeQuery();
            // If a record exists, then run another query to update the date appropriately
            if(resultSet.next()) {
                // Close the previous statement if it isn't closed already
                if(!statement.isClosed()) statement.close();
                statement = database.getConnection().prepareStatement("UPDATE Seen SET Date = ? WHERE Nick = ? AND Channel = ?");
                statement.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
                statement.setString(2, userToUpdate);
                statement.setString(3, channelToUpdate);
                statement.executeUpdate();
            }
            // Otherwise, create a new record in the database for the user
            else {
                // Close the previous statement if it isn't closed already
                if(!statement.isClosed()) statement.close();
                statement = database.getConnection().prepareStatement("INSERT INTO Seen(Nick, Date, Channel) VALUES (?, ?, ?)");
                statement.setString(1, userToUpdate);
                statement.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
                statement.setString(3, channelToUpdate);
                statement.executeUpdate();
            }
            // Disconnect from the database
            database.disconnect();
        } catch (Exception ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
        }
    }
}
