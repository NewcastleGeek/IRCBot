package us.rddt.IRCBot.Services.YouTube.Channel;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "favoriteCount",
    "viewCount"
})
public class Statistics {

    @JsonProperty("favoriteCount")
    private String favoriteCount;
    @JsonProperty("viewCount")
    private String viewCount;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("favoriteCount")
    public String getFavoriteCount() {
        return favoriteCount;
    }

    @JsonProperty("favoriteCount")
    public void setFavoriteCount(String favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    @JsonProperty("viewCount")
    public String getViewCount() {
        return viewCount;
    }

    @JsonProperty("viewCount")
    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
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
