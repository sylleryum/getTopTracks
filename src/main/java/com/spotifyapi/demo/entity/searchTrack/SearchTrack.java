
package com.spotifyapi.demo.entity.searchTrack;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "tracks"
})
public class SearchTrack {

    @JsonProperty("tracks")
    private Tracks tracks;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("tracks")
    public Tracks getTracks() {
        return tracks;
    }

    @JsonProperty("tracks")
    public void setTracks(Tracks tracks) {
        this.tracks = tracks;
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
        return "SearchResult{" +
                "tracks=" + tracks +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
