package us.rddt.IRCBot.Services.YouTube.Video;

import java.util.HashMap;
import java.util.Map;
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
	"sqDefault",
	"hqDefault"
})
public class Thumbnail {

	@JsonProperty("sqDefault")
	private String sqDefault;
	@JsonProperty("hqDefault")
	private String hqDefault;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("sqDefault")
	public String getSqDefault() {
		return sqDefault;
	}

	@JsonProperty("sqDefault")
	public void setSqDefault(String sqDefault) {
		this.sqDefault = sqDefault;
	}

	@JsonProperty("hqDefault")
	public String getHqDefault() {
		return hqDefault;
	}

	@JsonProperty("hqDefault")
	public void setHqDefault(String hqDefault) {
		this.hqDefault = hqDefault;
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