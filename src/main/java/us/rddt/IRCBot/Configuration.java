package us.rddt.IRCBot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.pircbotx.PircBotX;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import us.rddt.IRCBot.Implementations.DatabaseCleaner;
import us.rddt.IRCBot.Implementations.RedditWatcher;
import us.rddt.IRCBot.Implementations.YouTubeWatcher;
import us.rddt.IRCBot.Logging.IRCLogger;

/**
 * Class which maintains the bot's configuration settings. Settings are loaded upon
 * bot startup and can be reloaded on the fly via a command provided by a bot
 * administrator.
 * 
 * @author Ryan Morrison
 */
public class Configuration {
	/*
	 * Version information
	 */
	public static final int VERSION_MAJOR = 1;
	public static final int VERSION_MINOR = 3;
	public static final int VERSION_REVISION = 0;
	
    /*
     * Class variables.
     */
    private static String nick;
    private static String user;
    private static String server;
    private static int port;
    private static String password;
    private static boolean use_ssl;
    private static boolean ssl_verify;
    private static String[] channels;

    private static char command_prefix;

    private static String channel_announcement;
    private static String[] channel_participating;

    private static List<String> disabled_functions;

    private static String main_channel;

    private static String[] watchSubreddits;
    private static String[] watchYouTubers;

    private static int votekickDuration;
    private static int votekickPassPercent;

    private static String admin_nick;
    private static String admin_hostmask;

    private static String database_driver;

    private static String mysql_server;
    private static String mysql_user;
    private static String mysql_password;
    private static String mysql_database;

    private static String sqlite_database;

    private static ScheduledExecutorService watchScheduler;
    private static ScheduledExecutorService cleanupScheduler;
    private static ScheduledExecutorService youtubeScheduler;

    private static IRCLogger logger;
    private static String log_output;

    private static String user_agent;

    private static String steam_api_key;

    private static Twitter twitter_instance;
    private static String twitter_access_token;
    private static String twitter_access_secret;
    private static String twitter_consumer_key;
    private static String twitter_consumer_secret;

    /**
     * Loads the configuration provided via a properties file
     * @throws FileNotFoundException if the properties file does not exist
     * @throws IOException if an exception is raised reading the properties file
     */
    public static void loadConfiguration() throws FileNotFoundException, IOException {
        Properties config = new Properties();
        config.load(new FileInputStream("IRCBot.properties"));
        nick = config.getProperty("nick");
        user = config.getProperty("user");
        server = config.getProperty("server");
        port = Integer.parseInt(config.getProperty("port"));
        password = config.getProperty("password");
        use_ssl = Boolean.parseBoolean(config.getProperty("use_ssl"));
        ssl_verify = Boolean.parseBoolean(config.getProperty("ssl_verify"));
        channels = config.getProperty("channels").split(",");
        command_prefix = config.getProperty("command_prefix").charAt(0);
        channel_announcement = config.getProperty("channel_announcement");
        channel_participating = config.getProperty("channel_participating").split(",");
        disabled_functions = new ArrayList<String>(Arrays.asList(config.getProperty("disabled_functions").split(",")));
        main_channel = config.getProperty("main_channel");
        watchSubreddits = config.getProperty("watch_subreddits").split(",");
        watchYouTubers = config.getProperty("watch_youtubers").split(",");
        votekickDuration = Integer.parseInt(config.getProperty("votekick_duration"));
        votekickPassPercent = Integer.parseInt(config.getProperty("votekick_pass_percent"));
        admin_nick = config.getProperty("admin_nick");
        admin_hostmask = config.getProperty("admin_hostmask");
        database_driver = config.getProperty("database_driver");
        if(database_driver.equalsIgnoreCase("mysql")) {
            mysql_server = config.getProperty("mysql_server");
            mysql_user = config.getProperty("mysql_user");
            mysql_password = config.getProperty("mysql_password");
            mysql_database = config.getProperty("mysql_database");
        } else if(database_driver.equalsIgnoreCase("sqlite")) {
            sqlite_database = config.getProperty("sqlite_database");
        }
        log_output = config.getProperty("log_output");
        user_agent = config.getProperty("user_agent");
        steam_api_key = config.getProperty("steam_api_key");
        twitter_access_token = config.getProperty("twitter_access_token");
        twitter_access_secret = config.getProperty("twitter_access_secret");
        twitter_consumer_key = config.getProperty("twitter_consumer_key");
        twitter_consumer_secret = config.getProperty("twitter_consumer_secret");
    }

