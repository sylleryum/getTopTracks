package com.spotifyapi.demo.helper;

import com.spotifyapi.demo.entity.albumTracks.AlbumTracksItem;
import com.spotifyapi.demo.entity.albumTracksPopularity.TrackPopularity;
import com.spotifyapi.demo.entity.artistTopTracks.TopArtistTracks;

import java.util.List;
import java.util.Map;

public interface TracksUtil {
    //for spotify songs - paranoid android - remastered
    String clearRemastered(String trackName);

    String clearSpecialCharSong(String trackName);
    List<String> clearSpecialCharSongList(List<String> songList);
    String clearYoutubeChar(String trackName);
    String addPlus(String searchItem);
    String getYoutubeId(String url);
    String clearSpecialChar(String trackName);
    //List<String> getTopPopularityAlbum(List<TrackPopularity> listTopAlbumTracks, int amountTracks);
    Map<Integer, List<String>> clearTopPopularityAlbum(List<TrackPopularity> listTopAlbumTracks, int amountTracks);
    Map<Integer, List<String>> clearTopPopularityArtist(List<TopArtistTracks> listTopArtistTracks, int amountTracks);
    String albumTracksItemToString(List<AlbumTracksItem> listTracks);
}
