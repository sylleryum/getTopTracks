package com.spotifyapi.demo.helper;

import com.spotifyapi.demo.entity.albumTracks.AlbumTracksItem;
import com.spotifyapi.demo.entity.albumTracksPopularity.TrackPopularity;
import com.spotifyapi.demo.entity.artistTopTracks.TopArtistTracks;
import com.spotifyapi.demo.entity.searchTrack.TrackItem;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TracksUtilImpl implements TracksUtil {


    private static YoutubeUtil youtubeUtil;
    Pattern specialPattern;
    private static Pattern youtubePattern;
    private Matcher matcher;


    public TracksUtilImpl(YoutubeUtil youtubeUtil) {
        this.youtubeUtil = youtubeUtil;
        //static Pattern specialPattern = Pattern.compile("(?i)[(\bhq|lyrics|BY\b)!@#$%¨&*()_\\-+={\\[}\\]º|\\\\,.:;?°]");
        //static Pattern specialPattern = Pattern.compile("(?i)\\blyrics|inc\\b[!@#$%¨&*()_\\-+={\\[}\\]º|\\\\,.:;?°]");
        //=============
        //**special with only english characters
        // specialPattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        //**youtube with only english characters
        //youtubePattern = Pattern.compile("[^a-z0-9 ]|\\blyrics|hd|hq|lyric video|with lyrics|w/|video|official music\\b", Pattern.CASE_INSENSITIVE);

        specialPattern = Pattern.compile("[\\u201C\\u201D\\u201E\\u201F\\u2033\\u2036\"!@#$%¨*&()_\\-+={\\[}\\]º|\\\\,.:;?°]", Pattern.CASE_INSENSITIVE);
        youtubePattern = Pattern.compile("[\\u201C\\u201D\\u201E\\u201F\\u2033\\u2036\"!@#$%¨*&()_\\-+={\\[}\\]º|\\\\,.:;?°]|\\blyrics|hd|hq|lyric video|with lyrics|w/|video|official|official music\\b", Pattern.CASE_INSENSITIVE);


    }

    static public String matchSong(TrackItem track) {
        return null;
    }

    /**
     * remove any additional information after the '-' of a track name
     *
     * @param trackName e.g. paranoid android - remastered
     * @return e.g. paranoid android
     */
    @Override
    public String clearRemastered(String trackName) {

        if (trackName.contains("-")) {
            String a = trackName.substring(0, trackName.indexOf("-") - 1);
            return a;
        }
        return null;
    }


    @Override
    @Deprecated
    public String clearSpecialCharSong(String trackName) {
        String sTrim = trackName.trim();
        if (sTrim.substring(0, 4).equalsIgnoreCase("http")) {
            System.out.println("youtube");
            return clearYoutubeChar(youtubeUtil.getSongName(sTrim));
        } else {
            //System.out.println("song name");
            //return clearSpecialChar(trackName);
            return clearSpecialChar(trackName);
        }

    }

    @Override
    public List<String> clearSpecialCharSongList(List<String> songList) {
        List<String> theReturn = new ArrayList<>();
        for (String s : songList) {
            theReturn.add(clearSpecialCharSong(s));
        }
        return theReturn;
    }

    @Override
    public String clearYoutubeChar(String trackName) {
        matcher = youtubePattern.matcher(trackName);
        return matcher.replaceAll("");
    }

    @Override
    public String clearSpecialChar(String trackName) {
        matcher = specialPattern.matcher(trackName);
        return addPlus(matcher.replaceAll(""));
    }

    @Override
    public String addPlus(String searchItem){
        return searchItem.replace(" ", "+");
    }

    @Override
    public String getYoutubeId(String url) {

        MultiValueMap<String, String> parameters =
                UriComponentsBuilder.fromUriString(url).build().getQueryParams();
        String v;

        try {
            return parameters.get("v").get(0);
        } catch (Exception e) {

            String[] ss =  url.split("be/");
            String ok;
            if (ss.length>1){
                return ss[1];
            }
            System.out.println("unable to get youtube ID " + url);
            //e.printStackTrace();
            return null;
        }

    }

//    @Override
//    public List<String> getTopPopularityAlbum(List<AlbumTracksItem> listTopAlbumTracks, int amountTracks) {
//
//        // List<User> sortedList = users.stream()
//        //			.sorted(Comparator.comparingInt(User::getAge))
//        //			.collect(Collectors.toList());
//        //List<TopArtistTracks> topArtistTracks = listTopArtistTracks.stream().sorted(Comparator.comparingInt(i -> i.getPopularity())).collect(Collectors.toList());
//        //List<TopArtistTracks> topArtistTracks = listTopAlbumTracks.stream().sorted((o1, o2) ->  o1.getPopularity() > o2.getPopularity()  ? -1 : 0).collect(Collectors.toList());
//        System.out.println();
//        //Collections.reverse(topArtistTracks);
//        return null;
//    }

    /**
     *
     * @param listTopArtistTracks
     * @param amountTracks
     * @return clear list with number of tracks wanted as a List of String, ready to be added
     */
    @Override

    public Map<Integer, List<String>> clearTopPopularityArtist(List<TopArtistTracks> listTopArtistTracks, int amountTracks){

        List<String> topArtistTracksUri = listTopArtistTracks.stream().map(i->i.getUri()).limit(amountTracks).collect(Collectors.toList());
        List<String> topArtistTracksName = listTopArtistTracks.stream().map(i->i.getArtists().get(0).getName()+" - "+i.getName()).limit(amountTracks).collect(Collectors.toList());

        Map<Integer, List<String>> mapReturn = new HashMap<>();
        mapReturn.put(0, topArtistTracksUri);
        mapReturn.put(1, topArtistTracksName);

        return mapReturn;
    }

    /**
     * after obtained a List<TrackPopularity> from serviceApi.getTracksPopularity, this is used to get a list<String> in order of popularity
     * @param
     * @return key 0=list of URIs sorted from top popularity, key 1=list of artist - song name sorted from top popularity
     */
    @Override
    public Map<Integer, List<String>> clearTopPopularityAlbum(List<TrackPopularity> listTopAlbumTracks, int amountTracks) {

        //List<TrackPopularity> popListSorted = listTopAlbumTracks.stream().sorted(Comparator.comparingInt(i -> i.getPopularity())).collect(Collectors.toList());
        List<TrackPopularity> popListSorted = listTopAlbumTracks.stream().sorted((o1, o2) ->  o1.getPopularity() > o2.getPopularity()  ? -1 : 0).limit(amountTracks).collect(Collectors.toList());
        List<String> listName = popListSorted.stream().map(i-> i.getArtists().get(0).getName()+" - "+i.getName()).collect(Collectors.toList());
        List<String> listUri = popListSorted.stream().map(i->i.getUri()).collect(Collectors.toList());
        System.out.println();
        Map<Integer, List<String>> mapReturn = new HashMap<>();
        mapReturn.put(0, listUri);
        mapReturn.put(1, listName);
        return mapReturn;
    }

    /**
     * after obtained a List<AlbumTracksItem> from serviceApi.getAlbumTrackIds, this is used to convert the result to String
     * @param listTracks
     * @return a String, comma delimited with all of the IDs from listTracks
     */
    @Override
    public String albumTracksItemToString (List<AlbumTracksItem> listTracks){
        String res = listTracks.stream().map(i-> i.getId()).collect(Collectors.joining( "," ));
        return res;
    }


}