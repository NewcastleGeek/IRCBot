package us.rddt.IRCBot.Implementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import us.rddt.IRCBot.Configuration;

/**
 * Queries and returns data about a given Steam user from the Steam Web API
 * @author Ryan Morrison
 */
public class SteamUser {
    // Public Data
    private long steamId;
    private String personaName;
    private String profileUrl;
    private String avatarUrl;
    private String avatarMediumUrl;
    private String avatarFullUrl;
    private int personaState;
    private int communityVisibilityState;
    private int profileState;
    private long lastLogOff;
    private int commentPermission;

    // Private Data
    private String realName;
    private long primaryClanId;
    private long timeCreated;
    private int gameId;
    private String gameServerIp;
    private String gameExtraInfo;
    private String locCountryCode;
    private String locStateCode;
    private int locCityId;

    // Persona States
    public static final String personaStates[] = {"offline", "online", "busy", "away", "snooze", "looking to trade", "looking to play"};

    // User ID cache
    private static volatile Map<String, Long> idCache = new HashMap<String, Long>();

    /**
     * Class constructor
     * @param steamId the Steam ID of the user to query
     * @param fetchData whether to perform the query
     * @throws IOException if there is an error querying the API
     * @throws JSONException if there is an error parsing the returned JSON
     */
    public SteamUser(long steamId, boolean fetchData) throws IOException, JSONException {
        if(fetchData) {
            getUser(steamId);
        } else {
            this.steamId = steamId;
        }
    }

    /**
     * Class constructor
     * @param communityName the Steam Community name of the user to query
     * @param fetchData whether to perform the query
     * @throws DOMException if an error occurs in the DOM
     * @throws IOException if there is an error querying the API
     * @throws JSONException if there is an error parsing the returned JSON
     * @throws ParserConfigurationException if there is an error configuring the XML parser
     * @throws ParseException if there is an error parsing the returned XML
     * @throws SAXException if there is an error preparing the XML to be parsed
     */
    public SteamUser(String communityName, boolean fetchData) throws DOMException, IOException, JSONException, ParserConfigurationException, ParseException, SAXException {
        this(getSteamId64FromName(communityName), fetchData);
    }

