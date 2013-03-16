package us.rddt.IRCBot.Implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.IRCUtils;

/**
 * Using the reddit API, this class can return information about a provided reddit
 * link/comment URL including the submission date, number of votes, and whether
 * the link should be considered NSFW (not safe for work).
 * 
 * @see us.rddt.IRCBot.Implementations.URLGrabber
 * @author Ryan Morrison
 */
public class RedditLink {
    /*
     * Class variables.
     */
    private String id;
    private String title;
    private String author;
    private String subreddit;
    private long created_utc;
    private int score;
    private boolean over_18;
    private boolean is_nsfl;
    private String context_username;

    /**
     * Class constructor
     */
    public RedditLink() {
    }

    /**
     * Class constructor
     * @param id the reddit submission's ID
     * @param title the reddit submission's title
     * @param author the reddit submission's author
     * @param subreddit the reddit submission's subreddit
     * @param created_utc the reddit submission's creation date in UTC
     * @param score the reddit submission's current score
     * @param over_18 is the reddit submission marked as NSFW
     */
    public RedditLink(String id, String title, String author, String subreddit, long created_utc, int score, boolean over_18, boolean is_nsfl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.subreddit = subreddit;
        this.created_utc = created_utc;
        this.score = score;
        this.over_18 = over_18;
        this.is_nsfl = is_nsfl;
    }
    
