package us.rddt.IRCBot.Services.YouTube.Video;

import java.util.HashMap;
import java.util.Map;

import us.rddt.IRCBot.IRCUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Class to deserialize a YouTube video object to.
 * @author Ryan Morrison
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"id",
	"uploaded",
	"updated",
	"uploader",
	"category",
	"title",
	"description",
	"thumbnail",
	"player",
	"content",
	"duration",
	"aspectRatio",
	"rating",
	"likeCount",
	"ratingCount",
	"viewCount",
	"favoriteCount",
	"commentCount",
	"accessControl"
})
public class Item {

	@JsonProperty("id")
	private String id;
	@JsonProperty("uploaded")
	private String uploaded;
	@JsonProperty("updated")
	private String updated;
	@JsonProperty("uploader")
	private String uploader;
	@JsonProperty("category")
	private String category;
	@JsonProperty("title")
	private String title;
	@JsonProperty("description")
	private String description;
	@JsonProperty("thumbnail")
	private Thumbnail thumbnail;
	@JsonProperty("player")
	private Player player;
	@JsonProperty("content")
	private Content content;
	@JsonProperty("duration")
	private Integer duration;
	@JsonProperty("aspectRatio")
	private String aspectRatio;
	@JsonProperty("rating")
	private Double rating;
	@JsonProperty("likeCount")
	private String likeCount;
	@JsonProperty("ratingCount")
	private Integer ratingCount;
	@JsonProperty("viewCount")
	private Integer viewCount;
	@JsonProperty("favoriteCount")
	private Integer favoriteCount;
	@JsonProperty("commentCount")
	private Integer commentCount;
	@JsonProperty("accessControl")
	private AccessControl accessControl;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("uploaded")
	public String getUploaded() {
		return uploaded;
	}

	@JsonProperty("uploaded")
	public void setUploaded(String uploaded) {
		this.uploaded = uploaded;
	}

	@JsonProperty("updated")
	public String getUpdated() {
		return updated;
	}

	@JsonProperty("updated")
	public void setUpdated(String updated) {
		this.updated = updated;
	}

	@JsonProperty("uploader")
	public String getUploader() {
		return uploader;
	}

	@JsonProperty("uploader")
	public void setUploader(String uploader) {
		this.uploader = uploader;
	}

	@JsonProperty("category")
	public String getCategory() {
		return category;
	}

	@JsonProperty("category")
	public void setCategory(String category) {
		this.category = category;
	}

	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("thumbnail")
	public Thumbnail getThumbnail() {
		return thumbnail;
	}

	@JsonProperty("thumbnail")
	public void setThumbnail(Thumbnail thumbnail) {
		this.thumbnail = thumbnail;
	}

	@JsonProperty("player")
	public Player getPlayer() {
		return player;
	}

	@JsonProperty("player")
	public void setPlayer(Player player) {
		this.player = player;
	}

	@JsonProperty("content")
	public Content getContent() {
		return content;
	}

	@JsonProperty("content")
	public void setContent(Content content) {
		this.content = content;
	}

	@JsonProperty("duration")
	public Integer getDuration() {
		return duration;
	}

	@JsonProperty("duration")
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	@JsonProperty("aspectRatio")
	public String getAspectRatio() {
		return aspectRatio;
	}

	@JsonProperty("aspectRatio")
	public void setAspectRatio(String aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	@JsonProperty("rating")
	public Double getRating() {
		return rating;
	}

	@JsonProperty("rating")
	public void setRating(Double rating) {
		this.rating = rating;
	}

	@JsonProperty("likeCount")
	public String getLikeCount() {
		return likeCount;
	}

	@JsonProperty("likeCount")
	public void setLikeCount(String likeCount) {
		this.likeCount = likeCount;
	}

	@JsonProperty("ratingCount")
	public Integer getRatingCount() {
		return ratingCount;
	}

	@JsonProperty("ratingCount")
	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;
	}

	@JsonProperty("viewCount")
	public Integer getViewCount() {
		return viewCount;
	}

	@JsonProperty("viewCount")
	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}

	@JsonProperty("favoriteCount")
	public Integer getFavoriteCount() {
		return favoriteCount;
	}

	@JsonProperty("favoriteCount")
	public void setFavoriteCount(Integer favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	@JsonProperty("commentCount")
	public Integer getCommentCount() {
		return commentCount;
	}

	@JsonProperty("commentCount")
	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	@JsonProperty("accessControl")
	public AccessControl getAccessControl() {
		return accessControl;
	}

	@JsonProperty("accessControl")
	public void setAccessControl(AccessControl accessControl) {
		this.accessControl = accessControl;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperties(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
	
	/**
     * Returns the video's duration in a readable string format
     * @return the video's duration in a readable string format
     */
    public String getReadableDuration() {
        return IRCUtils.toReadableMinutes(getDuration());
    }

}