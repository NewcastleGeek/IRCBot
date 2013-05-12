package us.rddt.IRCBot.Implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.IRCUtils;
import us.rddt.IRCBot.Enums.RedditTypes;
import us.rddt.IRCBot.Services.Vimeo.VimeoVideo;
import us.rddt.IRCBot.Services.YouTube.Video.Item;
import us.rddt.IRCBot.Services.YouTube.Video.YouTubeVideo;

/**
 * Detects and returns information for URLs the bot sees in a channel. For normal
 * web pages, the bot downloads a small portion of the page to retrieve the title
 * tag from the HTML markup, and returns that to the user. Content from reddit
 * is queried using the reddit API to return additional information. Links to twitter
 * tweets are queried using the twitter API to return the text of the provided tweet.
 * Links to YouTube videos queries the YouTube API to return the title of the video
 * and its duration. Other URLs that are not web pages and are not recognized by the
 * bot return the MIME type reported and the length (size) of the file.
 * 
 * @author Ryan Morrison
 */
public class URLGrabber implements Runnable {
    /*
     * Class variables.
     */
    private MessageEvent<PircBotX> event = null;
    private URL url = null;

    // Regex pattern to match imgur links
    private static final Pattern IMGUR_LINK = Pattern.compile("http:\\/\\/(www.)?(i.)?imgur\\.com\\/.+");
    // Regex pattern to match Reddit links
    private static final Pattern REDDIT_LINK = Pattern.compile("https?:\\/\\/(www.)?reddit\\.com\\/r\\/.+\\/comments\\/.+\\/.+(\\/)?");
    // Regex pattern to match Reddit subreddits
    private static final Pattern REDDIT_SUBREDDIT = Pattern.compile("https?:\\/\\/(www.)?reddit\\.com\\/r\\/.+\\/?");
    // Regex pattern to match Reddit users
    private static final Pattern REDDIT_USER = Pattern.compile("https?:\\/\\/(www.)?reddit\\.com\\/user\\/.+");
    // Regex pattern to match Twitter tweets
    private static final Pattern TWITTER_TWEET = Pattern.compile("https?:\\/\\/(www\\.)?twitter\\.com\\/(?:#!\\/)?(\\w+)\\/status(es)?\\/(\\d+)");
    // Regex pattern to match YouTube videos
    private static final Pattern YOUTUBE_VIDEO = Pattern.compile("https?:\\/\\/(www.)?youtube\\.com\\/watch\\?v=.+");
    // Regex pattern to match Vimeo videos
    private static final Pattern VIMEO_VIDEO = Pattern.compile("http:\\/\\/(www.)?vimeo\\.com\\/[0-9]+");

    /**
     * Content-Type class definition
     */
    private static final class ContentType {
        // Regex pattern to match the character set from the Content-Type
        private static final Pattern CHARSET_HEADER = Pattern.compile("charset=([-_a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);

        // Variables
        private String charsetName;
        private String contentType;

        // Constructor for the ContentType class
        private ContentType(String headerValue) {
            // Throw an exception should the passed parameter be null
            if (headerValue == null)
                throw new IllegalArgumentException("ContentType must be constructed with a not-null headerValue");
            // Locate the index of the semicolon in the header and use the regex above to match and extract the character set
            // If a semicolon doesn't exist then the character set was never provided, so set the Content-Type appropriately
            int n = headerValue.indexOf(";");
            if (n != -1) {
                contentType = headerValue.substring(0, n);
                Matcher matcher = CHARSET_HEADER.matcher(headerValue);
                if (matcher.find())
                    charsetName = matcher.group(1);
            }
            else
                contentType = headerValue;
        }
    }

