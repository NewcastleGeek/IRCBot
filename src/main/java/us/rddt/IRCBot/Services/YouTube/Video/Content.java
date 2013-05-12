package us.rddt.IRCBot.Services.YouTube.Video;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"5",
	"1",
	"6"
})
public class Content {

	@JsonProperty("5")
	private String _5;
	@JsonProperty("1")
	private String _1;
	@JsonProperty("6")
	private String _6;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("5")
	public String get5() {
		return _5;
	}

	@JsonProperty("5")
	public void set5(String _5) {
		this._5 = _5;
	}

	@JsonProperty("1")
	public String get1() {
		return _1;
	}

	@JsonProperty("1")
	public void set1(String _1) {
		this._1 = _1;
	}

	@JsonProperty("6")
	public String get6() {
		return _6;
	}

	@JsonProperty("6")
	public void set6(String _6) {
		this._6 = _6;
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