package us.rddt.IRCBot.Services.YouTube.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Class to deserialize a YouTube channel object to.
 * @author Ryan Morrison
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "media$category",
    "media$content",
    "media$description",
    "media$keywords",
    "media$player",
    "media$thumbnail",
    "media$title",
    "yt$duration"
})
public class MediaGroup {

    @JsonProperty("media$category")
    private List<MediaCategory> mediaCategory = new ArrayList<MediaCategory>();
    @JsonProperty("media$content")
    private List<MediaContent> mediaContent = new ArrayList<MediaContent>();
    @JsonProperty("media$description")
    private MediaDescription mediaDescription;
    @JsonProperty("media$keywords")
    private MediaKeywords mediaKeywords;
    @JsonProperty("media$player")
    private List<MediaPlayer> mediaPlayer = new ArrayList<MediaPlayer>();
    @JsonProperty("media$thumbnail")
    private List<MediaThumbnail> mediaThumbnail = new ArrayList<MediaThumbnail>();
    @JsonProperty("media$title")
    private MediaTitle mediaTitle;
    @JsonProperty("yt$duration")
    private Duration duration;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("media$category")
    public List<MediaCategory> getMediaCategory() {
        return mediaCategory;
    }

    @JsonProperty("media$category")
    public void setMediaCategory(List<MediaCategory> mediaCategory) {
        this.mediaCategory = mediaCategory;
    }

    @JsonProperty("media$content")
    public List<MediaContent> getMediaContent() {
        return mediaContent;
    }

    @JsonProperty("media$content")
    public void setMediaContent(List<MediaContent> mediaContent) {
        this.mediaContent = mediaContent;
    }

    @JsonProperty("media$description")
    public MediaDescription getMediaDescription() {
        return mediaDescription;
    }

    @JsonProperty("media$description")
    public void setMediaDescription(MediaDescription mediaDescription) {
        this.mediaDescription = mediaDescription;
    }

    @JsonProperty("media$keywords")
    public MediaKeywords getMediaKeywords() {
        return mediaKeywords;
    }

    @JsonProperty("media$keywords")
    public void setMediaKeywords(MediaKeywords mediaKeywords) {
        this.mediaKeywords = mediaKeywords;
    }

    @JsonProperty("media$player")
    public List<MediaPlayer> getMediaPlayer() {
        return mediaPlayer;
    }

    @JsonProperty("media$player")
    public void setMediaPlayer(List<MediaPlayer> mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @JsonProperty("media$thumbnail")
    public List<MediaThumbnail> getMediaThumbnail() {
        return mediaThumbnail;
    }

    @JsonProperty("media$thumbnail")
    public void setMediaThumbnail(List<MediaThumbnail> mediaThumbnail) {
        this.mediaThumbnail = mediaThumbnail;
    }

    @JsonProperty("media$title")
    public MediaTitle getMediaTitle() {
        return mediaTitle;
    }

    @JsonProperty("media$title")
    public void setMediaTitle(MediaTitle mediaTitle) {
        this.mediaTitle = mediaTitle;
    }

    @JsonProperty("yt$duration")
    public Duration getDuration() {
        return duration;
    }

    @JsonProperty("yt$duration")
    public void setDuration(Duration duration) {
        this.duration = duration;
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
