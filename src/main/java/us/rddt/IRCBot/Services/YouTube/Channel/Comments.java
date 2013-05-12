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
    "gd$feedLink"
})
public class Comments {

    @JsonProperty("gd$feedLink")
    private FeedLink gd$feedLink;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("gd$feedLink")
    public FeedLink getGd$feedLink() {
        return gd$feedLink;
    }

    @JsonProperty("gd$feedLink")
    public void setGd$feedLink(FeedLink gd$feedLink) {
        this.gd$feedLink = gd$feedLink;
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
