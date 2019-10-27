package com.spotifyapi.demo.service;

import com.spotifyapi.demo.entity.albumTracks.AlbumTracks;
import com.spotifyapi.demo.entity.albumTracks.AlbumTracksItem;
import com.spotifyapi.demo.entity.albumTracksPopularity.TrackPopularity;
import com.spotifyapi.demo.entity.artistTopTracks.TopArtistTracks;
import com.spotifyapi.demo.entity.searchAlbum.AlbumItem;
import com.spotifyapi.demo.entity.searchArtist.ArtistItem;
import com.spotifyapi.demo.entity.searchTrack.TrackItem;
import com.spotifyapi.demo.entity.playlists.PlaylistItem;
import com.spotifyapi.demo.entity.*;
import com.spotifyapi.demo.entity.user.User;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public interface ServiceApi {
    String AUTHORIZE_URL = "https://accounts.spotify.com/authorize?client_id=be0f367a036e4d9485e09f0fe077eb19&response_type=code&redirect_uri=https%3A%2F%2Fgettoptracks.herokuapp.com%2Fcallback&scope=user-read-private%20user-read-email%20playlist-read-private%20playlist-modify-public%20playlist-modify-private";
    String REDIRECT_URL = "https://gettoptracks.herokuapp.com/callback";
    String GET_ACCESS = "https://accounts.spotify.com/api/token";
    String AUTHORIZATION_TO_ACCESS = "Basic YmUwZjM2N2EwMzZlNGQ5NDg1ZTA5ZjBmZTA3N2ViMTk6YWUwZmYyM2QxZTQ3NDRhZjhiN2IzMzQyMDA5OWRhMWU=";

//    String AUTHORIZE_URL = "https://accounts.spotify.com/authorize?client_id=be0f367a036e4d9485e09f0fe077eb19&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fcallback&scope=user-read-private%20user-read-email%20playlist-read-private%20playlist-modify-public%20playlist-modify-private";
//    String REDIRECT_URL = "http://localhost:8080/callback";
//    String GET_ACCESS = "https://accounts.spotify.com/api/token";
//    String AUTHORIZATION_TO_ACCESS = "Basic YmUwZjM2N2EwMzZlNGQ5NDg1ZTA5ZjBmZTA3N2ViMTk6YWUwZmYyM2QxZTQ3NDRhZjhiN2IzMzQyMDA5OWRhMWU=";
    String USER_DETAILS = "https://api.spotify.com/v1/me";
    String SEARCH = "https://api.spotify.com/v1/search";
    String GET_SEVERAL_TRACKS = "https://api.spotify.com/v1/tracks";
    String GET_YOUTUBE_SONG_NAME = "https://www.googleapis.com/youtube/v3/videos";
    String YOUTUBE_KEY = "AIzaSyCESltrZhcNxRodbjop8fMLhhtIfxD-_Wk";
    short getRYM_SEARCH_BOTH = 1;
    short getRYM_SEARCH_ARTIST = 2;
    short getRYM_SEARCH_ALBUM = 3;
    short search_SEARCH_TRACK = 1;
    short search_SEARCH_ARTIST = 2;
    short search_SEARCH_ALBUM = 3;


    AccessToken getAccessToken(String theCode);
    AccessToken beforeCall();
    User getUserDetails();
    PlaylistItem createPlaylist(String playlistName);
    List<PlaylistItem> getPlaylists();
    <T> T search(String paramToFind, short searchType);
    AlbumItem searchAlbum(String albumToFind);
    ArtistItem searchArtist(String artistToFind);
    List<AlbumTracksItem> getAlbumTrackIds(String albumId);
    List<TopArtistTracks> getArtistTopTracks(String artistId);
    TrackItem searchTrack(String trackTofind);
    Boolean addTracks(Uri uris, String playlistID);

    /////Map<Integer, Map<Boolean, List<String>>> submitAddAllTracks(Map<String, String> mapAlbums, Map<String, String> mapArtist, String playlistID, int amountTracks);
    Map<Integer, Map<Boolean, List<String>>> submitAddAllTracks(List<String> albums, List<String> artists, String playlistID, int amount, boolean rym);
    /////Map<Integer, Map<Boolean, List<String>>> submitAddAllTracks(List<String> albums, List<String> artists, String playlistID, int amount);
    String getClearSongName(String v);
    List<TrackPopularity> getTracksPopularity(String trackIds);
    Map<Short, List<String>> getRYM(String url, short searchType, int amountResults);

    boolean isAccessToken();
    void test(AccessToken accessToken);
    void testRefresh(String refresh);
    RestTemplate interceptorRest();
    void testMap(List<String> list);
}
