package us.rddt.IRCBot;

import java.util.logging.Level;

import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.ListenerAdapter;

import us.rddt.IRCBot.Statistics.StatisticsUpdater;
import us.rddt.IRCBot.Streaming.TwitterMentions;

/**
 * The entry point for IRCBot. IRCBot is a custom IRC bot written in Java primarily
 * to support the RDDT IRC Network.
 * 
 * @author Ryan Morrison
 */
public class IRCBot extends ListenerAdapter<PircBotX> {
    /*
     * Class variables
     */
    private static PircBotX bot;

    /**
     * The main entry point of the application
     * @param args arguments passed through the command line
     */
    public static void main(String[] args) throws Exception {
        try {
            Configuration.loadConfiguration();
        } catch(Exception ex) {
            Configuration.getLogger().write(Level.SEVERE, IRCUtils.getStackTraceString(ex));
            System.exit(-1);
        }
        Configuration.getLogger().write(Level.INFO, "Initializing bot (IRCBot version " + Configuration.getApplicationVersion() + ")");
        
        // Create a new instance of the IRC bot
        bot = new PircBotX();
        // Add new listeners for the actions we want the bot to handle
        bot.getListenerManager().addListener(new IRCBotHandlers());
        // Set the bot's nick
        bot.setName(Configuration.getNick());
        // Set the bot's user
        bot.setLogin(Configuration.getUser());
        // Automatically split messages longer than IRC's size limit
        bot.setAutoSplitMessage(true);
        // Connect to the IRC server
        connect(bot, args);
        // Create the scheduler
        Configuration.startScheduler(bot);
        // Create the scheduler for statistics updating
        if(!Configuration.getDisabledFunctions().contains("tweetstatistics")) {
            StatisticsUpdater.schedule();
        }
        // Create the scheduler for tweet mention streaming
        if(!Configuration.getDisabledFunctions().contains("tweetmentions")) {
            TwitterMentions.listenForTweets(bot);
        }
        // Add a shutdown handler to attempt to properly disconnect from the server upon shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                if(bot.isConnected()) bot.quitServer("Received SIGINT from command line");
            }
        }));
    }

    /**
     * Connects to the IRC server
     * @param bot the IRC bot
     * @param channels the channels to join (ignores the bot's configuration)
     */
    private static void connect(PircBotX bot, String[] channels) {
        // Attempt to connect to the server and join the required channel(s)
        Configuration.getLogger().write(Level.INFO, "Connecting to " + Configuration.getServer() + " and joining channel(s).");
        try {
            if(Configuration.isSSL() && Configuration.isSSLVerified()) {
                bot.connect(Configuration.getServer(), Configuration.getPort(), Configuration.getPassword(), new UtilSSLSocketFactory());
            } else if (Configuration.isSSL() && !Configuration.isSSLVerified()) {
                bot.connect(Configuration.getServer(), Configuration.getPort(), Configuration.getPassword(), new UtilSSLSocketFactory().trustAllCertificates());
            } else {
                bot.connect(Configuration.getServer(), Configuration.getPort(), Configuration.getPassword());
            }
        } catch (NickAlreadyInUseException ex) {
            Configuration.getLogger().write(Level.WARNING, ex.getMessage());
            bot.setName(bot.getNick() + "_");
        } catch (Exception ex) {
            Configuration.getLogger().write(Level.SEVERE, ex.getMessage());
            bot.disconnect();
            System.exit(-1);
        }
        bot.sendRawLine("MODE " + bot.getNick() + " +B");
        if(channels.length > 0) {
            joinChannels(channels, bot);
        } else {
            joinChannels(Configuration.getChannels(), bot);
        }
    }

    /**
     * Joins channels as defined in the bot's configuration
     * @param channels the channels to join
     * @param bot the IRC bot
     */
    private static void joinChannels(String[] channels, PircBotX bot) {
        for (int i = 0; i < channels.length; i++) {
            bot.joinChannel(channels[i]);
        }
    }
}