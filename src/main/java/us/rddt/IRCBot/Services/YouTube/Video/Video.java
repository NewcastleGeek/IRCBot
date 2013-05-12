package us.rddt.IRCBot.Services.YouTube.Video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import us.rddt.IRCBot.Configuration;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class to deserialize a YouTube video object to.
 * @author Ryan Morrison
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"apiVersion",
	"data"
})
public class Video {

	@JsonProperty("apiVersion")
	private String apiVersion;
	@JsonProperty("data")
	private Data data;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	/**
     * Gets information about a provided link to a YouTube video
     * @return a new instance of the class with the video's details
     * @param link the link to the user page
     * @throws IOException if the download fails
     */
	public static Video getVideoProperties(String id) throws IOException {
        /*
         * Variables
         */
        StringBuilder jsonToParse = new StringBuilder();
        String buffer;
        URL apiUrl = new URL("http://gdata.youtube.com/feeds/api/videos?q=" + id + "&v=2&alt=jsonc");

        /*
         * Opens a connection to the provided URL, and downloads the data into a temporary variable.
         */
        HttpURLConnection conn = (HttpURLConnection)apiUrl.openConnection();
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
        /*JSONObject parsedArray = new JSONObject(jsonToParse.toString());
        if(parsedArray.getJSONObject("data").getInt("totalItems") > 0) {
            JSONObject youtubeLink = parsedArray.getJSONObject("data").getJSONArray("items").getJSONObject(0);
            return new YouTubeVideo(IRCUtils.escapeHTMLEntities(youtubeLink.getString("title")), youtubeLink.getString("uploader"), youtubeLink.getLong("duration"));
        } else {
            throw new NoSuchElementException("YouTube video ID invalid or video is private.");
        }*/
        ObjectMapper mapper = new ObjectMapper();
        Video video = mapper.readValue(jsonToParse.toString(), Video.class);
        return video;
    }

	@JsonProperty("apiVersion")
	public String getApiVersion() {
		return apiVersion;
	}

	@JsonProperty("apiVersion")
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	@JsonProperty("data")
	public Data getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(Data data) {
		this.data = data;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperties(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}