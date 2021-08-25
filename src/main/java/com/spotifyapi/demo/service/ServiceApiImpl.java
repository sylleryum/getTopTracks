package com.spotifyapi.demo.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotifyapi.demo.entity.albumTracks.AlbumTracksItem;
import com.spotifyapi.demo.entity.albumTracksPopularity.AlbumTracksPopularity;
import com.spotifyapi.demo.entity.albumTracksPopularity.TrackPopularity;
import com.spotifyapi.demo.entity.artistTopTracks.ArtistTopTracks;
import com.spotifyapi.demo.entity.artistTopTracks.TopArtistTracks;
import com.spotifyapi.demo.entity.searchAlbum.AlbumItem;
import com.spotifyapi.demo.entity.searchAlbum.SearchAlbum;
import com.spotifyapi.demo.entity.searchArtist.ArtistItem;
import com.spotifyapi.demo.entity.searchTrack.SearchTrack;
import com.spotifyapi.demo.entity.searchTrack.TrackItem;
import com.spotifyapi.demo.entity.albumTracks.AlbumTracks;
import com.spotifyapi.demo.entity.playlists.PlaylistItem;
import com.spotifyapi.demo.entity.*;
import com.spotifyapi.demo.entity.searchArtist.SearchArtist;
import com.spotifyapi.demo.entity.playlists.PlaylistList;
import com.spotifyapi.demo.entity.youtube.Youtube;
import com.spotifyapi.demo.helper.LoggingRequestInterceptor;
import com.spotifyapi.demo.helper.TracksUtil;
import com.spotifyapi.demo.helper.YoutubeUtil;
import com.spotifyapi.demo.entity.user.User;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Scope("session")
public class ServiceApiImpl implements ServiceApi {


    private AccessToken accessToken;
    private RestTemplate template = new RestTemplate();
    private HttpHeaders headers = new HttpHeaders();
    private MultiValueMap<String, String> bodyParameters = new LinkedMultiValueMap<>();
    private User user;
    private Map<Short, Map<String, String>> mapRym;


    TracksUtil tracksUtil;
    YoutubeUtil youtubeUtil;
    HttpEntity<MultiValueMap<String, String>> requestEntity;
    ObjectMapper objectMapper;
//    @Autowired
//    SongTitleUtilImpl songMatcher;


    public ServiceApiImpl(TracksUtil tracksUtil, YoutubeUtil youtubeUtil) {
        this.tracksUtil = tracksUtil;
        this.youtubeUtil = youtubeUtil;
        objectMapper = new ObjectMapper();
    }


    @Override
    public AccessToken getAccessToken(String theCode) {
        cleaner();

        System.out.println("new token request");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", ServiceApi.AUTHORIZATION_TO_ACCESS);

        bodyParameters.add("grant_type", "authorization_code");
        bodyParameters.add("code", theCode);
        bodyParameters.add("redirect_uri", REDIRECT_URL);
        requestEntity = new HttpEntity<>(bodyParameters, headers);


        return tokenCall(requestEntity);
    }

    /**
     * clean variables and check for a valid access token, if token is valid, only clean, if expired, request a refresh one
     *
     * @return AccessToken if it was expired
     */
    @Override
    public AccessToken beforeCall() {
        cleaner();

        if (accessToken == null) {
            System.out.println("I has no token hurdurrr");
            return null;
        } else if (accessToken.getValidity() < System.currentTimeMillis()) {
            System.out.println("refreshing token");
            headers.add("Authorization", ServiceApi.AUTHORIZATION_TO_ACCESS);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            bodyParameters.add("grant_type", "refresh_token");
            bodyParameters.add("refresh_token", accessToken.getRefreshToken());
            requestEntity = new HttpEntity<>(bodyParameters, headers);
            return tokenCall(requestEntity);
        } else {
            return accessToken;
        }

    }

    /**
     * get users details and set the current user in the service
     *
     * @return User
     */
    @Override
    public User getUserDetails() {
        if (beforeCall() == null) return null;
        User u = callApiGet(ServiceApi.USER_DETAILS, User.class);
        return u;
    }

