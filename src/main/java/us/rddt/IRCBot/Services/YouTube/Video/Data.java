package us.rddt.IRCBot.Services.YouTube.Video;

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
 * Class to deserialize a YouTube video object to.
 * @author Ryan Morrison
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"updated",
	"totalItems",
	"startIndex",
	"itemsPerPage",
	"items"
})
public class Data {

	@JsonProperty("updated")
	private String updated;
	@JsonProperty("totalItems")
	private Integer totalItems;
	@JsonProperty("startIndex")
	private Integer startIndex;
	@JsonProperty("itemsPerPage")
	private Integer itemsPerPage;
	@JsonProperty("items")
	private List<Item> items = new ArrayList<Item>();
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("updated")
	public String getUpdated() {
		return updated;
	}

	@JsonProperty("updated")
	public void setUpdated(String updated) {
		this.updated = updated;
	}

	@JsonProperty("totalItems")
	public Integer getTotalItems() {
		return totalItems;
	}

	@JsonProperty("totalItems")
	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
	}

	@JsonProperty("startIndex")
	public Integer getStartIndex() {
		return startIndex;
	}

	@JsonProperty("startIndex")
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	@JsonProperty("itemsPerPage")
	public Integer getItemsPerPage() {
		return itemsPerPage;
	}

	@JsonProperty("itemsPerPage")
	public void setItemsPerPage(Integer itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	@JsonProperty("items")
	public List<Item> getItems() {
		return items;
	}

	@JsonProperty("items")
	public void setItems(List<Item> items) {
		this.items = items;
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