    /**
     * Starts the scheduler(s) to monitor subreddits and handle database cleanup
     * @param bot the IRC bot
     */
    public static void startScheduler(PircBotX bot) {
        if(watchSubreddits.length > 0 && !watchSubreddits[0].equals("") && !disabled_functions.contains("watcher_reddit")) {
            if(watchScheduler != null) {
                Configuration.getLogger().write(Level.INFO, "Shutting down existing subreddit updates");
                watchScheduler.shutdownNow();
            }
            watchScheduler = Executors.newScheduledThreadPool(watchSubreddits.length + 1);
            for(int i = 0; i < watchSubreddits.length; i++) {
                String[] configuration = watchSubreddits[i].split(":");
                String subreddit = configuration[0];
                int frequency = Integer.parseInt(configuration[1]);
                Configuration.getLogger().write(Level.INFO, "Scheduling subreddit updates for r/" + subreddit + " starting in " + (5 * i) + " minutes (frequency: " + frequency + " minutes)");
                watchScheduler.scheduleWithFixedDelay(new RedditWatcher(bot, subreddit), (5 * i), frequency, TimeUnit.MINUTES);
            }
        }
        if(!disabled_functions.contains("dbcleanup")) {
            if(cleanupScheduler != null) {
                cleanupScheduler.shutdownNow();
            }
            cleanupScheduler = Executors.newScheduledThreadPool(1);
            cleanupScheduler.scheduleWithFixedDelay(new DatabaseCleaner(), 1, 12, TimeUnit.HOURS);
        }
        if(watchYouTubers.length > 0 && !watchYouTubers[0].equals("") && !disabled_functions.contains("watcher_youtube")) {
            if(youtubeScheduler != null) {
                Configuration.getLogger().write(Level.INFO, "Shutting down existing YouTube updates");
                youtubeScheduler.shutdownNow();
            }
            youtubeScheduler = Executors.newScheduledThreadPool(watchYouTubers.length + 1);
            for(int i = 0; i < watchYouTubers.length; i++) {
                Configuration.getLogger().write(Level.INFO, "Scheduling YouTube updates for user " + watchYouTubers[i] + " starting in " + (2 * i) + " minutes.");
                youtubeScheduler.scheduleWithFixedDelay(new YouTubeWatcher(bot, watchYouTubers[i]), (2 * i), 5, TimeUnit.MINUTES);
            }
        }
    }

    /**
     * Returns the application's version string from the manifest.
     * @return the application version string
     */
    public static String getApplicationVersion() {
        return VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_REVISION;
    }

    /**
     * Returns a new or existing Twitter instance.
     * @return a Twitter instance
     */
    public static Twitter getTwitterInstance() {
        if(twitter_instance != null) return twitter_instance;
        else {
            if(getTwitterConsumerKey() != null && getTwitterConsumerSecret() != null && getTwitterAccessToken() != null && getTwitterAccessSecret() != null) {
                getLogger().write(Level.INFO, "Twitter credentials provided, making an authentication attempt.");
                ConfigurationBuilder cb = new ConfigurationBuilder();
                cb.setDebugEnabled(true)
                .setOAuthConsumerKey(Configuration.getTwitterConsumerKey())
                .setOAuthConsumerSecret(Configuration.getTwitterConsumerSecret())
                .setOAuthAccessToken(Configuration.getTwitterAccessToken())
                .setOAuthAccessTokenSecret(Configuration.getTwitterAccessSecret());
                twitter_instance = new TwitterFactory(cb.build()).getInstance();
            } else {
                getLogger().write(Level.INFO, "Twitter credentials not provided, skipping authentication.");
                twitter_instance = TwitterFactory.getSingleton();
            }
            return twitter_instance;
        }
    }

    /**
     * Returns the bot's nickname
     * @return the bot's nickname
     */
    public static String getNick() {
        return nick;
    }

    /**
     * Returns the bot's username
     * @return the bot's username
     */
    public static String getUser() {
        return user;
    }

    /**
     * Returns the server to connect to
     * @return the server to connect to
     */
    public static String getServer() {
        return server;
    }

    /**
     * Returns the server's port
     * @return the server's port
     */
    public static int getPort() {
        return port;
    }

    /**
     * Returns the server's password
     * @return the server's password
     */
    public static String getPassword() {
        return password;
    }

    /**
     * Returns the channels to join
     * @return the channels to join
     */
    public static String[] getChannels() {
        return channels;
    }

