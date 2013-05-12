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
    "xmlns",
    "xmlns$media",
    "xmlns$openSearch",
    "xmlns$gd",
    "xmlns$yt",
    "id",
    "updated",
    "category",
    "title",
    "logo",
    "link",
    "author",
    "generator",
    "openSearch$totalResults",
    "openSearch$startIndex",
    "openSearch$itemsPerPage",
    "entry"
})
public class Feed {

    @JsonProperty("xmlns")
    private String xmlns;
    @JsonProperty("xmlns$media")
    private String xmlns$media;
    @JsonProperty("xmlns$openSearch")
    private String xmlns$openSearch;
    @JsonProperty("xmlns$gd")
    private String xmlns$gd;
    @JsonProperty("xmlns$yt")
    private String xmlns$yt;
    @JsonProperty("id")
    private Value id;
    @JsonProperty("updated")
    private Value updated;
    @JsonProperty("category")
    private List<Category> category = new ArrayList<Category>();
    @JsonProperty("title")
    private Title title;
    @JsonProperty("logo")
    private Value logo;
    @JsonProperty("link")
    private List<Link> link = new ArrayList<Link>();
    @JsonProperty("author")
    private List<Author> author = new ArrayList<Author>();
    @JsonProperty("generator")
    private Generator generator;
    @JsonProperty("openSearch$totalResults")
    private Value openSearchTotalResults;
    @JsonProperty("openSearch$startIndex")
    private Value openSearchStartIndex;
    @JsonProperty("openSearch$itemsPerPage")
    private Value openSearchItemsPerPage;
    @JsonProperty("entry")
    private List<Entry> entry = new ArrayList<Entry>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("xmlns")
    public String getXmlns() {
        return xmlns;
    }

    @JsonProperty("xmlns")
    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    @JsonProperty("xmlns$media")
    public String getXmlns$media() {
        return xmlns$media;
    }

    @JsonProperty("xmlns$media")
    public void setXmlns$media(String xmlns$media) {
        this.xmlns$media = xmlns$media;
    }

    @JsonProperty("xmlns$openSearch")
    public String getXmlns$openSearch() {
        return xmlns$openSearch;
    }

    @JsonProperty("xmlns$openSearch")
    public void setXmlns$openSearch(String xmlns$openSearch) {
        this.xmlns$openSearch = xmlns$openSearch;
    }

    @JsonProperty("xmlns$gd")
    public String getXmlns$gd() {
        return xmlns$gd;
    }

    @JsonProperty("xmlns$gd")
    public void setXmlns$gd(String xmlns$gd) {
        this.xmlns$gd = xmlns$gd;
    }

    @JsonProperty("xmlns$yt")
    public String getXmlns$yt() {
        return xmlns$yt;
    }

    @JsonProperty("xmlns$yt")
    public void setXmlns$yt(String xmlns$yt) {
        this.xmlns$yt = xmlns$yt;
    }

    @JsonProperty("id")
    public Value getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Value id) {
        this.id = id;
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

    @JsonProperty("logo")
    public Value getLogo() {
        return logo;
    }

    @JsonProperty("logo")
    public void setLogo(Value logo) {
        this.logo = logo;
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

    @JsonProperty("generator")
    public Generator getGenerator() {
        return generator;
    }

    @JsonProperty("generator")
    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    @JsonProperty("openSearch$totalResults")
    public Value getOpenSearchTotalResults() {
        return openSearchTotalResults;
    }

    @JsonProperty("openSearch$totalResults")
    public void setOpenSearchTotalResults(Value openSearch$totalResults) {
        this.openSearchTotalResults = openSearch$totalResults;
    }

    @JsonProperty("openSearch$startIndex")
    public Value getOpenSearchStartIndex() {
        return openSearchStartIndex;
    }

    @JsonProperty("openSearch$startIndex")
    public void setOpenSearchStartIndex(Value openSearch$startIndex) {
        this.openSearchStartIndex = openSearch$startIndex;
    }

    @JsonProperty("openSearch$itemsPerPage")
    public Value getOpenSearchItemsPerPage() {
        return openSearchItemsPerPage;
    }

    @JsonProperty("openSearch$itemsPerPage")
    public void setOpenSearchItemsPerPage(Value openSearchItemsPerPage) {
        this.openSearchItemsPerPage = openSearchItemsPerPage;
    }

    @JsonProperty("entry")
    public List<Entry> getEntry() {
        return entry;
    }

    @JsonProperty("entry")
    public void setEntry(List<Entry> entry) {
        this.entry = entry;
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
