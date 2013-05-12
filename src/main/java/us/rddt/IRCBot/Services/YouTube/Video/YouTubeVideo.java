package us.rddt.IRCBot.Services.YouTube.Video;

import java.util.HashMap;
import java.util.Map;

import us.rddt.IRCBot.Services.General.Downloadable;

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
	"apiVersion",
	"data"
})
public class YouTubeVideo extends Downloadable {

	@JsonProperty("apiVersion")
	private String apiVersion;
	@JsonProperty("data")
	private Data data;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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