    /**
     * Extracts the character set from the Content-Type header property
     * @param contentType the Content-Type property to parse
     * @return the character set
     */
    private static Charset getCharset(ContentType contentType) {
        // Extract the character set from the character set or return null upon failure
        if (contentType != null && contentType.charsetName != null && Charset.isSupported(contentType.charsetName))
            return Charset.forName(contentType.charsetName);
        else
            return null;
    }
    /**
     * Extracts the Content-Length property from the HTTP response
     * @param conn the open HTTP connection to read the headers from
     * @return the length of the data stream
     */
    private static long getContentLengthHeader(HttpURLConnection conn) {
        // Variables
        int i = 0;
        boolean moreHeaders = true;
        // Loop through the headers until we find the Content-Length property
        // If we find it, break out, otherwise continue reading
        do {
            String headerName = conn.getHeaderFieldKey(i);
            String headerValue = conn.getHeaderField(i);
            if (headerName != null && headerName.equals("Content-Length"))
                return Long.parseLong(headerValue);
            i++;
            moreHeaders = headerName != null || headerValue != null;
        }
        while (moreHeaders);
        // If we reach this point we couldn't find the headers we need, so return 0
        return 0;
    }

    /**
     * Extracts the Content-Type property from the HTTP response
     * @param conn the open HTTP connection to read the headers from
     * @return the content type header value(s)
     */
    private static ContentType getContentTypeHeader(HttpURLConnection conn) {
        // Variables
        int i = 0;
        boolean moreHeaders = true;
        // Loop through the headers until we find the Content-Type property
        // If we find it, break out, otherwise continue reading
        do {
            String headerName = conn.getHeaderFieldKey(i);
            String headerValue = conn.getHeaderField(i);
            if (headerName != null && headerName.equals("Content-Type"))
                return new ContentType(headerValue);
            i++;
            moreHeaders = headerName != null || headerValue != null;
        }
        while (moreHeaders);
        // If we reach this point we couldn't find the headers we need, so return null
        return null;
    }

