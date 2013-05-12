package us.rddt.IRCBot.Services.Vimeo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import us.rddt.IRCBot.Configuration;
import us.rddt.IRCBot.IRCUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class to deserialize a Vimeo video object to.
 * @author Ryan Morrison
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"id",
	"title",
	"description",
	"url",
	"upload_date",
	"mobile_url",
	"thumbnail_small",
	"thumbnail_medium",
	"thumbnail_large",
	"user_id",
	"user_name",
	"user_url",
	"user_portrait_small",
	"user_portrait_medium",
	"user_portrait_large",
	"user_portrait_huge",
	"stats_number_of_likes",
	"stats_number_of_plays",
	"stats_number_of_comments",
	"duration",
	"width",
	"height",
	"tags",
	"embed_privacy"
})
public class Video {

	@JsonProperty("id")
	private Integer id;
	@JsonProperty("title")
	private String title;
	@JsonProperty("description")
	private String description;
	@JsonProperty("url")
	private String url;
	@JsonProperty("upload_date")
	private String upload_date;
	@JsonProperty("mobile_url")
	private String mobile_url;
	@JsonProperty("thumbnail_small")
	private String thumbnail_small;
	@JsonProperty("thumbnail_medium")
	private String thumbnail_medium;
	@JsonProperty("thumbnail_large")
	private String thumbnail_large;
	@JsonProperty("user_id")
	private Integer user_id;
	@JsonProperty("user_name")
	private String user_name;
	@JsonProperty("user_url")
	private String user_url;
	@JsonProperty("user_portrait_small")
	private String user_portrait_small;
	@JsonProperty("user_portrait_medium")
	private String user_portrait_medium;
	@JsonProperty("user_portrait_large")
	private String user_portrait_large;
	@JsonProperty("user_portrait_huge")
	private String user_portrait_huge;
	@JsonProperty("stats_number_of_likes")
	private Integer stats_number_of_likes;
	@JsonProperty("stats_number_of_plays")
	private Integer stats_number_of_plays;
	@JsonProperty("stats_number_of_comments")
	private Integer stats_number_of_comments;
	@JsonProperty("duration")
	private long duration;
	@JsonProperty("width")
	private Integer width;
	@JsonProperty("height")
	private Integer height;
	@JsonProperty("tags")
	private String tags;
	@JsonProperty("embed_privacy")
	private String embed_privacy;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	/**
     * Gets information about a provided link to a Vimeo video
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
        URL apiUrl = new URL("http://vimeo.com/api/v2/video/" + id + ".json");

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
        
        ObjectMapper mapper = new ObjectMapper();
        Video video = mapper.readValue(jsonToParse.toString(), Video.class);
        System.out.println(video.getTitle());
        return video;
    }

	@JsonProperty("id")
	public Integer getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(Integer id) {
		this.id = id;
	}

	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = IRCUtils.escapeHTMLEntities(title);
	}

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	@JsonProperty("url")
	public void setUrl(String url) {
		this.url = url;
	}

	@JsonProperty("upload_date")
	public String getUploadDate() {
		return upload_date;
	}

	@JsonProperty("upload_date")
	public void setUploadDate(String upload_date) {
		this.upload_date = upload_date;
	}

	@JsonProperty("mobile_url")
	public String getMobileUrl() {
		return mobile_url;
	}

	@JsonProperty("mobile_url")
	public void setMobileUrl(String mobile_url) {
		this.mobile_url = mobile_url;
	}

	@JsonProperty("thumbnail_small")
	public String getThumbnailSmall() {
		return thumbnail_small;
	}

	@JsonProperty("thumbnail_small")
	public void setThumbnailSmall(String thumbnail_small) {
		this.thumbnail_small = thumbnail_small;
	}

	@JsonProperty("thumbnail_medium")
	public String getThumbnailMedium() {
		return thumbnail_medium;
	}

	@JsonProperty("thumbnail_medium")
	public void setThumbnailMedium(String thumbnail_medium) {
		this.thumbnail_medium = thumbnail_medium;
	}

	@JsonProperty("thumbnail_large")
	public String getThumbnailLarge() {
		return thumbnail_large;
	}

	@JsonProperty("thumbnail_large")
	public void setThumbnailLarge(String thumbnail_large) {
		this.thumbnail_large = thumbnail_large;
	}

	@JsonProperty("user_id")
	public Integer getUserId() {
		return user_id;
	}

	@JsonProperty("user_id")
	public void setUserId(Integer user_id) {
		this.user_id = user_id;
	}

	@JsonProperty("user_name")
	public String getUsername() {
		return user_name;
	}

	@JsonProperty("user_name")
	public void setUsername(String user_name) {
		this.user_name = user_name;
	}

	@JsonProperty("user_url")
	public String getUserUrl() {
		return user_url;
	}

	@JsonProperty("user_url")
	public void setUserUrl(String user_url) {
		this.user_url = user_url;
	}

	@JsonProperty("user_portrait_small")
	public String getUserPortraitSmall() {
		return user_portrait_small;
	}

	@JsonProperty("user_portrait_small")
	public void setUserPortraitSmall(String user_portrait_small) {
		this.user_portrait_small = user_portrait_small;
	}

	@JsonProperty("user_portrait_medium")
	public String getUserPortraitMedium() {
		return user_portrait_medium;
	}

	@JsonProperty("user_portrait_medium")
	public void setUserPortraitMedium(String user_portrait_medium) {
		this.user_portrait_medium = user_portrait_medium;
	}

	@JsonProperty("user_portrait_large")
	public String getUserPortraitLarge() {
		return user_portrait_large;
	}

	@JsonProperty("user_portrait_large")
	public void setUserPortraitLarge(String user_portrait_large) {
		this.user_portrait_large = user_portrait_large;
	}

	@JsonProperty("user_portrait_huge")
	public String getUserPortraitHuge() {
		return user_portrait_huge;
	}

	@JsonProperty("user_portrait_huge")
	public void setUserPortraitHuge(String user_portrait_huge) {
		this.user_portrait_huge = user_portrait_huge;
	}

	@JsonProperty("stats_number_of_likes")
	public Integer getStatsNumberOfLikes() {
		return stats_number_of_likes;
	}

	@JsonProperty("stats_number_of_likes")
	public void setStatsNumberOfLikes(Integer stats_number_of_likes) {
		this.stats_number_of_likes = stats_number_of_likes;
	}

	@JsonProperty("stats_number_of_plays")
	public Integer getStatsNumberOfPlays() {
		return stats_number_of_plays;
	}

	@JsonProperty("stats_number_of_plays")
	public void setStatsNumberOfPlays(Integer stats_number_of_plays) {
		this.stats_number_of_plays = stats_number_of_plays;
	}

	@JsonProperty("stats_number_of_comments")
	public Integer getStatsNumberOfComments() {
		return stats_number_of_comments;
	}

	@JsonProperty("stats_number_of_comments")
	public void setStatsNumberOfComments(Integer stats_number_of_comments) {
		this.stats_number_of_comments = stats_number_of_comments;
	}

	@JsonProperty("duration")
	public long getDuration() {
		return duration;
	}

	@JsonProperty("duration")
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public String getReadableDuration() {
		return IRCUtils.toReadableMinutes(getDuration());
	}

	@JsonProperty("width")
	public Integer getWidth() {
		return width;
	}

	@JsonProperty("width")
	public void setWidth(Integer width) {
		this.width = width;
	}

	@JsonProperty("height")
	public Integer getHeight() {
		return height;
	}

	@JsonProperty("height")
	public void setHeight(Integer height) {
		this.height = height;
	}

	@JsonProperty("tags")
	public String getTags() {
		return tags;
	}

	@JsonProperty("tags")
	public void setTags(String tags) {
		this.tags = tags;
	}

	@JsonProperty("embed_privacy")
	public String getEmbedPrivacy() {
		return embed_privacy;
	}

	@JsonProperty("embed_privacy")
	public void setEmbedPrivacy(String embed_privacy) {
		this.embed_privacy = embed_privacy;
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