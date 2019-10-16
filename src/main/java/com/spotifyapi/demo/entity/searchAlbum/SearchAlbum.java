
package com.spotifyapi.demo.entity.searchAlbum;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "albums"
})
public class SearchAlbum {

    @JsonProperty("albums")
    private Albums albums;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("albums")
    public Albums getAlbums() {
        return albums;
    }

    @JsonProperty("albums")
    public void setAlbums(Albums albums) {
        this.albums = albums;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "SearchAlbum{" +
                "albums=" + albums +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