    /**
     * Converts a data measurement value to a more human-readable format
     * @param bytes the length to convert into a data measurement
     * @param si whether to use the SI measurement
     * @return the formatted human-readable string
     */
    private static String humanReadableByteCount(long bytes, boolean si) {
        // Variable for the unit of measurement used
        int unit = si ? 1000 : 1024;
        // If our value is less than a kilobyte than just return the value untouched in bytes
        if (bytes < unit) return bytes + " B";
        // Otherwise, properly convert to the appropriate human-readable value and return it
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Class constructor
     */
    public URLGrabber(MessageEvent<PircBotX> event, URL url) {
        this.event = event;
        this.url = url;
    }

    /**
     * Determines if an imgur link has been posted to Reddit and return post details if it has
     * @param imgurURL the imgur URL to check against Reddit
     * @return true if the image appears on Reddit, false if it does not
     */
    private boolean checkImgurReddit(URL imgurURL) {
        // Variables
        URL appendURL = null;

        // Construct the appropriate URL to get the JSON via the Reddit API
        try {
            appendURL = new URL("http://www.reddit.com/api/info.json?url=" + imgurURL.toString());
            RedditLink link = new RedditLink();
            RedditLink bestSubmission = link.checkImgurLink(appendURL);
            if(bestSubmission != null) {
                String formattedString = "[imgur by '" + event.getUser().getNick() + "'] As spotted on Reddit: " + Colors.BOLD + bestSubmission.getTitle() + Colors.NORMAL + " (submitted by " + bestSubmission.getAuthor() + " to /r/" + bestSubmission.getSubreddit() + " about " + bestSubmission.getCreatedReadableUTC() + " ago, " + bestSubmission.getScore() + " points: http://redd.it/" + bestSubmission.getId() + ")";
                if(bestSubmission.isOver18()) {
                    formattedString += (" " + Colors.BOLD + Colors.RED + "[NSFW]");
                }
                if(bestSubmission.isNSFL()) {
                    formattedString += (" " + Colors.BOLD + Colors.RED + "[NSFL]");
                }
                event.getBot().sendMessage(event.getChannel(), formattedString);
                return true;
            } else {
                return false;
            }
        } catch (MalformedURLException ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
        } catch (Exception ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
        }
        return false;
    }

    /**
     * Formats a friendly error message to return to a user if a lookup fails
     * @param site the website where the lookup failed
     * @param message the error message
     */
    private String formatError(String site, String message) {
        return "[" + site + " by '" + event.getUser().getNick() + "'] An error occurred while retrieving this URL. (" + IRCUtils.trimString(message, 50) + ")";
    }

    /**
     * Gets the page title from a provided URL
     * @param url the URL of the page to extract the title from
     * @return the page title
     * @throws Exception if an error occurs downloading the page
     */
    private String getPageTitle(URL url) throws Exception {
        // Connect to the server
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        // Set a proper user agent, some sites return HTTP 409 without it
        conn.setRequestProperty("User-Agent", Configuration.getUserAgent());
        // Follow 301 redirects
        if(conn.getResponseCode() == 301) {
            return getPageTitle(new URL(conn.getHeaderField("Location")));
        }
        // Return an error if the response code is over 400
        if(conn.getResponseCode() >= 400) {
            throw new IOException("Server returned response code: " + conn.getResponseCode());
        }
        // No need to check validity of the URL - it's already been proven valid at this point
        // Get the Content-Type property from the HTTP headers so we can parse accordingly
        ContentType contentType = getContentTypeHeader(conn);
        
        // Prefix for marking SSL/TLS and IPv6 connections
        StringBuilder builder = new StringBuilder();
        
        // Check whether the connection is IPv6 or IPv4
        String ip = InetAddress.getByName(url.getHost()).getHostAddress();
        if(ip.contains(":") && !ip.contains(".")) {
            builder.append(Colors.BOLD + Colors.GREEN + "[IPv6]" + Colors.NORMAL + " ");
        }
        // Check whether the connection is over HTTPS
        if (conn instanceof HttpsURLConnection) {
            builder.append(Colors.BOLD + Colors.GREEN + "[" + IRCUtils.getReadableCipherSuite(((HttpsURLConnection) conn).getCipherSuite()) + "]" + Colors.NORMAL + " ");
        }
        
        // Ensure the server did provide us with proper HTTP headers
        if(contentType == null) {
        	return builder.append("The server did not provide correct HTTP headers. Unable to determine Content-Type property.").toString();
        }
        // If the document isn't HTML, return the Content-Type and Content-Length instead
        if(!contentType.contentType.equals("text/html")) {
            return builder.append("Type: " + contentType.contentType + ", size: " + humanReadableByteCount(getContentLengthHeader(conn), true)).toString();
        }
        else {
            // Get the character set or use the default accordingly
            Charset charset = getCharset(contentType);
            if(charset == null) charset = Charset.defaultCharset();
            // Create and prepare our streams for reading from the web server
            InputStream in = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
            // More variables
            int n = 0, totalRead = 0;
            char[] buf = new char[1024];
            StringBuilder content = new StringBuilder();

            // Read the page from the web server into the buffer up to 8192 bytes and create a string from it
            // The first 8192 bytes should be enough to fetch the title, while saving us time and bandwidth
            while(totalRead < 8192 && (n = reader.read(buf, 0, buf.length)) != -1) {
                content.append(buf, 0, n);
                totalRead += n;
            }
            // Close the BufferedReader
            reader.close();
            // Disconnect from the web server
            conn.disconnect();

            // Regex giving you trouble? Well, it's only 8192 bytes, so let's just do a straight-up string search
            int titleIndex = content.indexOf("<title>");
            int titleEndIndex = content.indexOf("</title>");
            if (titleIndex == -1 || titleEndIndex == -1 || titleIndex >= content.length() || titleEndIndex >= content.length())
            {
                return "Title not found or not within first 8192 bytes of page, aborting.";
            }
            // Abbreviate with ellipsis if titles are greater than 180 characters to avoid abuse/spam
            return builder.append(Colors.BOLD + StringUtils.abbreviate(IRCUtils.escapeHTMLEntities(content.substring(titleIndex + 7, titleEndIndex).replaceAll("[\\s\\<>]+", " ").trim()), 180)).toString();
        }
    }

    /**
     * Prints the title of a Reddit submissions or information about a user depending on the URL provided
     * @param redditURL the reddit URL to extract the data from
     * @param type the type of reddit link to parse
     */
    private void returnReddit(URL redditURL, RedditTypes type) {
        // Variables
        URL appendURL = null;

        // Construct the appropriate URL to get the JSON via the Reddit API
        try {
            if(type == RedditTypes.USER) {
                appendURL = new URL(redditURL.toString() + "/about.json");
                RedditUser user = RedditUser.getUser(appendURL);
                String formattedString = "[Reddit by '" + event.getUser().getNick() + "'] " + Colors.BOLD + user.getName() + Colors.NORMAL + ": " + user.getLinkKarma() + " link karma, " + user.getCommentKarma() + " comment karma, user since " + user.getReadableCreated();
                if(user.isGold()) {
                    formattedString += " [reddit gold]";
                }
                event.getBot().sendMessage(event.getChannel(), formattedString);
                return;
            } else if(type == RedditTypes.URL) {
            	RedditLink link;
            	// If a ? appears in a reddit URL, the URL must contain a context parameter
            	// This signals that we are being linked to a comment in a post with context
            	int contextIndex = redditURL.toString().lastIndexOf('?');
            	// lastIndexOf returns -1 on characters not found, so build the API query URL normally
            	if(contextIndex == -1) {
            		appendURL = new URL(redditURL.toString() + "/.json");
            		link = RedditLink.getLink(appendURL, false);
            	}
            	// We need to insert the '/.json' to query the API *before* the context parameter
            	else {
            		appendURL = new URL(new StringBuffer(redditURL.toString()).insert(contextIndex, "/.json").toString());
            		link = RedditLink.getLink(appendURL, true);
            	}
            	// Build the string to print to the channel, adding in comment details if necessary
                String formattedString = "[Reddit by '" + event.getUser().getNick() + "'] ";
                if(contextIndex != -1) formattedString += link.getContextUsername() + " comments on ";
                formattedString += Colors.BOLD + link.getTitle() + Colors.NORMAL + " (submitted by " + link.getAuthor() + " to /r/" + link.getSubreddit() + " about " +  link.getCreatedReadableUTC() + " ago, " + link.getScore() + " points)";
                if(link.isOver18()) {
                    formattedString += (" " + Colors.BOLD + Colors.RED + "[NSFW]");
                }
                if(link.isNSFL()) {
                    formattedString += (" " + Colors.BOLD + Colors.RED + "[NSFL]");
                }
                event.getBot().sendMessage(event.getChannel(), formattedString);
                return;
            } else if(type == RedditTypes.SUBREDDIT) {
                appendURL = new URL(redditURL.toString() + "/about.json");
                RedditSubreddit subreddit = RedditSubreddit.getSubreddit(appendURL);
                String formattedString = "[Reddit by '" + event.getUser().getNick() + "'] " + Colors.BOLD + "/r/" +  subreddit.getDisplayName() + Colors.NORMAL + " : " + subreddit.getPublicDescription() + " (" + subreddit.getFormattedSubscribers() + " subscribers)";
                if(subreddit.isOver18()) {
                    formattedString += (" " + Colors.BOLD + Colors.RED + "[NSFW]");
                }
                event.getBot().sendMessage(event.getChannel(), formattedString);
                return;
            }
        } catch (MalformedURLException ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            return;
        } catch (Exception ex) {
            event.getBot().sendMessage(event.getChannel(), formatError("Reddit", ex.getMessage()));
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            return;
        }
    }

    /**
     * Prints the content of a provided tweet to a specified channel
     * @param tweetID the ID value of the tweet to print
     */
    private void returnTweet(long tweetID) {
        try {
            // Get the Tweet and send it back to the channel
            Twitter twitter = Configuration.getTwitterInstance();
            Status status = twitter.showStatus(tweetID);
            event.getBot().sendMessage(event.getChannel(), "[Tweet by '" + event.getUser().getNick() + "'] " + Colors.BOLD + "@" + status.getUser().getScreenName() + Colors.NORMAL + ": " + status.getText());
        } catch (TwitterException te) {
            event.getBot().sendMessage(event.getChannel(), formatError("Twitter", te.getMessage()));
            Configuration.getLogger().write(Level.WARNING, te.getStackTrace().toString());
        }
    }

    /**
     * Prints the title and duration of a YouTube video to a specified channel
     * @param youtubeURL the URL to process
     */
    private void returnYouTubeVideo(URL youtubeURL) {
        // Construct the URL to read the JSON data from
        try {
        	Item item = ((YouTubeVideo) YouTubeVideo.downloadMetadata(new URL("http://gdata.youtube.com/feeds/api/videos?q=" + youtubeURL.toString().split("=")[1] + "&v=2&alt=jsonc"), YouTubeVideo.class)).getData().getItems().iterator().next();
            event.getBot().sendMessage(event.getChannel(), "[YouTube by '" + event.getUser().getNick() + "'] " + Colors.BOLD + item.getTitle() + Colors.NORMAL + " (uploaded by " + item.getUploader() + ", " + item.getReadableDuration() + ")");
            return;
        } catch (MalformedURLException ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            return;
        } catch (Exception ex) {
            event.getBot().sendMessage(event.getChannel(), formatError("YouTube", ex.getMessage()));
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            return;
        }
    }
    
    /**
     * Prints the title and duration of a Vimeo video to a specified channel
     * @param vimeoURL the URL to process
     */
    private void returnVimeoVideo(URL vimeoURL) {
        // Construct the URL to read the JSON data from
        try {
        	VimeoVideo video = (VimeoVideo) VimeoVideo.downloadMetadata(new URL("http://vimeo.com/api/v2/video/" + url.toString().substring(url.toString().lastIndexOf("/") + 1) + ".json"), VimeoVideo.class);
            event.getBot().sendMessage(event.getChannel(), "[Vimeo by '" + event.getUser().getNick() + "'] " + Colors.BOLD + video.getTitle() + Colors.NORMAL + " (uploaded by " + video.getUsername() + ", " + video.getReadableDuration() + ")");
            return;
        } catch (MalformedURLException ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            return;
        } catch (IOException ex) {
            if(ex.getMessage().equals("Server returned response code: 404")) {
                event.getBot().sendMessage(event.getChannel(), formatError("Vimeo", "Vimeo video ID invalid or video is private."));
            }
        } catch (Exception ex) {
            event.getBot().sendMessage(event.getChannel(), formatError("Vimeo", ex.getMessage()));
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            return;
        }
    }
    
    /**
     * Method that executes upon thread start
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        // Run the URL through each regex pattern and parse accordingly
        Matcher urlMatcher = TWITTER_TWEET.matcher(url.toString());
        if(urlMatcher.find()) {
            returnTweet(Long.parseLong(url.toString().substring(url.toString().lastIndexOf("/")).replaceAll("/", "")));
            return;
        }
        urlMatcher = REDDIT_LINK.matcher(url.toString());
        if(urlMatcher.find()) {
            try {
                returnReddit(new URL(urlMatcher.group()), RedditTypes.URL);
            } catch (MalformedURLException ex) {
                return;
            }
            return;
        }
        urlMatcher = REDDIT_SUBREDDIT.matcher(url.toString());
        if(urlMatcher.find()) {
            returnReddit(url, RedditTypes.SUBREDDIT);
            return;
        }
        urlMatcher = REDDIT_USER.matcher(url.toString());
        if(urlMatcher.find()) {
            returnReddit(url, RedditTypes.USER);
            return;
        }
        urlMatcher = IMGUR_LINK.matcher(url.toString());
        if(urlMatcher.find()) {
            if(checkImgurReddit(url)) return;
        }
        urlMatcher = YOUTUBE_VIDEO.matcher(url.toString());
        if(urlMatcher.find()) {
            returnYouTubeVideo(url);
            return;
        }
        urlMatcher = VIMEO_VIDEO.matcher(url.toString());
        if(urlMatcher.find()) {
            returnVimeoVideo(url);
            return;
        }
        // If none of the regex patterns matched, then get the page title/length
        try {
            event.getBot().sendMessage(event.getChannel(), ("[URL by '" + event.getUser().getNick() + "'] " + getPageTitle(url)));
        } catch (Exception ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
            event.getBot().sendMessage(event.getChannel(), formatError("URL", ex.getMessage()));
            return;
        }
    }

    /*
     * Static block to ensure that HTTPS connections don't bother validating certificate chains
     * Only executes on the initial class creation, doesn't run in every thread 
     */
    static {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        try {
            // Ensure that HTTPS connections use our custom trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception ex) {
            Configuration.getLogger().write(Level.WARNING, IRCUtils.getStackTraceString(ex));
        }
    }
}
