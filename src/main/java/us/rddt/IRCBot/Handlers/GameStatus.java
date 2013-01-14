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

package us.rddt.IRCBot.Handlers;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.Database;
import us.rddt.IRCBot.IRCUtils;
import us.rddt.IRCBot.Enums.GameStatusModes;

/**
 * Allows users to set games that they are currently playing, and allows other
 * users to determine who is playing what game or who is playing a provided game.
 * 
 * @author Ryan Morrison
 */
public class GameStatus implements Runnable {
    // Variables
    private MessageEvent<PircBotX> event;
    private GameStatusModes mode;
    private Database database;
    private HashMap<String, String> gamesMap = new HashMap<String, String>();

    /**
     * Class constructor
     * @param event the MessageEvent that triggered this class
     */
    public GameStatus(MessageEvent<PircBotX> event, GameStatusModes mode) {
        this.event = event;
        this.mode = mode;
    }
    
    /**
     * Adds a game to the database of known games
     * @param gameId the shorthand identifier of the game
     * @param game the full name of the game
     * @throws ClassNotFoundException if the database class cannot be found
     * @throws SQLException if the SQL query fails
     * @throws IOException if reading from the ResultSet fails
     */
    private void addGame(String gameId, String game) throws ClassNotFoundException, SQLException, IOException {
        // Connect to the database
        database = new Database();
        database.connect();
        
        // Prepare and execute the SQL query to insert
        PreparedStatement statement = database.getConnection().prepareStatement("INSERT INTO GameList(GameID, GameName) VALUES (?, ?)");
        statement.setString(1, gameId);
        statement.setString(2, game);
        statement.executeUpdate();
        
        // Disconnect from the database
        database.disconnect();
        
        // Add the game to the HashMap for easy access
        gamesMap.put(gameId, game);
    }
    
    /**
     * Deletes a game from the database of known games
     * @param gameId the shorthand identifier of the game
     * @throws ClassNotFoundException if the database class cannot be found
     * @throws SQLException if the SQL query fails
     * @throws IOException if reading from the ResultSet fails
     */
    private void deleteGame(String gameId) throws ClassNotFoundException, SQLException, IOException {
        // Connect to the database
        database = new Database();
        database.connect();
        
        // Prepare and execute the SQL query to insert
        PreparedStatement statement = database.getConnection().prepareStatement("DELETE FROM GameList WHERE GameID = ?");
        statement.setString(1, gameId);
        statement.executeUpdate();
        
        // Disconnect from the database
        database.disconnect();
        
        // Remove the game from the HashMap
        gamesMap.remove(gameId);
    }

    /**
     * Returns the users who have statuses set
     * @throws ClassNotFoundException if the database class cannot be found
     * @throws SQLException if the SQL query fails
     * @throws IOException if reading from the ResultSet fails
     */
    private void getAllStatus() throws ClassNotFoundException, SQLException, IOException {
        // Boolean value to determine if results were returned or not
        boolean emptyRows = true;
        
        // Connect to the database
        database = new Database();
        database.connect();
        // Prepare the StringBuilder to hold the list of nicks playing
        StringBuilder builder = new StringBuilder();

        // Prepare and execute the SQL query
        PreparedStatement statement = database.getConnection().prepareStatement("SELECT * FROM GameStatus");
        ResultSet resultSet = statement.executeQuery();

        // If a result was returned, tell the channel what the user is playing
        // Otherwise, they aren't playing anything
        String prefix = "";
        while(resultSet.next()) {
            builder.append(prefix);
            prefix = ", ";
            builder.append(resultSet.getString("Nick") + " playing " + resultSet.getString("Game") + " (" + IRCUtils.toReadableTime(resultSet.getTimestamp("Date"), false, false) + ")");
            emptyRows = false;
        }

        // Disconnect from the database
        database.disconnect();

        /*
         * JDBC does not provide a clear method of determining whether a ResultSet actually has any rows.
         * We have to use a boolean to work out whether it actually returned anything.
         */
        if(emptyRows) builder.append("Nobody is playing any games.");

        // Return the result
        event.getBot().sendMessage(event.getChannel(), builder.toString());
    }

