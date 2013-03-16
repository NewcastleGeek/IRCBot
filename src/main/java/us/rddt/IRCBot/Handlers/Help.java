package us.rddt.IRCBot.Handlers;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import us.rddt.IRCBot.Configuration;

public class Help implements Runnable {
    /*
     * Class variables
     */
    private PrivateMessageEvent<PircBotX> event;
    
    /**
     * Class constructor
     * @param event the MessageEvent that triggered this class
     */
    public Help(PrivateMessageEvent<PircBotX> event) {
        this.event = event;
    }
    
    /**
     * Prints help about a specific command.
     * @param command the command to display help for
     */
    private void printCommandHelp(String command) {
        if(command.equalsIgnoreCase("calc")) {
            event.respond("calc - A basic calculator. Returns the result of a provided expression.");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "calc [expression]");
        }
        else if(command.equalsIgnoreCase("convert")) {
            event.respond("convert - performs conversion functions using the Google Calculator API.");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "convert [value to convert] [original type] to [type to convert]");
        }
        else if(command.equalsIgnoreCase("decide")) {
            event.respond("decide - randomly selects a decision based on user input.");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "decide [first choice] or [second choice]");
            event.respond(" -- or -- ");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "decide [choice] (returns a yes/no answer)");
            event.respond("Note: you may provide as many arguments as you like separated by 'or' to select from those choices.");
        }
        else if(command.equalsIgnoreCase("g")) {
            event.respond("g - performs a Google search using the provided query string.");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "g [query]");
        }
        else if(command.equalsIgnoreCase("seen")) {
            event.respond("seen - returns the last time a user was seen in a channel.");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "seen [nickname]");
        }
        else if(command.equalsIgnoreCase("status")) {
            event.respond("status - updates or displays the currently played game of a user.");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "status [argument]");
            event.respond("  where argument is one of the following:");
            event.respond("    set [game] (sets your current status to a provided game)");
            event.respond("    reset (resets your current status to not playing)");
            event.respond("    user [user] (gets the status of a given nickname)");
            event.respond("    game [game] (gets the users currently playing a provided game)");
            event.respond("    all (gets all users playing any game)");
        }
        else if(command.equalsIgnoreCase("steam")) {
            event.respond("steam - gets the status of a Steam user.");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "steam [numerical ID or community ID]");
        }
        else if(command.equalsIgnoreCase("ud")) {
            event.respond("ud - searches Urban Dictionary for a given query string.");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "ud [query]");
        }
        else if(command.equalsIgnoreCase("who")) {
            event.respond("who - shout management.");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "who [argument]");
            event.respond("  where argument is one of the following:");
            event.respond("    [shout] (returns the user who shouted the provided quote)");
            event.respond("    last (returns the last quote and who shouted it)");
            event.respond("    list (returns statistics about all quotes in the database)");
            event.respond("    user (returns statistics about a user's quotes in the database)");
            event.respond("    top10 (returns the top 10 most active shouters in a channel)");
            event.respond("    delete [--purge] [quote] (channel operators only - deletes a provided quote from the database. This command will permanently purge a quote if the --purge argument is provided.)");
            event.respond("    undelete [quote] (channel operators only - undeletes a provided quote from the database.)");
        }
        else if(command.equalsIgnoreCase("votekick")) {
            event.respond("votekick - Channel votekicking.");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "votekick [nickname]");
            event.respond("If a votekick has not been started, this command will start a votekick against the user. Otherwise, this command will add a vote to an already started votekick.");
            event.respond("If a votekick receives the number of votes equal or greater to " + Configuration.getVotekickPassPercent() + "% of the number of users in a channel within " + Configuration.getVotekickDuration() + " seconds, the user will be kicked from the channel.");
            event.respond("If a votekick reaches a duration of " + Configuration.getVotekickDuration() + " seconds, the votekick will expire and end.");
        }
        else if(command.equalsIgnoreCase("appendtopic")) {
            event.respond("appendtopic - appends a string to the end of a channel's current topic. (Channel operators only)");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "appendtopic [string]");
        }
        else if(command.equalsIgnoreCase("disablestatistics")) {
            event.respond("disablestatistics - disables statistics on a channel. (Channel operators only)");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "disablestatistics");
        }
        else if(command.equalsIgnoreCase("enablestatistics")) {
            event.respond("enablestatistics - enables statistics on a channel. (Channel operators only)");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "enablestatistics");
        }
        else if(command.equalsIgnoreCase("removetopic")) {
            event.respond("removetopic - searches for and removes a string from a channel's current topic. (Channel operators only)");
            event.respond("Usage: " + Configuration.getCommandPrefix() + "removetopic [string]");
        }
    }
    
    /**
     * Prints the main help text including a list of commands.
     */
    private void printMainHelp() {
        event.respond("Hi, I'm " + event.getBot().getNick() + "! I'm here to provide conveniences to IRC channels.");
        event.respond("For help with a specific command, message me with 'help [command]'.");
        event.respond(" -- List of Commands --");
        event.respond("calc, convert, decide, g, seen, status, steam, ud, who, votekick");
        event.respond(" -- List of Operator Commands --");
        event.respond("appendtopic, disablestatistics, enablestatistics, removetopic");
    }
    
    /**
     * Method that executes upon thread-start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        String[] args = event.getMessage().trim().split(" ");
        if(args.length == 1) printMainHelp();
        else printCommandHelp(args[1]);
    }
}