    /**
     * Returns the command prefix
     * @return the command prefix
     */
    public static char getCommandPrefix() {
        return command_prefix;
    }

    /**
     * Returns the channel announcement
     * @return the channel announcement
     */
    public static String getChannelAnnouncement() {
        return channel_announcement;
    }

    /**
     * Returns the list of participating channels to send announcements in
     * @return the list of participating channels to send announcements in
     */
    public static String[] getChannelsParticipating() {
        return channel_participating;
    }

    /**
     * Returns the list of functions to disable
     * @return the list of functions to disable
     */
    public static List<String> getDisabledFunctions() {
        return disabled_functions;
    }

    /**
     * Return the main channel
     * @return the main channel
     */
    public static String getMainChannel() {
        return main_channel;
    }

    /**
     * Returns the subreddits to watch
     * @return the subreddits to watch
     */
    public static String[] getWatchSubreddits() {
        return watchSubreddits;
    }

    /**
     * Returns the duration of votekicks in seconds
     * @return the duration of votekicks in seconds
     */
    public static int getVotekickDuration() {
        return votekickDuration;
    }

    /**
     * Returns the percentage required for a votekick to pass
     * @return the percentage required for a votekick to pass
     */
    public static int getVotekickPassPercent() {
        return votekickPassPercent;
    }
    /**
     * Returns the nick of the administrator
     * @return the nick of the administrator
     */
    public static String getAdminNick() {
        return admin_nick;
    }

    /**
     * Returns the hostmask of the administrator
     * @return the hostmask of the administrator
     */
    public static String getAdminHostmask() {
        return admin_hostmask;
    }

    /**
     * Returns the database driver to use
     * @return the database driver to use
     */
    public static String getDatabaseDriver() {
        return database_driver;
    }

    /**
     * Returns the MySQL server
     * @return the MySQL server
     */
    public static String getMySQLServer() {
        return mysql_server;
    }

    /**
     * Returns the MySQL user
     * @return the MySQL user
     */
    public static String getMySQLUser() {
        return mysql_user;
    }

    /**
     * Returns the MySQL password
     * @return the MySQL password
     */
    public static String getMySQLPassword() {
        return mysql_password;
    }

    /**
     * Returns the MySQL database
     * @return the MySQL database
     */
    public static String getMySQLDatabase() {
        return mysql_database;
    }

    /**
     * Returns the SQLite database
     * @return the SQLite database
     */
    public static String getSQLiteDatabase() {
        return sqlite_database;
    }

    /**
     * Returns the logger for use if it exists, otherwise initialize and return a new one
     * @return the logger to use
     */
    public static IRCLogger getLogger() {
        if(logger == null) {
            logger = new IRCLogger();
            try {
                logger.setup();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return logger;
    }

    public static String getUserAgent() {
        if(user_agent != null && !user_agent.isEmpty()) {
            return user_agent;
        }
        return "Mozilla/5.0 (Windows NT 6.1; rv:6.0) Gecko/20110814 Firefox/6.0";
    }

    /**
     * Returns the log file to output the HTML-formatted log to
     * @return the log file to output the HTML-formatted log to
     */
    public static String getLogFile() {
        return log_output;
    }

    /**
     * Returns the Steam API key
     * @return the Steam API key
     */
    public static String getSteamAPIKey() {
        return steam_api_key;
    }

    /**
     * Returns the access token for Twitter
     * @return the access token for Twitter
     */
    public static String getTwitterAccessToken() {
        return twitter_access_token;
    }

    /**
     * Returns the access secret for Twitter
     * @return the access secret for Twitter
     */
    public static String getTwitterAccessSecret() {
        return twitter_access_secret;
    }

    /**
     * Returns the consumer key for Twitter
     * @return the consumer key for Twitter
     */
    public static String getTwitterConsumerKey() {
        return twitter_consumer_key;
    }

    /**
     * Returns the consumer secret for Twitter
     * @return the consumer secret for Twitter
     */
    public static String getTwitterConsumerSecret() {
        return twitter_consumer_secret;
    }

    /**
     * Returns if the connection should be secured through SSL
     * @return true if SSL should be used, false for unsecured connections
     */
    public static boolean isSSL() {
        return use_ssl;
    }

    /**
     * Returns if the SSL connection should verify certificates
     * @return true for SSL certificate verification, false to trust all certificates
     */
    public static boolean isSSLVerified() {
        return ssl_verify;
    }
}