    /**
     * Returns the users playing the provided game
     * @param game the game to retrieve the status of
     * @throws ClassNotFoundException if the database class cannot be found
     * @throws SQLException if the SQL query fails
     * @throws IOException if reading from the ResultSet fails
     */
    private void getGameStatus(String game) throws ClassNotFoundException, SQLException, IOException {
        // Check to see if the game exists in the HashMap and if so update accordingly
        if(gamesMap.containsKey(game)) {
            // Get the game's full title
            game = gamesMap.get(game);
            // Boolean value to determine if results were returned or not
            boolean emptyRows = true;

            // Connect to the database
            database = new Database();
            database.connect();
            // Prepare the StringBuilder to hold the list of nicks playing
            StringBuilder builder = new StringBuilder();

            // Prepare and execute the SQL query
            PreparedStatement statement = database.getConnection().prepareStatement("SELECT * FROM GameStatus WHERE Game = ?");
            statement.setString(1, game);
            ResultSet resultSet = statement.executeQuery();

            builder.append("Users playing " + game + ": ");

            // If a result was returned, tell the channel what the user is playing
            // Otherwise, they aren't playing anything
            String prefix = "";
            while(resultSet.next()) {
                builder.append(prefix);
                prefix = ", ";
                builder.append(resultSet.getString("Nick") + " (" + IRCUtils.toReadableTime(resultSet.getTimestamp("Date"), false, false) + ")");
                emptyRows = false;
            }

            // Disconnect from the database
            database.disconnect();

            /*
             * JDBC does not provide a clear method of determining whether a ResultSet actually has any rows.
             * We have to use a boolean to work out whether it actually returned anything.
             */
            if(emptyRows) builder.append("nobody");

            // Return the result
            event.getBot().sendMessage(event.getChannel(), builder.toString());
        } else {
            // The game's full title isn't in the HashMap
            throw new IllegalArgumentException("Game does not exist");
        }
    }

    /**
     * Returns the status of a given user to the channel
     * @param nick the nick to retrieve the status of
     * @throws ClassNotFoundException if the database class cannot be found
     * @throws SQLException if the SQL query fails
     * @throws IOException if reading from the ResultSet fails
     */
    private void getUserStatus(String nick) throws ClassNotFoundException, SQLException, IOException {
        // Connect to the database
        database = new Database();
        database.connect();

        // Prepare and execute the SQL query
        PreparedStatement statement = database.getConnection().prepareStatement("SELECT * FROM GameStatus WHERE Nick = ?");
        statement.setString(1, nick);
        ResultSet resultSet = statement.executeQuery();

        // If a result was returned, tell the channel what the user is playing
        // Otherwise, they aren't playing anything
        if(resultSet.next()) {
            event.getBot().sendMessage(event.getChannel(), nick + " is playing " + resultSet.getString("Game") + " (" + IRCUtils.toReadableTime(resultSet.getTimestamp("Date"), false, false) + ")");
        } else {
            event.getBot().sendMessage(event.getChannel(), nick + " is not playing anything!");
        }

        // Disconnect from the database
        database.disconnect();
    }

    /**
     * Loads full game titles from a database into a HashMap for easy access
     * @throws ClassNotFoundException if the database class cannot be found
     * @throws SQLException if the SQL query fails
     * @throws IOException if reading from the ResultSet fails
     */
    private void loadGameTitles() throws ClassNotFoundException, SQLException, IOException {
        // Connect to the database
        database = new Database();
        database.connect();

        // Prepare and execute the SQL query
        PreparedStatement statement = database.getConnection().prepareStatement("SELECT * FROM GameList");
        ResultSet resultSet = statement.executeQuery();
        
        // Load all the games from the database into the HashMap
        while(resultSet.next()) {
            gamesMap.put(resultSet.getString("GameID"), resultSet.getString("GameName"));
        }
        
        // Disconnect from the database
        database.disconnect();
    }

    /**
     * Resets the given user's status (deletes the database entry)
     * @param nick the nick to retrieve the status of
     * @throws ClassNotFoundException if the database class cannot be found
     * @throws SQLException if the SQL query fails
     * @throws IOException if reading from the ResultSet fails
     */
    private void resetUserStatus(String nick) throws ClassNotFoundException, SQLException, IOException {
        // Connect to the database
        database = new Database();
        database.connect();

        // Prepare and execute the query to delete any entry
        PreparedStatement statement = database.getConnection().prepareStatement("DELETE FROM GameStatus WHERE Nick = ?");
        statement.setString(1, nick);
        statement.executeUpdate();

        // Disconnect from the database
        database.disconnect();
    }

