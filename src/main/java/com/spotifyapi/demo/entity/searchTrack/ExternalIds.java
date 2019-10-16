
package com.spotifyapi.demo.entity.searchTrack;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "isrc"
})
public class ExternalIds {

    @JsonProperty("isrc")
    private String isrc;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("isrc")
    public String getIsrc() {
        return isrc;
    }

    @JsonProperty("isrc")
    public void setIsrc(String isrc) {
        this.isrc = isrc;
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
        return "ExternalIds{" +
                "isrc='" + isrc + '\'' +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
