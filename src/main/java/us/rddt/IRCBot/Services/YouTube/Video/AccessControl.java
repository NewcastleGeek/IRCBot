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
	"comment",
	"commentVote",
	"videoRespond",
	"rate",
	"embed",
	"list",
	"autoPlay",
	"syndicate"
})
public class AccessControl {
	
	@JsonProperty("comment")
	private String comment;
	@JsonProperty("commentVote")
	private String commentVote;
	@JsonProperty("videoRespond")
	private String videoRespond;
	@JsonProperty("rate")
	private String rate;
	@JsonProperty("embed")
	private String embed;
	@JsonProperty("list")
	private String list;
	@JsonProperty("autoPlay")
	private String autoPlay;
	@JsonProperty("syndicate")
	private String syndicate;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("comment")
	public String getComment() {
		return comment;
	}

	@JsonProperty("comment")
	public void setComment(String comment) {
		this.comment = comment;
	}

	@JsonProperty("commentVote")
	public String getCommentVote() {
		return commentVote;
	}

	@JsonProperty("commentVote")
	public void setCommentVote(String commentVote) {
		this.commentVote = commentVote;
	}

	@JsonProperty("videoRespond")
	public String getVideoRespond() {
		return videoRespond;
	}

	@JsonProperty("videoRespond")
	public void setVideoRespond(String videoRespond) {
		this.videoRespond = videoRespond;
	}

	@JsonProperty("rate")
	public String getRate() {
		return rate;
	}

	@JsonProperty("rate")
	public void setRate(String rate) {
		this.rate = rate;
	}

	@JsonProperty("embed")
	public String getEmbed() {
		return embed;
	}

	@JsonProperty("embed")
	public void setEmbed(String embed) {
		this.embed = embed;
	}

	@JsonProperty("list")
	public String getList() {
		return list;
	}

	@JsonProperty("list")
	public void setList(String list) {
		this.list = list;
	}

	@JsonProperty("autoPlay")
	public String getAutoPlay() {
		return autoPlay;
	}

	@JsonProperty("autoPlay")
	public void setAutoPlay(String autoPlay) {
		this.autoPlay = autoPlay;
	}

	@JsonProperty("syndicate")
	public String getSyndicate() {
		return syndicate;
	}

	@JsonProperty("syndicate")
	public void setSyndicate(String syndicate) {
		this.syndicate = syndicate;
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