    @Override
    public PlaylistItem createPlaylist(String playlistName) {
        if (beforeCall() == null || playlistName == null) return null;
        if (user == null) user = getUserDetails();
        String URL_PLAYLIST = "https://api.spotify.com/v1/users/" + user.getId() + "/playlists";

        headers.setContentType(MediaType.APPLICATION_JSON);

        CreatePlaylist createPlaylist = new CreatePlaylist(playlistName);
        HttpEntity<CreatePlaylist> request = new HttpEntity<>(createPlaylist, headers);

        try {
            PlaylistItem result = template.postForObject(URL_PLAYLIST, request, PlaylistItem.class);
            System.out.println("playlist created " + result);
            return result;
        } catch (RestClientResponseException e) {
            System.out.println(e.getMessage());
            System.out.println("Fail " + e.getResponseBodyAsString());
            return null;
        }
    }

    /**
     * get playlists owned by user
     *
     * @return
     */
    @Override
    public List<PlaylistItem> getPlaylists() {
        if (beforeCall() == null) return null;
        if (user == null) user = getUserDetails();
        String URL_PLAYLIST = "https://api.spotify.com/v1/users/" + user.getId() + "/playlists";


        try {
            HttpEntity<String> headParameters = new HttpEntity<>("paramenters", headers);
            ResponseEntity<PlaylistList> response = template.exchange(URL_PLAYLIST, HttpMethod.GET, headParameters, PlaylistList.class);

            List<PlaylistItem> listReturn = new ArrayList<>();
            for (PlaylistItem item : response.getBody().getItems()) {
                if (item.getOwner().getId().equalsIgnoreCase(user.getId())) {
                    listReturn.add(item);
                }
            }

            return listReturn;
        } catch (RestClientResponseException e) {
            System.out.println(e.getMessage());
            System.out.println("Fail " + e.getResponseBodyAsString());
            return null;
        }

    }

    /**
     * Search for track, artist or album, searchAlbum or searchArtist is advised to be used instead
     *
     * @param paramToFind
     * @param searchType  SEARCH_TRACK, SEARCH_ARTIST or SEARCH_ALBUM
     * @return
     */
    @Override
    public <T> T search(String paramToFind, short searchType) {
        if (beforeCall() == null || paramToFind.isEmpty()) {
            return null;
        }
        if (user == null) user = getUserDetails();

        //variable for the search
        String theString = paramToFind;
        String apiSearchType;

        //if track
        if (searchType == ServiceApi.search_SEARCH_TRACK) {
            theString = tracksUtil.clearSpecialChar(paramToFind);
            apiSearchType = "track";
        } else if (searchType == ServiceApi.search_SEARCH_ARTIST) {
            apiSearchType = "artist";
            theString = tracksUtil.addPlus(theString);
        } else {
            apiSearchType = "album";
            String toSearch = getClearSongName(theString);
            theString = tracksUtil.addPlus(toSearch);

        }

        /////////////trying to get details of the eventual nullpointer exception
        try {
            headers.add("Authorization", "Bearer " + accessToken.getAccessToken());
        } catch (Exception e) {
            System.out.println("**********access token null");
            if (accessToken != null) {
                System.out.println("access token is not null: " + accessToken);
            } else {
                System.out.println("access token is null");
            }

        }


        ////////////////////headers.add("Authorization", "Bearer " + accessToken.getAccessToken());
        HttpEntity<?> entity = new HttpEntity<>(headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ServiceApi.SEARCH)
                .queryParam("type", apiSearchType)

                .queryParam("market", user.getCountry())
                .queryParam("q", theString);

        try {
            ///////////HttpEntity<SearchResult> response = template.exchange(builder.toUriString(), HttpMethod.GET, entity, SearchResult.class);
            HttpEntity<String> response;
            T result;
            JavaType javaType;
            ///JavaType javaType = objectMapper.getTypeFactory().constructType(objectClass);
            if (searchType == ServiceApi.search_SEARCH_TRACK) {
                response = template.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
                javaType = objectMapper.getTypeFactory().constructType(SearchTrack.class);
                result = objectMapper.readValue(response.getBody(), javaType);
            } else if (searchType == ServiceApi.search_SEARCH_ARTIST) {
                response = template.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
                javaType = objectMapper.getTypeFactory().constructType(SearchArtist.class);
                result = objectMapper.readValue(response.getBody(), javaType);
            } else {
                response = template.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
                javaType = objectMapper.getTypeFactory().constructType(SearchAlbum.class);
                result = objectMapper.readValue(response.getBody(), javaType);
            }


            return result;

        } catch (RestClientResponseException e) {
            //System.out.println(e.getMessage());
            System.out.println("*******Fail to find spotify track, error " + e.getResponseBodyAsString() + " paramToFind" + paramToFind);
            return null;//new TrackItem(paramToFind);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("*******other fail to find spotify track " + paramToFind);
            return null;//new TrackItem(paramToFind);
        }
    }

