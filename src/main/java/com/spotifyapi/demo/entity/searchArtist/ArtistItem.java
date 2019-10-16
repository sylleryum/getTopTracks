
package com.spotifyapi.demo.entity.searchArtist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "external_urls",
    "followers",
    "genres",
    "href",
    "id",
    "images",
    "name",
    "popularity",
    "type",
    "uri"
})
public class ArtistItem {

    @JsonProperty("external_urls")
    private ExternalUrls externalUrls;
    @JsonProperty("followers")
    private Followers followers;
    @JsonProperty("genres")
    private List<String> genres = null;
    @JsonProperty("href")
    private String href;
    @JsonProperty("id")
    private String id;
    @JsonProperty("images")
    private List<Image> images = null;
    @JsonProperty("name")
    private String name;
    @JsonProperty("popularity")
    private Integer popularity;
    @JsonProperty("type")
    private String type;
    @JsonProperty("uri")
    private String uri;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public ArtistItem() {
    }

    public ArtistItem(String name) {
        this.name = name;
    }

    public ArtistItem(ExternalUrls externalUrls, Followers followers, List<String> genres, String href, String id, List<Image> images, String name, Integer popularity, String type, String uri, Map<String, Object> additionalProperties) {
        this.externalUrls = externalUrls;
        this.followers = followers;
        this.genres = genres;
        this.href = href;
        this.id = id;
        this.images = images;
        this.name = name;
        this.popularity = popularity;
        this.type = type;
        this.uri = uri;
        this.additionalProperties = additionalProperties;
    }

    @JsonProperty("external_urls")
    public ExternalUrls getExternalUrls() {
        return externalUrls;
    }

    @JsonProperty("external_urls")
    public void setExternalUrls(ExternalUrls externalUrls) {
        this.externalUrls = externalUrls;
    }

    @JsonProperty("followers")
    public Followers getFollowers() {
        return followers;
    }

    @JsonProperty("followers")
    public void setFollowers(Followers followers) {
        this.followers = followers;
    }

    @JsonProperty("genres")
    public List<String> getGenres() {
        return genres;
    }

    @JsonProperty("genres")
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    @JsonProperty("href")
    public String getHref() {
        return href;
    }

    @JsonProperty("href")
    public void setHref(String href) {
        this.href = href;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("images")
    public List<Image> getImages() {
        return images;
    }

    @JsonProperty("images")
    public void setImages(List<Image> images) {
        this.images = images;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("popularity")
    public Integer getPopularity() {
        return popularity;
    }

    @JsonProperty("popularity")
    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    @JsonProperty("uri")
    public void setUri(String uri) {
        this.uri = uri;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
