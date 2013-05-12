package us.rddt.IRCBot.Services.YouTube.Channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@JsonPropertyOrder({
    "id",
    "published",
    "updated",
    "category",
    "title",
    "content",
    "link",
    "author",
    "gd$comments",
    "yt$hd",
    "media$group",
    "gd$rating",
    "yt$statistics"
})
public class Entry {

    @JsonProperty("id")
    private Value id;
    @JsonProperty("published")
    private Value published;
    @JsonProperty("updated")
    private Value updated;
    @JsonProperty("category")
    private List<Category> category = new ArrayList<Category>();
    @JsonProperty("title")
    private Title title;
    @JsonProperty("content")
    private Content content;
    @JsonProperty("link")
    private List<Link> link = new ArrayList<Link>();
    @JsonProperty("author")
    private List<Author> author = new ArrayList<Author>();
    @JsonProperty("gd$comments")
    private Comments comments;
    @JsonProperty("yt$hd")
    private HD hd;
    @JsonProperty("media$group")
    private MediaGroup mediaGroup;
    @JsonProperty("gd$rating")
    private Rating rating;
    @JsonProperty("yt$statistics")
    private Statistics statistics;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Value getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Value id) {
        this.id = id;
    }

    @JsonProperty("published")
    public Value getPublished() {
        return published;
    }

    @JsonProperty("published")
    public void setPublished(Value published) {
        this.published = published;
    }

    @JsonProperty("updated")
    public Value getUpdated() {
        return updated;
    }

    @JsonProperty("updated")
    public void setUpdated(Value updated) {
        this.updated = updated;
    }

    @JsonProperty("category")
    public List<Category> getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(List<Category> category) {
        this.category = category;
    }

    @JsonProperty("title")
    public Title getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(Title title) {
        this.title = title;
    }

    @JsonProperty("content")
    public Content getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(Content content) {
        this.content = content;
    }

    @JsonProperty("link")
    public List<Link> getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(List<Link> link) {
        this.link = link;
    }

    @JsonProperty("author")
    public List<Author> getAuthor() {
        return author;
    }

    @JsonProperty("author")
    public void setAuthor(List<Author> author) {
        this.author = author;
    }

    @JsonProperty("gd$comments")
    public Comments getComments() {
        return comments;
    }

    @JsonProperty("gd$comments")
    public void setComments(Comments gd$comments) {
        this.comments = gd$comments;
    }

    @JsonProperty("yt$hd")
    public HD getHD() {
        return hd;
    }

    @JsonProperty("yt$hd")
    public void setHD(HD yt$hd) {
        this.hd = yt$hd;
    }

    @JsonProperty("media$group")
    public MediaGroup getMediaGroup() {
        return mediaGroup;
    }

    @JsonProperty("media$group")
    public void setMediaGroup(MediaGroup media$group) {
        this.mediaGroup = media$group;
    }

    @JsonProperty("gd$rating")
    public Rating getRating() {
        return rating;
    }

    @JsonProperty("gd$rating")
    public void setRating(Rating gd$rating) {
        this.rating = gd$rating;
    }

    @JsonProperty("yt$statistics")
    public Statistics getStatistics() {
        return statistics;
    }

    @JsonProperty("yt$statistics")
    public void setStatistics(Statistics yt$statistics) {
        this.statistics = yt$statistics;
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