    @Override
    public AlbumItem searchAlbum(String albumToFind) {
        SearchAlbum searchAlbum = search(albumToFind, ServiceApi.search_SEARCH_ALBUM);
        System.out.println();
        try {
            return searchAlbum.getAlbums().getItems().get(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("não encontrado " + albumToFind);
            return new AlbumItem(albumToFind);
        } catch (NullPointerException e) {
            System.out.println("não encontrado NULL " + albumToFind);
            return new AlbumItem(albumToFind);
        }
    }

    @Override
    public ArtistItem searchArtist(String artistToFind) {
        SearchArtist searchArtist = search(artistToFind, ServiceApi.search_SEARCH_ARTIST);
        System.out.println();
        try {
            return searchArtist.getArtists().getItems().get(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("não encontrado OutOfBound " + artistToFind);
            return new ArtistItem(artistToFind);
        } catch (Exception e) {
            System.out.println("não encontrado NULL/other " + artistToFind);
            return new ArtistItem(artistToFind);
        }
    }

    @Override
    public TrackItem searchTrack(String trackToFind) {
        SearchTrack i = search(trackToFind, ServiceApi.search_SEARCH_TRACK);
        System.out.println();
        try {
            TrackItem track = i.getTracks().getItems().get(0);
            return track;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("não encontrado OutOfBound " + trackToFind);
            return new TrackItem(trackToFind);
        } catch (NullPointerException e) {
            System.out.println("não encontrado NULL " + trackToFind);
            return new TrackItem(trackToFind);
        }
    }

    @Override
    public List<AlbumTracksItem> getAlbumTrackIds(String albumId) {
        if (beforeCall() == null) return null;
        String URL_GET_ALBUM = "https://api.spotify.com/v1/albums/" + albumId + "/tracks";

        AlbumTracks a = callApiGet(URL_GET_ALBUM, AlbumTracks.class);

        if (a != null) {
            //List<String> listIds = a.getAlbumTracksItems().stream().map(i -> i.getId()).collect(Collectors.toList());
            return a.getAlbumTracksItems();
        } else {
            System.out.println("incorrect albumId");
            return new ArrayList<>();
        }


    }

    @Override
    public List<TopArtistTracks> getArtistTopTracks(String artistId) {
        if (beforeCall() == null) return null;
        if (user == null) user = getUserDetails();
        String URL_GET_TOP_TRACKS = "https://api.spotify.com/v1/artists/" + artistId + "/top-tracks?country=" + user.getCountry();
        ArtistTopTracks a = callApiGet(URL_GET_TOP_TRACKS, ArtistTopTracks.class);

        if (a != null) {
            return a.getTracks();
        } else {
            System.out.println("***YOU SHOULDN'T BE HERE, CHECK RESULT OF falseReturn");
            TopArtistTracks falseReturn = new TopArtistTracks(artistId);
            return Arrays.asList(falseReturn);
        }


    }

    @Override
    public Boolean addTracks(Uri uris, String playlistID) {
        if (beforeCall() == null || playlistID.isEmpty()) {
            return null;
        }
        if (uris.getUris().size() < 1) {
            return false;
        }

        String URL = "https://api.spotify.com/v1/playlists/" + playlistID + "/tracks";
        HttpEntity<Uri> request = new HttpEntity<>(uris, headers);

        try {
            ///////////////String s = template.postForObject(URL, requestEntity, String.class);
            String string = template.postForObject(URL, request, String.class);
            System.out.println("Tracks added " + string);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            //System.out.println("Fail- " + e.getResponseBodyAsString());
            return false;
        }
    }

//    @Override
//    public Map<Integer, Map<Boolean, List<String>>> submitAddAllTracks(List<String> albums, List<String> artist, String playlistID, int amountTracks) {
//
//
//        return null;
//    }
//
//    @Override
//    public Map<Integer, Map<Boolean, List<String>>> submitAddAllTracks(List<String> albums, List<String> artists, String playlistID, int amount) {
//        return submitAddAllTracks(albums, artists, playlistID, amount, false);
//    }

    /**
     * Main method for searching and adding tracks
     *
     * @param albums
     * @param artists
     * @param playlistID
     * @param amountTracks
     * @return outer map key 0=list of Albums;
     * outer map key 1=list of artists;
     * inner key true=success, false=fail
     */
    @Override
    public Map<Integer, Map<Boolean, List<String>>> submitAddAllTracks(List<String> albums, List<String> artists, String playlistID, int amountTracks, boolean rym) {
        if (beforeCall() == null) {
            return null;
        }

        Map<Integer, Map<Boolean, List<String>>> mapReturn = new HashMap<>();
        List<String> listToAddAlbum = new ArrayList<>();

        if (albums != null) {
            Map<Boolean, List<String>> mapInternalAlbum = new HashMap<>();
            Map<Boolean, List<AlbumItem>> mapSearchAlbum = new HashMap<>();
            List<String> cleanIdList;
            List<String> failedAlbum;
            //find album != rym
            if (!rym){
                mapSearchAlbum = albums.stream().map(e -> searchAlbum(e)).collect(Collectors.partitioningBy(it -> it.getId() != null));
                cleanIdList = mapSearchAlbum.get(true).stream().map(AlbumItem::getId).collect(Collectors.toList());
                failedAlbum = mapSearchAlbum.get(false).stream().map(AlbumItem::getName).collect(Collectors.toList());
                mapInternalAlbum.put(false, failedAlbum);
            } else {
                cleanIdList = albums.stream().map(i-> mapRym.get(ServiceApi.getRYM_SEARCH_ALBUM).get(i)).collect(Collectors.toList());
            }
            System.out.println();

            //get track ids
            List<List<AlbumTracksItem>> listAlbumTracks = cleanIdList.stream().map(i -> getAlbumTrackIds(i)).collect(Collectors.toList());
            //convert ids to list<string> (each string = ids of an album)
            List<String> listIds = listAlbumTracks.stream().map(i -> i.stream().map(l1 -> l1.getId()).collect(Collectors.joining(","))).collect(Collectors.toList());
            //get popularity of the ids (from album)
            List<List<TrackPopularity>> listPopularity = listIds.stream().map(i -> getTracksPopularity(i)).collect(Collectors.toList());
            //sort popularity of the ids (from album) and set amount
            List<Map<Integer, List<String>>> listAlbumPopularitySorted = listPopularity.stream().map(i -> tracksUtil.clearTopPopularityAlbum(i, amountTracks)).collect(Collectors.toList());
            //joining cleared
            listToAddAlbum = listAlbumPopularitySorted.stream().flatMap(i -> i.get(0).stream()).collect(Collectors.toList());

            if (addTracks(new Uri(listToAddAlbum), playlistID)) {
                mapInternalAlbum.put(true, listAlbumPopularitySorted.stream().flatMap(i -> i.get(1).stream()).collect(Collectors.toList()));
            } else {
                mapInternalAlbum.put(true, null);
            }

            mapReturn.put(0, mapInternalAlbum);
        } else {
            mapReturn.put(0, null);
        }

        if (artists != null) {
            Map<Boolean, List<String>> mapInternalArtist = new HashMap<>();
            Map<Boolean, List<ArtistItem>> mapSearchArtist = new HashMap<>();
            List<String> cleanIdList;
            List<String> failedArtist;

            if (!rym){
//                mapSearchAlbum = albums.stream().map(e -> searchAlbum(e)).collect(Collectors.partitioningBy(it -> it.getId() != null));
//                cleanIdList = mapSearchAlbum.get(true).stream().map(AlbumItem::getId).collect(Collectors.toList());
//                failedAlbum = mapSearchAlbum.get(false).stream().map(AlbumItem::getName).collect(Collectors.toList());
//                mapInternalAlbum.put(false, failedAlbum);

                mapSearchArtist = artists.stream().map(e -> searchArtist(e)).collect(Collectors.partitioningBy(it -> it.getId() != null));
                cleanIdList = mapSearchArtist.get(true).stream().map(ArtistItem::getId).collect(Collectors.toList());
                failedArtist = mapSearchArtist.get(false).stream().map(ArtistItem::getName).collect(Collectors.toList());
                mapInternalArtist.put(false, failedArtist);
            } else {
                cleanIdList = artists.stream().map(i-> mapRym.get(ServiceApi.getRYM_SEARCH_ARTIST).get(i)).collect(Collectors.toList());
            }


            //get top tracks
            Map<Boolean, List<List<TopArtistTracks>>> mapTopArtistTracks = cleanIdList.stream().map(e -> getArtistTopTracks(e)).collect(Collectors.partitioningBy(it -> it.get(0).getUri() != null));
            //get uris and number of tracks selected
            List<String> finalListToAddAlbum = listToAddAlbum;
            List<Map<Integer, List<String>>> listClearedArtistTopTracks = mapTopArtistTracks.get(true).stream().map(e -> tracksUtil.clearTopPopularityArtist(e, finalListToAddAlbum, amountTracks)).collect(Collectors.toList());
            //joining cleared
            List<String> listToAddArtist = listClearedArtistTopTracks.stream().flatMap(i -> i.get(0).stream()).collect(Collectors.toList());

            if (addTracks(new Uri(listToAddArtist), playlistID)) {
                mapInternalArtist.put(true, listClearedArtistTopTracks.stream().flatMap(i -> i.get(1).stream()).collect(Collectors.toList()));
            } else {
                mapInternalArtist.put(true, null);
            }

            mapReturn.put(1, mapInternalArtist);
        } else {
            mapReturn.put(1, null);
        }
        System.out.println();

        return mapReturn;

    }

    /**
     * @param url either the song name or the youtube URL, if youtube URL, returns the name cleared
     * @return track name cleared and ready to be searched
     */
    @Override
    public String getClearSongName(String url) {
        cleaner(false);

        String sTrim = url.trim();
        if (sTrim.substring(0, 4).equalsIgnoreCase("http")) {
            //System.out.println("youtube");
            //return clearYoutubeChar(youtubeUtil.getSongName(sTrim));
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ServiceApi.GET_YOUTUBE_SONG_NAME)
                    .queryParam("part", "snippet")
                    .queryParam("key", ServiceApi.YOUTUBE_KEY)
                    .queryParam("id", tracksUtil.getYoutubeId(url));

            HttpEntity<?> entity = new HttpEntity<>(headers);

            HttpEntity<Youtube> response = template.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    Youtube.class);
            try {
                String name = tracksUtil.clearYoutubeChar(response.getBody().getItems().get(0).getSnippet().getTitle());
                return tracksUtil.clearSpecialChar(name);
            } catch (RestClientResponseException e) {
                //System.out.println(e.getMessage());
                System.out.println("Fail to get youtube song - " + e.getResponseBodyAsString() + " " + url);
                return "\b" + url;
            } catch (Exception e) {
                System.out.println("other small");
                return "\b" + url;
            }

        } else {
            return url;
        }

    }

