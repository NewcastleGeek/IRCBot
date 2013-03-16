package us.rddt.IRCBot.Logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import us.rddt.IRCBot.Configuration;

/**
 * Initializes a logger to write HTML logfiles.
 * 
 * @author Ryan Morrison
 */
public class IRCLogger {
    /*
     * Class variables
     */
    private FileHandler fileHTML;
    private Formatter formatterHTML;
    
    private final Logger LOGGER = Logger.getLogger(IRCLogger.class.getName());

    /**
     * Initializes the logger and prepares it for use
     * @throws IOException
     */
    public void setup() throws IOException {
        Logger logger = Logger.getLogger("");
        fileHTML = new FileHandler(Configuration.getLogFile());

        formatterHTML = new HTMLFormatter();
        fileHTML.setFormatter(formatterHTML);
        logger.addHandler(fileHTML);
    }
    
    /**
     * Writes a log entry to the file
     * @param level the log level to write
     * @param output the data to be outputted to the log
     */
    public void write(Level level, String output) {
        LOGGER.log(level, output);
    }
}