    /**
     * Class constructor
     * @param id the reddit submission's ID
     * @param title the reddit submission's title
     * @param author the reddit submission's author
     * @param subreddit the reddit submission's subreddit
     * @param created_utc the reddit submission's creation date in UTC
     * @param score the reddit submission's current score
     * @param over_18 is the reddit submission marked as NSFW
     * @param context_username the username of the comment submitter
     */
    public RedditLink(String id, String title, String author, String subreddit, long created_utc, int score, boolean over_18, boolean is_nsfl, String context_username) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.subreddit = subreddit;
        this.created_utc = created_utc;
        this.score = score;
        this.over_18 = over_18;
        this.is_nsfl = is_nsfl;
        this.context_username = context_username;
    }

    /**
     * Gets the content of a provided Reddit URL
     * @param link the URL to fetch the JSON data from
     * @throws IOException if the download fails
     * @throws JSONException if the JSON cannot be parsed
     */
    public static RedditLink getLink(URL link, boolean isContext) throws IOException, JSONException {
        /*
         * Variables.
         */
        StringBuilder jsonToParse = new StringBuilder();
        String buffer;

        /*
         * Opens a connection to the provided URL, and downloads the data into a temporary variable.
         */
        HttpURLConnection conn = (HttpURLConnection)link.openConnection();
        conn.setRequestProperty("User-Agent", Configuration.getUserAgent());
        if(conn.getResponseCode() >= 400) {
            throw new IOException("Server returned response code: " + conn.getResponseCode());
        }

        BufferedReader buf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while((buffer = buf.readLine()) != null) {
            jsonToParse.append(buffer);
        }

        /*
         * Disconnect from the server.
         */
        conn.disconnect();

        /*
         * Parse the JSON data.
         */
        JSONArray parsedArray = new JSONArray(jsonToParse.toString());
        JSONObject redditLink = parsedArray.getJSONObject(0).getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data");
        // If a URL contains a context parameter, also parse out the username of the comment's submitter
        if(isContext) {
        	JSONObject redditContext = parsedArray.getJSONObject(1).getJSONObject("data").getJSONArray("children").getJSONObject(0).getJSONObject("data");
        	return new RedditLink(redditLink.getString("id"),
                    IRCUtils.escapeHTMLEntities(redditLink.getString("title")),
                    redditLink.getString("author"),
                    redditLink.getString("subreddit"),
                    redditLink.getLong("created_utc"),
                    redditLink.getInt("score"),
                    redditLink.getBoolean("over_18"),
                    redditLink.getString("title").toUpperCase().contains("NSFL"),
                    redditContext.getString("author"));
        } else {
        	return new RedditLink(redditLink.getString("id"),
        			IRCUtils.escapeHTMLEntities(redditLink.getString("title")),
        			redditLink.getString("author"),
        			redditLink.getString("subreddit"),
        			redditLink.getLong("created_utc"),
        			redditLink.getInt("score"),
        			redditLink.getBoolean("over_18"),
        			redditLink.getString("title").toUpperCase().contains("NSFL"));
        }
    }

    /**
     * Checks imgur links against the Reddit API
     * @param link the URL to fetch the JSON data from
     * @return the best possible submission to use
     * @throws IOException if the download fails
     * @throws JSONException if the JSON cannot be parsed
     */
    public RedditLink checkImgurLink(URL link) throws IOException, JSONException {
        /*
         * Variables.
         */
        StringBuilder jsonToParse = new StringBuilder();
        String buffer;

        ArrayList<RedditLink> submissions = new ArrayList<RedditLink>();

        /*
         * Opens a connection to the provided URL, and downloads the data into a temporary variable.
         */
        HttpURLConnection conn = (HttpURLConnection)link.openConnection();
        conn.setRequestProperty("User-Agent", Configuration.getUserAgent());
        if(conn.getResponseCode() != 200) {
            throw new IOException("Server returned response code: " + conn.getResponseCode());
        }

        BufferedReader buf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while((buffer = buf.readLine()) != null) {
            jsonToParse.append(buffer);
        }

        /*
         * Disconnect from the server.
         */
        conn.disconnect();

        /*
         * Parse each submission into an ArrayList of RedditLink classes.
         * Return the best possible submission.
         * If there are no submissions at all, return null instead.
         */
        JSONObject parsedArray = new JSONObject(jsonToParse.toString());
        if(parsedArray.getJSONObject("data").getJSONArray("children").length() > 0) {
            for(int i = 0; i < parsedArray.getJSONObject("data").getJSONArray("children").length(); i++) {
                submissions.add(new RedditLink(parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(i).getJSONObject("data").getString("id"),
                        IRCUtils.escapeHTMLEntities(parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(i).getJSONObject("data").getString("title")),
                        parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(i).getJSONObject("data").getString("author"),
                        parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(i).getJSONObject("data").getString("subreddit"),
                        parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(i).getJSONObject("data").getLong("created_utc"),
                        parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(i).getJSONObject("data").getInt("score"),
                        parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(i).getJSONObject("data").getBoolean("over_18"),
                        parsedArray.getJSONObject("data").getJSONArray("children").getJSONObject(i).getJSONObject("data").getString("title").toUpperCase().contains("NSFL")));
            }
            return weighSubmissions(submissions);
        } else {
            return null;
        }
    }

    /**
     * Weighs Reddit submissions to determine the most appropriate submission to use
     * @param submissions the submissions (stored in an ArrayList) to weigh
     * @return the best submission
     */
    private RedditLink weighSubmissions(ArrayList<RedditLink> submissions) {
        /*
         * Variables
         */
        int bestSubmission = 0;
        double bestWeightValue = 0;
        double weightScore = 0;
        /*
         * For each submission, assign a score to each potential candidate.
         * The score is based upon the age of the submission and the score of the submission.
         * If the score is higher than the current best submission, replace it.
         * Return the best possible submission.
         */
        for(int i = 0; i < submissions.size(); i++) {
            int submissionScore = submissions.get(i).score;
            long submissionDate = submissions.get(i).created_utc;
            try {
                weightScore = (submissionScore / ((double)((new Date().getTime() / 1000) - submissionDate) / 3600));
            } catch (ArithmeticException ex) {
                Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            }
            if(weightScore > bestWeightValue) {
                bestSubmission = i;
                bestWeightValue = weightScore;
            }
        }
        return submissions.get(bestSubmission);
    }

    /**
     * Returns the title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the author
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the subreddit
     * @return the subreddit
     */
    public String getSubreddit() {
        return subreddit;
    }

    /**
     * Returns the creation date in UTC time
     * @return the creation date in UTC time
     */
    public long getCreatedUTC() {
        return created_utc;
    }

    /**
     * Returns the creation date in UTC time, in a human-readable string format
     * @return the creation date in UTC time, in a human-readable string format
     */
    public String getCreatedReadableUTC() {
        return IRCUtils.toReadableTime(new Date(getCreatedUTC() * 1000), false, true);
    }

    /**
     * Returns the score
     * @return the score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * Returns the username of the person who submitted the linked comment (if applicable)
     * @return the username of the person who submitted the linked comment
     */
    public String getContextUsername() {
    	return context_username;
    }

    /**
     * Returns if the submission is marked NSFW or not
     * @return true if the submission is NSFW, false if not
     */
    public boolean isOver18() {
        return over_18;
    }
    
    /**
     * Returns if the submission contains NSFL in the title
     * @return true if the submission contains NSFL in the title
     */
    public boolean isNSFL() {
        return is_nsfl;
    }

    /**
     * Returns the unique link ID
     * @return the unique link ID
     */
    public String getId() {
        return id;
    }
}
