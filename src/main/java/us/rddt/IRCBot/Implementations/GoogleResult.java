package us.rddt.IRCBot.Implementations;

/**
 * A simple class to define a data type for individual Google search results.
 * 
 * @see us.rddt.IRCBot.Implementations.GoogleSearch
 * @author Ryan Morrison
 */
public class GoogleResult {
    /*
     * Class variables
     */
    private String url;
    private String title;
    
    /**
     * Class constructor
     * @param url the URL of the result
     * @param title the title of the result
     */
    public GoogleResult(String url, String title) {
        this.url = url;
        this.title = title;
    }
    
    /**
     * Returns the URL of the result
     * @return the URL of the result
     */
    public String getUrl() {
        return url;
    }
    /**
     * Returns the title of the result
     * @return the title of the result
     */
    public String getTitle() {
        return title;
    }
}
