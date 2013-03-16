package us.rddt.IRCBot.Logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Formats a log entry into an HTML format, for easier reading.
 * 
 * @author Ryan Morrison
 */
public class HTMLFormatter extends Formatter {
    /**
     * Formats a provided log entry into an HTML format
     * @param rec the LogRecord entry to format
     * @return the formatted entry
     */
    public String format(LogRecord rec) {
        StringBuffer buf = new StringBuffer(1000);
        buf.append("<tr>");
        buf.append("<td>");

        if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("</b>");
        } else {
            buf.append(rec.getLevel());
        }
        buf.append("</td>");
        buf.append("<td>");
        buf.append(calcDate(rec.getMillis()));
        buf.append("</td>");
        buf.append("<td>");
        buf.append(formatMessage(rec));
        buf.append("</td>");
        buf.append("</tr>\n");
        return buf.toString();
    }
    
    /**
     * Helper method to calculate a date provided a long value
     * @param millisecs the long value in milliseconds to return to a readable date
     * @return the readable date string
     */
    private String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("MMM dd, yyyy hh:mma z");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }

    /**
     * Returns the header to apply to the handler's output
     * @param h the handler to apply the header to
     * @return the header
     */
    public String getHead(Handler h) {
        return "<html>\n<head></head>\n<body>Log Started: " + (new Date()) + "\n<pre>\n"
                + "<table border>\n  "
                + "<tr><th>Level</th><th>Time</th><th>Message</th></tr>\n";
    }

    /**
     * Returns the footer to apply to the handler's output
     * @param h the handler to apply the footer to
     * @return the footer
     */
    public String getTail(Handler h) {
        return "</table>\n  </pre></body>\n</html>\n";
    }
}
