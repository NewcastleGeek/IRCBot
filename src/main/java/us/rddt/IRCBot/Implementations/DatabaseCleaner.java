package us.rddt.IRCBot.Implementations;

import java.sql.PreparedStatement;
import java.util.logging.Level;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.Database;
import us.rddt.IRCBot.IRCUtils;

/**
 * Cleans the database, removing old and unnecessary entries after a specified period of time.
 * @author Ryan Morrison
 */
public class DatabaseCleaner implements Runnable {
    public void run() {
        // Variables
        Database database;
        PreparedStatement statement;
        int status;
        
        Configuration.getLogger().write(Level.INFO, "Database cleanup initialized.");

        try {
            // Connect to the database
            database = new Database();
            database.connect();

            // Clean out deleted quotes older than one week
            statement = database.getConnection().prepareStatement("DELETE FROM Quotes WHERE Deleted = '1' AND Date < DATE_SUB(NOW(), INTERVAL 7 DAY)");
            status = statement.executeUpdate();
            Configuration.getLogger().write(Level.INFO, "Cleaned up " + status + " deleted quotes from the database.");

            statement.close();

            // Clean out game statuses older than 12 hours
            statement = database.getConnection().prepareStatement("DELETE FROM GameStatus WHERE Date < DATE_SUB(NOW(), INTERVAL 12 HOUR)");
            status = statement.executeUpdate();
            Configuration.getLogger().write(Level.INFO, "Cleaned up " + status + " stale game statuses from the database.");

            database.disconnect();
            
            Configuration.getLogger().write(Level.INFO, "Database cleanup complete.");
        } catch (Exception ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
        }
    }
}