    /**
     * Sets or updates the given user's status
     * @param nick the nick to update the status for
     * @param game the shortened game string
     * @throws ClassNotFoundException if the database class cannot be found
     * @throws SQLException if the SQL query fails
     * @throws IOException if reading from the ResultSet fails
     * @throws IllegalArgumentException if the game's full string doesn't exist in the HashMap
     */
    private void setUserStatus(String nick, String game) throws ClassNotFoundException, SQLException, IOException, IllegalArgumentException {
        // Prepare the database object
        database = new Database();

        // Check to see if the game exists in the HashMap and if so update accordingly
        if(gamesMap.containsKey(game)) {
            // Get the game's full title
            game = gamesMap.get(game);

            // Connect to the database
            database.connect();
            // Prepare the query to check if an entry already exists and execute it
            PreparedStatement statement = database.getConnection().prepareStatement("SELECT * FROM GameStatus WHERE Nick = ?");
            statement.setString(1, nick);
            ResultSet resultSet = statement.executeQuery();
            // If there is already a game, update it instead of creating a brand new entry
            if(resultSet.next()) {
                // Close the previous statement if it isn't closed already
                if(!statement.isClosed()) statement.close();
                // Prepare and execute the SQL query to update
                statement = database.getConnection().prepareStatement("UPDATE GameStatus SET Game = ?, Date = ? WHERE Nick = ?");
                statement.setString(1, game);
                statement.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
                statement.setString(3, nick);
                statement.executeUpdate();
            } else {
                // Close the previous statement if it isn't closed already
                if(!statement.isClosed()) statement.close();
                // Prepare and execute the SQL query to insert
                statement = database.getConnection().prepareStatement("INSERT INTO GameStatus(Nick, Date, Game) VALUES (?, ?, ?)");
                statement.setString(1, nick);
                statement.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
                statement.setString(3, game);
                statement.executeUpdate();
            }

            // Disconnect from the database
            database.disconnect();
        } else {
            // The game's full title isn't in the HashMap
            throw new IllegalArgumentException("Game does not exist");
        }
    }

    /**
     * Method that executes upon thread start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        // Load the list of game titles into the HashMap
        // If they cannot be loaded, just return
        try {
            this.loadGameTitles();
        } catch (Exception ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            return;
        }
        
        try {
            if(mode == GameStatusModes.SET) {
                // Split the command into parameters
                String parameters[] = event.getMessage().split(" ");
                // Ensure the user has provided enough parameters for the command
                if(parameters.length > 2) {
                    setUserStatus(event.getUser().getNick(), parameters[2]);
                    event.respond("Done!");
                }
                else {
                    event.respond("You must provide a game to play!");
                }
            } else if(mode == GameStatusModes.RESET) {
                // Reset the user's status
                resetUserStatus(event.getUser().getNick());
                event.respond("Done!");
            } else if(mode == GameStatusModes.ALL) {
                // Return all statuses
                getAllStatus();
            } else if(mode == GameStatusModes.USER) {
                // Split the command into parameters
                String parameters[] = event.getMessage().split(" ");
                // Ensure the user has provided enough parameters for the command
                if(parameters.length > 2) {
                    getUserStatus(parameters[2]);
                } else {
                    event.respond("You must provide a user to retrieve status for!");
                }
            } else if(mode == GameStatusModes.GAME) {
                // Split the command into parameters
                String parameters[] = event.getMessage().split(" ");
                // Ensure the user has provided enough parameters for the command
                if(parameters.length > 2) {
                    getGameStatus(parameters[2]);
                } else {
                    event.respond("You must provide a game to retrieve status for!");
                }
            } else if(mode == GameStatusModes.ADD) {
                // Split the command into parameters
                String parameters[] = event.getMessage().split(" ");
                // Ensure the user has provided enough parameters for the command
                if(parameters.length > 3) {
                    // Ensure the user is not adding a duplicate game
                    if(!gamesMap.containsKey(parameters[2])) {
                        // The rest of the parameters is the full name of the game, so combine them into a string
                        StringBuilder gameName = new StringBuilder();
                        for(int i = 3; i < parameters.length; i++) {
                            gameName.append(parameters[i] + " ");
                        }
                        // Add the game
                        addGame(parameters[2], gameName.toString().trim());
                        event.respond("Done!");
                    } else {
                        event.respond("Game \"" + parameters[2] + "\" already exists!");
                    }
                } else {
                    event.respond("You must provide a shorthand identifier and the full game name to add!");
                }
            } else if(mode == GameStatusModes.DELETE) {
                // Split the command into parameters
                String parameters[] = event.getMessage().split(" ");
                // Ensure the user has provided enough parameters for the command
                if(parameters.length > 2) {
                    deleteGame(parameters[2]);
                    event.respond("Done!");
                } else {
                    event.respond("You must provide a game to delete!");
                }
            }
        } catch (Exception ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            event.respond("Unable to get status - " + ex.getMessage());
        }
    }
}
