package us.rddt.IRCBot.Statistics;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.logging.Level;

import us.rddt.IRCBot.Configuration;

/**
 * Schedules a task to output channel statistics once daily at midnight.
 * @author Ryan Morrison
 */
public class StatisticsUpdater {
    private static Timer timer;
    
    /**
     * Schedules the timer to update statistics automatically.
     */
    public static void schedule() {
        Calendar calendar = Calendar.getInstance();
        // Set the calendar to midnight for the next day
        calendar.set(Calendar.DAY_OF_MONTH, new GregorianCalendar().get(Calendar.DAY_OF_MONTH) + 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        timer = new Timer();
        timer.schedule(new StatisticsTask(), calendar.getTime(), 1000*60*60*24);
        
        Configuration.getLogger().write(Level.INFO, "Created timer to refresh statistics.");
    }
}