    @Override
    public List<TrackPopularity> getTracksPopularity(String trackIds) {
        if (beforeCall() == null) return null;
        if (user == null) user = getUserDetails();

        String concatUrl = ServiceApi.GET_SEVERAL_TRACKS + "?ids=" + trackIds + "&market=" + user.getCountry();
        AlbumTracksPopularity a = callApiGet(concatUrl, AlbumTracksPopularity.class);
        System.out.println();

        return a.getTracks();
    }

    /**
     * gets Spotify songs from given RYM top releases page (I.E.:https://rateyourmusic.com/customchart?page=1&chart_type=top&type=album&year=alltime&genre_include=1&genres=Atmospheric+Black+Metal&include_child_genres=t&include=both&limit=none&countries=
     *
     * @param url
     * @param searchType    what to search:
     *                      ServiceApi.getRYM_SEARCH_BOTH = 1; (search the artists and the albums from the given page)
     *                      ServiceApi.getRYM_SEARCH_ARTIST = 2;
     *                      ServiceApi.getRYM_SEARCH_ALBUM = 3;
     * @param amountResults amount of results requested (maximum of 40)
     * @return a Map with keys of what was requested (2=artists, 3=albums, 2 and 3 for both), inner map has the album/artist name as key and id as value
     */
    @Override
    public Map<Short, List<String>> getRYM(String url, short searchType, int amountResults) {


        try {

            String urlProxy = "https://hyperbyte.net/";
            Connection.Response doc = Jsoup.connect(urlProxy).method(Connection.Method.GET).execute();
            Document responseDocument = doc.parse();
            Element potentialForm = responseDocument.select("form#hyperform1").first();
            Element search = responseDocument.selectFirst("input[name=url]");
            search.attr("value", url);
            FormElement form = (FormElement) potentialForm;

            Document searchResults = form.submit().cookies(doc.cookies()).post();
            Elements elements = searchResults.getElementsByClass("ui_stream_link_btn_spotify");
            //String e = elements.get(0).attributes().get("href");
//            String artist = elements.get(4).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString();
//            String album = elements.get(4).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(3).childNode(1).childNode(0).toString();
//            System.out.println("=== "+artist+" "+album);

//            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/70.0").followRedirects(true).timeout(100000).ignoreContentType(true).get();
//
//            Elements elements = doc.getElementsByClass("ui_stream_link_btn_spotify");

            Map<Short, List<String>> mapOuter = new HashMap<>();
            Map<String, String> mapInnerArtist = new HashMap<>();
            Map<String, String> mapInnerAlbum = new HashMap<>();

            Set<String> artistsSet = new HashSet<>();
            List<String> albums=new ArrayList<>();
            int index = amountResults>elements.size() ? elements.size() : amountResults;
            System.out.println();
            for (int i=0;i<index;i++){
                //int j = 0;
                if (searchType==ServiceApi.getRYM_SEARCH_ARTIST || searchType==ServiceApi.getRYM_SEARCH_BOTH) {
//                    do {
////                        if (elements.get(i).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString().
////                                equalsIgnoreCase(elements.get(j).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString())) {
////
////                            //List<AlbumTracksItem> artistIds = getAlbumTrackIds(elements.get(i).attributes().get("href").split("album/")[1]);
////                            //artists.add(elements.get(i).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString());
////                            artistsSet.add(elements.get(i).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString());
//////                            mapInnerArtist.put(elements.get(i).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString(),
//////                                    artistIds.get(0).getArtists().get(0).getId());
////                            break;
////                        }
//                        j++;
//                        artistsSet.add(elements.get(i).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString());
//                    } while (j <= albums.size());
                    artistsSet.add(elements.get(i).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString());
                }
                albums.add(elements.get(i).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(3).childNode(1).childNode(0).toString());
                //mapInnerAlbum.put(elements.get(i).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(3).childNode(1).childNode(0).toString()+" - "+elements.get(i).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString(), elements.get(i).attributes().get("href").split("album/")[1]);

//                artists.add(elements.get(i).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString());
//                albums.add(elements.get(i).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(3).childNode(1).childNode(0).toString()+" - "+elements.get(i).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString());
//                ids.add(elements.get(i).attributes().get("href").split("album/")[1]);
            }
            System.out.println();
            List<String> artists = new ArrayList<>();
            if (searchType==ServiceApi.getRYM_SEARCH_BOTH){
                artists.addAll(artistsSet);
                mapOuter.put(ServiceApi.getRYM_SEARCH_ARTIST, artists);
                mapOuter.put(ServiceApi.getRYM_SEARCH_ALBUM, albums);
                //mapRym = mapOuter;
                return mapOuter;
            } else if (searchType==ServiceApi.getRYM_SEARCH_ARTIST){
                artists.addAll(artistsSet);
                mapOuter.put(ServiceApi.getRYM_SEARCH_ARTIST, artists);
                //mapRym = mapOuter;
                return mapOuter;
            } else if (searchType==ServiceApi.getRYM_SEARCH_ALBUM){
                mapOuter.put(ServiceApi.getRYM_SEARCH_ALBUM, albums);
                //mapRym = mapOuter;
                return mapOuter;
            }
            System.out.println("you aren't supposed to be here");
            return null;
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("problem in Jsoup (problem with link? )");
            e.printStackTrace();
            return null;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    //=========================internal methods


    private AccessToken tokenCall(HttpEntity<MultiValueMap<String, String>> requestEntityCall) {

        try {
            ///////accessToken = template.postForObject(ServiceApi.GET_ACCESS, requestEntityCall, AccessToken.class);
            //template = new RestTemplate();
            this.accessToken = template.postForObject(ServiceApi.GET_ACCESS, requestEntityCall, AccessToken.class);
            this.accessToken.setValidity(3600000 + System.currentTimeMillis());
            System.out.println("Access acquired " + accessToken + " **" + accessToken.getValidity());

            return accessToken;
        } catch (RestClientResponseException e) {
            System.out.println(e.getMessage());
            System.out.println("Fail " + e.getResponseBodyAsString());
            accessToken = null;
            return null;
        }

    }

    @Override
    public boolean isAccessToken() {
        if (accessToken != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void test(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void testRefresh(String refresh) {
        this.accessToken.setRefreshToken(refresh);
    }

    @Override
    public RestTemplate interceptorRest() {
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    private void cleaner() {
        cleaner(true);
    }

    private void cleaner(Boolean addSpotifyHeader) {
        headers.clear();
        //objectMapper = new ObjectMapper();
        if (accessToken != null && addSpotifyHeader == true) {
            headers.add("Authorization", "Bearer " + accessToken.getAccessToken());
        }
        bodyParameters.clear();
    }

    public <T> T callApiGet(String Url, Class<T> objectClass) {
        cleaner();
        try {
            HttpEntity<String> headParameters = new HttpEntity<>("paramenters", headers);
            ResponseEntity<String> response = template.exchange(Url, HttpMethod.GET, headParameters, String.class);
            //System.out.println("user details " + response.getStatusCode() + " " + response.getBody());

            JavaType javaType = objectMapper.getTypeFactory().constructType(objectClass);
            T result;
            result = objectMapper.readValue(response.getBody(), javaType);
            System.out.println();
            return result;
        } catch (RestClientResponseException e) {
            System.out.println(e.getMessage());
            System.out.println("Fail " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void testMap(List<String> list) {

        try {
            System.out.println("====="+mapRym.get(ServiceApi.getRYM_SEARCH_ARTIST).get(list.get(0)));
            System.out.println();
        } catch (Exception e){

        }
    }
}
