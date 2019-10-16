
package com.spotifyapi.demo.entity.searchTrack;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "spotify"
})
public class ExternalUrls__ {

    @JsonProperty("spotify")
    private String spotify;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("spotify")
    public String getSpotify() {
        return spotify;
    }

    @JsonProperty("spotify")
    public void setSpotify(String spotify) {
        this.spotify = spotify;
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
        return "ExternalUrls__{" +
                "spotify='" + spotify + '\'' +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