    /**
     * Queries the Steam Web API for a provided user ID
     * @param steamId the user ID to query
     * @throws IOException if there is an error querying the API
     * @throws JSONException if there is an error parsing the returned JSON
     */
    private void getUser(long steamId) throws IOException, JSONException {
        /*
         * Variables
         */
        StringBuilder jsonToParse = new StringBuilder();
        String buffer;

        /*
         * Opens a connection to the provided URL, and downloads the data into a temporary variable.
         */
        HttpURLConnection conn = (HttpURLConnection)new URL("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + Configuration.getSteamAPIKey() + "&steamids=" + steamId).openConnection();
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
        JSONObject parsedObject = new JSONObject(jsonToParse.toString()).getJSONObject("response").getJSONArray("players").getJSONObject(0);
        // Public Data
        this.setSteamId(parsedObject.getLong("steamid"));
        this.setPersonaName(parsedObject.getString("personaname"));
        this.setProfileUrl(parsedObject.getString("profileurl"));
        this.setAvatarUrl(parsedObject.getString("avatar"));
        this.setAvatarMediumUrl(parsedObject.getString("avatarmedium"));
        this.setAvatarFullUrl(parsedObject.getString("avatarfull"));
        this.setPersonaState(parsedObject.getInt("personastate"));
        this.setCommunityVisibilityState(parsedObject.getInt("communityvisibilitystate"));
        this.setProfileState(parsedObject.getInt("profilestate"));
        this.setLastLogOff(parsedObject.getLong("lastlogoff"));
        if(parsedObject.has("commentpermission")) this.setCommentPermission(parsedObject.getInt("commentpermission"));
        // Private Data
        if(parsedObject.has("realname")) this.setRealName(parsedObject.getString("realname"));
        if(parsedObject.has("primaryclanid")) this.setPrimaryClanId(parsedObject.getLong("primaryclanid"));
        if(parsedObject.has("timecreated")) this.setTimeCreated(parsedObject.getLong("timecreated"));
        if(parsedObject.has("gameid")) this.setGameId(parsedObject.getInt("gameid"));
        if(parsedObject.has("gameserverip")) this.setGameServerIp(parsedObject.getString("gameserverip"));
        if(parsedObject.has("gameextrainfo")) this.setGameExtraInfo(parsedObject.getString("gameextrainfo"));
        if(parsedObject.has("loccountrycode")) this.setLocCountryCode(parsedObject.getString("loccountrycode"));
        if(parsedObject.has("locstatecode")) this.setLocStateCode(parsedObject.getString("locstatecode"));
        if(parsedObject.has("loccityid")) this.setLocCityId(parsedObject.getInt("loccityid"));
    }

    /**
     * Retrieves a user's 64-bit Steam ID from their Community Name
     * @param communityName the user's community name
     * @return the 64-bit Steam ID
     * @throws IOException if there is an error querying the API
     * @throws DOMException if an error occurs in the DOM
     * @throws ParserConfigurationException if there is an error configuring the XML parser
     * @throws ParseException if there is an error parsing the returned XML
     * @throws SAXException if there is an error preparing the XML to be parsed
     */
    private static long getSteamId64FromName(String communityName) throws IOException, DOMException, ParserConfigurationException, ParseException, SAXException {
        /*
         * First attempt to return the 64-bit ID from cache. If the user does not exist in the cache, then query
         * the API for the ID and store it in the cache for future use.
         */
        if(idCache.containsKey(communityName)) {
            return idCache.get(communityName);
        } else {
            /*
             * Opens a connection to the API
             */
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection conn = (HttpURLConnection) (new URL("http://steamcommunity.com/id/" + communityName + "?xml=1")).openConnection();

            conn.setRequestProperty("User-Agent", Configuration.getUserAgent());
            if(conn.getResponseCode() >= 400) {
                throw new IOException("Server returned response code: " + conn.getResponseCode());
            }

            /*
             * Parses and returns the user's 64-bit Steam ID
             */
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Element profile = parser.parse(conn.getInputStream()).getDocumentElement();
            
            long steamID64 = Long.parseLong(profile.getElementsByTagName("steamID64").item(0).getTextContent());

            synchronized(idCache) {
                idCache.put(communityName, steamID64);
            }
            
            return steamID64;
        }
    }

    /**
     * Returns the user's 64-bit Steam ID
     * @return the user's 64-bit Steam ID
     */
    public long getSteamId() {
        return steamId;
    }

    /**
     * Sets the user's 64-bit Steam ID
     * @param steamId the 64-bit Steam ID to set
     */
    public void setSteamId(long steamId) {
        this.steamId = steamId;
    }

    /**
     * Returns the user's set nickname
     * @return the user's set nickname
     */
    public String getPersonaName() {
        return personaName;
    }

    /**
     * Sets the user's nickname
     * @param personaName the user's nickname to set
     */
    public void setPersonaName(String personaName) {
        this.personaName = personaName;
    }

    /**
     * Returns the user's profile URL
     * @return the user's profile URL
     */
    public String getProfileUrl() {
        return profileUrl;
    }

    /**
     * Sets the user's profile URL
     * @param profileUrl the user's profile URL to set
     */
    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    /**
     * Returns the user's avatar URL
     * @return the user's avatar URL
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Sets the user's avatar URL
     * @param avatarUrl the user's avatar URL to set
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * Returns the user's medium size avatar URL
     * @return the user's medium size avatar URL
     */
    public String getAvatarMediumUrl() {
        return avatarMediumUrl;
    }

    /**
     * Sets the user's medium size avatar URL
     * @param avatarMediumUrl the user's medium size URL to set
     */
    public void setAvatarMediumUrl(String avatarMediumUrl) {
        this.avatarMediumUrl = avatarMediumUrl;
    }

    /**
     * Returns the user's full size avatar URL
     * @return the user's full size avatar URL
     */
    public String getAvatarFullUrl() {
        return avatarFullUrl;
    }

    /**
     * Sets the user's full size avatar URL
     * @param avatarFullUrl the user's full size avatar URL to set
     */
    public void setAvatarFullUrl(String avatarFullUrl) {
        this.avatarFullUrl = avatarFullUrl;
    }

    /**
     * Returns the user's current state
     * @return the user's current state
     */
    public int getPersonaState() {
        return personaState;
    }

    /**
     * Sets the user's current state
     * @param personaState the user's current state to set
     */
    public void setPersonaState(int personaState) {
        this.personaState = personaState;
    }

    /**
     * Returns the user's Community Visibility State
     * @return the user's Community Visibility State
     */
    public int getCommunityVisibilityState() {
        return communityVisibilityState;
    }

    /**
     * Sets the user's Community Visibility State
     * @param communityVisibilityState the user's Community Visibility State to set
     */
    public void setCommunityVisibilityState(int communityVisibilityState) {
        this.communityVisibilityState = communityVisibilityState;
    }

    /**
     * Returns the user's profile state
     * @return the user's profile state
     */
    public int getProfileState() {
        return profileState;
    }

    /**
     * Sets the user's profile state
     * @param profileState the user's profile state to set
     */
    public void setProfileState(int profileState) {
        this.profileState = profileState;
    }

    /**
     * Returns the user's last log off time
     * @return the user's last log off time
     */
    public long getLastLogOff() {
        return lastLogOff;
    }

    /**
     * Sets the user's last log off time
     * @param lastLogOff the user's last log off time
     */
    public void setLastLogOff(long lastLogOff) {
        this.lastLogOff = lastLogOff;
    }

    /**
     * Returns the user's comment permission status
     * @return the user's comment permission status
     */
    public int getCommentPermission() {
        return commentPermission;
    }

    /**
     * Sets the user's comment permission status
     * @param commentPermission the user's comment permission status to set
     */
    public void setCommentPermission(int commentPermission) {
        this.commentPermission = commentPermission;
    }

    /**
     * Returns the user's real name
     * @return the user's real name
     */
    public String getRealName() {
        return realName;
    }

    /**
     * Sets the user's real name
     * @param realName the user's real name to set
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * Returns the user's primary clan ID
     * @return the user's primary clan ID
     */
    public long getPrimaryClanId() {
        return primaryClanId;
    }

    /**
     * Sets the user's primary clan ID
     * @param primaryClanId the user's primary clan ID to set
     */
    public void setPrimaryClanId(long primaryClanId) {
        this.primaryClanId = primaryClanId;
    }

    /**
     * Returns the user's creation date
     * @return the user's creation date
     */
    public long getTimeCreated() {
        return timeCreated;
    }

    /**
     * Sets the user's creation date
     * @param timeCreated the user's creation date to set
     */
    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    /**
     * Returns the user's current game ID
     * @return the user's current game ID
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * Sets the user's current game ID
     * @param gameId the user's current game ID to set
     */
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    /**
     * Returns the user's current game server IP address
     * @return the user's current game server IP address
     */
    public String getGameServerIp() {
        return gameServerIp;
    }

    /**
     * Sets the user's current game server IP address
     * @param gameServerIp the user's current game server IP address to set
     */
    public void setGameServerIp(String gameServerIp) {
        this.gameServerIp = gameServerIp;
    }

    /**
     * Returns the user's current game title
     * @return the user's current game title
     */
    public String getGameExtraInfo() {
        return gameExtraInfo;
    }

    /**
     * Sets the user's current game title
     * @param gameExtraInfo the user's current game title to set
     */
    public void setGameExtraInfo(String gameExtraInfo) {
        this.gameExtraInfo = gameExtraInfo;
    }

    /**
     * Returns the user's country code
     * @return the user's country code
     */
    public String getLocCountryCode() {
        return locCountryCode;
    }

    /**
     * Sets the user's country code
     * @param locCountryCode the user's country code to set
     */
    public void setLocCountryCode(String locCountryCode) {
        this.locCountryCode = locCountryCode;
    }

    /**
     * Returns the user's state code
     * @return the user's state code
     */
    public String getLocStateCode() {
        return locStateCode;
    }

    /**
     * Sets the user's state code
     * @param locStateCode the user's state code to set
     */
    public void setLocStateCode(String locStateCode) {
        this.locStateCode = locStateCode;
    }

    /**
     * Returns the user's city ID (currently only used internally in the Steam Web API)
     * @return the locCityId the user's city ID
     */
    public int getLocCityId() {
        return locCityId;
    }

    /**
     * Sets the user's city ID
     * @param locCityId the user's city ID to set
     */
    public void setLocCityId(int locCityId) {
        this.locCityId = locCityId;
    }
}