package com.spotifyapi.demo;

import com.spotifyapi.demo.entity.*;
import com.spotifyapi.demo.entity.albumTracks.AlbumTracks;
import com.spotifyapi.demo.entity.albumTracks.AlbumTracksItem;
import com.spotifyapi.demo.entity.albumTracksPopularity.TrackPopularity;
import com.spotifyapi.demo.entity.artistTopTracks.TopArtistTracks;
import com.spotifyapi.demo.entity.searchAlbum.AlbumItem;
import com.spotifyapi.demo.entity.searchArtist.ArtistItem;
import com.spotifyapi.demo.entity.searchTrack.TrackItem;
import com.spotifyapi.demo.entity.playlists.PlaylistItem;
import com.spotifyapi.demo.entity.user.User;
import com.spotifyapi.demo.helper.TracksUtil;
import com.spotifyapi.demo.helper.YoutubeUtil;
import com.spotifyapi.demo.service.ServiceApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spotifyapi.demo.service.ServiceApi.getRYM_SEARCH_BOTH;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
    String baseUrl = "https://accounts.spotify.com/api/token";
    String CODE = "AQDfvwVJkR2UuDIKfwRNAqxUKRoL9HAe_foyboL9t03cR_YGHqYdTmb8crpSmks93dvKQuft99IiUPbM-f2MpSz1wI4kZpJF0LUbQ38awFVwdjiyKHGKDWzk1X4F_8jFTGCKaDCcBwK_RSfUZfAyDshilkPBg582MT-S2GBaOagptElx64w0S4C8eyVFM9fXrDZWlFik2Tj9gQyUOxY2Wrk5gOXEOHkyYnmz67Ve3FZ2gXuiJFVc65PBFJcNj8hp4gdF6_wCv6dFZAE7bzOvC15u6_OhlFlt7LLIEOR-aZjWgVci3xo5_7i7c_bR0Cz_7S_YcxFya1cs1e3D6O1AFIHOwMO28aMLa7tEmkJb";
    String AUTHORIZATION = "Bearer BQB0Q5ThoEzmdqXwLH7q0_KoFNjw3Pgl80xFBa_PKIcQmk0S7d8AR9GkbLgiXbmjYC8tQYPQhmGQgn3Z3i8pXuvp15awKZy1pd214ogATe2eyNu1Z3UQhXMo6JOC5eDYsqkAzc6cg3T6YRMnlDD3w0BKbUn1MdKt8hm2QCO-P7s5ZEfakiqm85fPl_U8YWyiQBQbdVvxWjMKl2wYB3dc1XMoGMmJX6O-C8hVVu03dH0";

    @Autowired
    TracksUtil tracksUtil;
    @Autowired
    YoutubeUtil youtubeUtil;
    @Autowired
    ServiceApi serviceApi;

    String accessToken = "BQC4IxsBWrDWq_jfLDj6FFKioR2f4GQJ1z8_6C_lz1cT5NpOHBFZgj3y5tAAeB99hpZ2SnHxQCKfVKhnmfg_dBfyJ5gUTjQnPNtJfF3kl0N388Ty1szzGS4z8u2MMF-QeNAYk0asXzkwXvfrdBm8dR4qYK_cBWHFZDByKOHXS9-tg0iuCcIyWQ5UyAnLEZJZz98XFomQtsICrm5y8B4HVAIEBLNYZ3HGcN_Pmr9amixB2rFc9geZdkce_WsTBOSs5rrHDhurLJSE8vk";
    AccessToken accesstoken = new AccessToken(accessToken,
            3600000 + System.currentTimeMillis());

    @Test
    public void testVPN() {

        try {
//        File result = new File("results.html");
//        FileUtils.copyURLToFile(new URL("https://rateyourmusic.com/charts/top/album/2016"), result);
//
//        Document docFile = Jsoup.parse(result, "UTF-8", "http://example.com/");
//        Elements elementsFile = docFile.getElementsByClass("ui_stream_link_btn_spotify");
//        String artistFile = elementsFile.get(4).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString();
//        System.out.println("*** "+artistFile);
            System.out.println();
        serviceApi.getRYM("https://rateyourmusic.com/customchart?page=1&chart_type=top&type=album&year=alltime&genre_include=1&genres=Art+Rock&include_child_genres=t&include=both&limit=none&countries=", getRYM_SEARCH_BOTH, 5);


            //https://rateyourmusic.com/charts/top/album/2016
            String url = "https://hyperbyte.net/";
            Connection.Response doc = Jsoup.connect(url).method(Connection.Method.GET).execute();
            Document responseDocument = doc.parse();
            Element potentialForm = responseDocument.select("form#hyperform1").first();
            Element search = responseDocument.selectFirst("input[name=url]");
            search.attr("value", "https://rateyourmusic.com/charts/top/album/2016");
            FormElement form = (FormElement) potentialForm;

            Document searchResults = form.submit().cookies(doc.cookies()).post();
            Elements elements = searchResults.getElementsByClass("ui_stream_link_btn_spotify");
            //String e = elements.get(0).attributes().get("href");
            String artist = elements.get(4).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString();
            String album = elements.get(4).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(3).childNode(1).childNode(0).toString();
            System.out.println("=== "+artist+" "+album);
            System.out.println();
        }catch (Exception e){
            System.out.println("deu ruim");
            e.printStackTrace();
        }
    }

    @Test
    public void testGetRYM() {
        serviceApi.test(accesstoken);
        String tt = "https://open.spotify.com/album/277GP8d3KlBSQwMZJza6pe";
        serviceApi.getRYM("https://rateyourmusic.com/customchart?page=1&chart_type=top&type=album&year=alltime&genre_include=1&genres=Atmospheric+Black+Metal&include_child_genres=t&include=both&limit=none&countries=", getRYM_SEARCH_BOTH, 85);
        System.out.println();

        String url = "https://rateyourmusic.com/customchart?page=1&chart_type=top&type=album&year=alltime&genre_include=1&genres=Atmospheric+Black+Metal&include_child_genres=t&include=both&limit=none&countries=";
        Document doc = null;
//        String ID = getId(url);
//        return serviceApiYoutube.getClearSongName(url);

        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Elements elements = doc.getElementsByClass("ui_stream_link_btn_spotify");
            System.out.println();
            //System.out.println("Song name "+doc.getElementsByTag("h1"));
            String att = elements.get(2).attributes().get("href");
            String testArtist = elements.get(4).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(1).childNode(3).childNode(0).toString();
            String testAlbum = elements.get(4).parentNode().parentNode().parentNode().childNode(1).childNode(3).childNode(3).childNode(1).childNode(0).toString();
            System.out.println();
        } catch (Exception e) {
            System.out.println("not possible to get YOUTUBE songname " + url);
            e.printStackTrace();
        }

        List<AlbumTracksItem> test = serviceApi.getAlbumTrackIds("5OEz7YwAQyYvaSl1pmkPCI");
        test.get(0).getArtists().get(0).getId();
        System.out.println();
    }

    @Test
    public void testSubmitArtist() {
        serviceApi.test(accesstoken);

        //0 2 11
        List<String> testDuplicates = Arrays.asList("spotify:track:54qzcTSGvggVEa1pywUKdKç", "spotify:track:56UVGnWlFTCKOELqxVKwtQ", "spotify:track:1NifPbQ0M0K0zOsa2dNPHm");

        List<String> theList = Arrays.asList("spotify:track:54qzcTSGvggVEa1pywUKdK", "spotify:track:2ovwJqPsH7TuD5R1upfwEo", "spotify:track:6b2oQwSGFkzsMtQruIWm2p", "spotify:track:078BAiMFxiuQzUQrpwLcKz", "spotify:track:27rgTetikreqkvedaxrF5N");
        List<String> test = Arrays.asList("spotify:track:6b2oQwSGFkzsMtQruIWm2p", "oi2", "spotify:track:2ovwJqPsH7TuD5R1upfwEo", "oi4");
        List<String> result = theList.stream().filter(i -> !test.stream().anyMatch(in -> in.equalsIgnoreCase(i))).collect(Collectors.toList());

        System.out.println();

        List<String> listArtist = Arrays.asList("art zoyd", "radiohead", "demians", "huihiaehwwwoj", "elbow");
        //======artist top
        Map<Boolean, List<ArtistItem>> mapSearchArtist = listArtist.stream().map(e -> serviceApi.searchArtist(e)).collect(Collectors.partitioningBy(it -> it.getId() != null));
        System.out.println();
        Map<Boolean, List<List<TopArtistTracks>>> mapTopArtistTracks = mapSearchArtist.get(true).stream().map(e -> serviceApi.getArtistTopTracks(e.getId())).collect(Collectors.partitioningBy(it -> it.get(0).getUri() != null));
        System.out.println();
        List<Map<Integer, List<String>>> listClearedArtistTopTracks = mapTopArtistTracks.get(true).stream().map(e -> tracksUtil.clearTopPopularityArtist(e, 3)).collect(Collectors.toList());
        System.out.println();

        //failed (only getting search)
        List<String> failedResults = mapSearchArtist.get(false).stream().map(e -> e.getName()).collect(Collectors.toList());
        System.out.println();

        //joining cleared
        List<String> listToAdd = listClearedArtistTopTracks.stream().flatMap(i -> i.get(0).stream()).collect(Collectors.toList());
        System.out.println();

        List<Map<Integer, List<String>>> testDuplicatesResult = mapTopArtistTracks.get(true).stream().map(e -> tracksUtil.clearTopPopularityArtist(e, testDuplicates, 3)).collect(Collectors.toList());

        Boolean bResult = serviceApi.addTracks(new Uri(listToAdd), "6CRh3WU4Ygi2S2HPfLm8iP");
        System.out.println("added successfully");

//        ArtistItem i = serviceApi.searchArtist(s);
//        List<TopArtistTracks> ia = serviceApi.getArtistTopTracks(i.getId());
//        List<String> rr = tracksUtil.clearTopPopularityArtist(ia, 3);
//        System.out.println();
    }

    @Test
    public void testSubmitAlbum() {
        serviceApi.test(accesstoken);

        List<String> listAlbum = Arrays.asList("ok computer", "kid a", "pablo honey", "oiejfioo", "lost souls doves");

        //=================================================================================================================
        //=====album top
        //find album
        Map<Boolean, List<AlbumItem>> mapSearchAlbum = listAlbum.stream().map(e -> serviceApi.searchAlbum(e)).collect(Collectors.partitioningBy(it -> it.getId() != null));
        //get track ids
        List<List<AlbumTracksItem>> listAlbumTracks = mapSearchAlbum.get(true).stream().map(i -> serviceApi.getAlbumTrackIds(i.getId())).collect(Collectors.toList());
        //convert ids to list<string> (each string = ids of an album)
        List<String> listIds = listAlbumTracks.stream().map(i -> i.stream().map(l1 -> l1.getId()).collect(Collectors.joining(","))).collect(Collectors.toList());
        //get popularity of the ids (from album)
        List<List<TrackPopularity>> listPopularity = listIds.stream().map(i -> serviceApi.getTracksPopularity(i)).collect(Collectors.toList());
        //sort popularity of the ids (from album)
        List<Map<Integer, List<String>>> mapListAlbumPopularitySorted = listPopularity.stream().map(i -> tracksUtil.clearTopPopularityAlbum(i, 3)).collect(Collectors.toList());
        //joining cleared
        List<String> listToAddAlbum = mapListAlbumPopularitySorted.stream().flatMap(i -> i.get(0).stream()).collect(Collectors.toList());

        Boolean bResult = serviceApi.addTracks(new Uri(listToAddAlbum), "6CRh3WU4Ygi2S2HPfLm8iP");
        System.out.println("added successfully");
    }

    @Test
    public void testAlbumArtist() {
        serviceApi.test(accesstoken);
        //String s = "Snail Mail \"Pristi!ne\"";
        String s = "ok computer"; //7dxKtc08dYeRVHt3p9CZJn?si=PtwbgNc0RFe1Y4xpKmdGjg
        //String s = "art zoyd"; //3UGoJfjsENJ0x9rVckIuOg
        //=================================================================================================================
        //======artist top
//        ArtistItem i = serviceApi.searchArtist(s);
//        List<TopArtistTracks> ia = serviceApi.getArtistTopTracks(i.getId());
//        List<String> rrUri = tracksUtil.clearTopPopularityArtist(ia, 3);
//        System.out.println();
        //=================================================================================================================
        //=====album top
        //find album
        AlbumItem a = serviceApi.searchAlbum(s);
        //get track ids from album
        List<AlbumTracksItem> albumTracks = serviceApi.getAlbumTrackIds(a.getId());
        //convert ids to string
        String albumTracksString = tracksUtil.albumTracksItemToString(albumTracks);
        //get popularity of the ids (from album)
        List<TrackPopularity> listPopularity = serviceApi.getTracksPopularity(albumTracksString);
        //sort popularity of the ids (from album)
        ////////////////////////////////////////////////////////List<String> albumPopularitySortedUri = tracksUtil.clearTopPopularityAlbum(listPopularity, 3);
        System.out.println();
        //=================================================================================================================
    }

    @Test
    public void testRefresh() {
        serviceApi.test(accesstoken);
        //serviceApi.testRefresh("AQC3iZ75gRGcIOUBUKaPnL4AUDNKRv7aUL-T1_tb6g-dXJixErA4zdSkK53ZikS1xj8aBH7-2CjuqpqNdjhpUBUImbIdtvdc9iP_OC9bQMoFGRMRNEwYjXzVfUVizQPHcJmeZw");
        //serviceApi.beforeCall();
    }

    @Test
    public void testSearchSubmit() {

        List<String> list = new ArrayList<>();
        list.add("https://www.youtube.com/watch?v=4k4SP01l6rY");
        list.add("https://www.youtu");
        list.add("kin k - kiukjioo");
        list.add("https://www.youtube.com/watch?v=uAsV5-Hv-7U");

        //validity 3600000 + System.currentTimeMillis()

        //****Set access token to use serviceApi's spotify API
        serviceApi.test(accesstoken);
        //serviceApi.testRefresh("AQC3iZ75gRGcIOUBUKaPnL4AUDNKRv7aUL-T1_tb6g-dXJixErA4zdSkK53ZikS1xj8aBH7-2CjuqpqNdjhpUBUImbIdtvdc9iP_OC9bQMoFGRMRNEwYjXzVfUVizQPHcJmeZw");


        Map<Boolean, List<String>> mapYt = list.stream().map(i -> serviceApi.getClearSongName(i)).collect(Collectors.partitioningBy(str -> !str.contains("\b")));

        Map<Boolean, List<TrackItem>> mapSp = mapYt.get(true).stream().map(e -> serviceApi.searchTrack(e)).collect(Collectors.partitioningBy(it -> it.getUri() != null));

        List<String> listToAdd = mapSp.get(true).stream().map(i -> i.getUri()).collect(Collectors.toList());
        serviceApi.addTracks(new Uri(listToAdd), "7vOmFSzBe6C4TG7PGvg5lw");
        System.out.println();


        //List<String> newList = list.parallelStream().map(l -> songTitleUtil.clearSpecialCharSong(l)).collect(Collectors.toList());
        //List<Item> trackList = list.stream().map(l -> songTitleUtil.clearSpecialCharSong(l)).map(lItem->serviceApi.searchTrack(lItem)).collect(Collectors.toList());

        //System.out.println(youtubeUtil.getClearSongName("https://www.youtube.com/watch?v=4k4SP01l6rY"));
//        System.out.println();
//        System.out.println(songTitleUtil.clearSpecialCharSong("https://www.youtube.com/watch?v=4k4SP01l6rY"));
//        String uri = "https://www.youtube.com/watch?v=jljJ1m0CXAs&list=PLQl4GEJu5zoU273YLWjYvn1HU7nuXN02e&index=4&t=0s";
//        MultiValueMap<String, String> parameters =
//                UriComponentsBuilder.fromUriString(uri).build().getQueryParams();
//        List<String> s = parameters.get("v");
//        System.out.println();

//        List<String> param1 = parameters.get("param1");
//        List<String> param2 = parameters.get("param2");
//        System.out.println("param1: " + param1.get(0));
//        System.out.println("param2: " + param2.get(0) + "," + param2.get(1));
        //List<Item> listItem = newList.stream().map(lItem->serviceApi.searchTrack(lItem)).collect(Collectors.toList());
        //System.out.println(newList);

        //trackList.parallelStream().forEach(i->i.getUri());

        //===normalize
        //System.out.println(Normalizer.normalize("Susanne Sundfór", Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));

        //===collator
//        Collator insenstiveStringComparator = Collator.getInstance();
//        insenstiveStringComparator.setStrength(Collator.PRIMARY);
//        System.out.println("pp "+insenstiveStringComparator.compare("ô, ana", "o ana"));

    }

    @Test
    public void getToken() {
        System.out.println("entrou");
        String url = baseUrl;
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic M2JjZDNmMDVmYTBiNDVkOWE4MTY4ZmFmMjNhYjg3Mjk6Yzc4YWI5YzE5YmRhNGY3MzgwMDA5M2E3NDdiMmJjODg=");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        //map.add("Authorization", "Basic M2JjZDNmMDVmYTBiNDVkOWE4MTY4ZmFmMjNhYjg3Mjk6Yzc4YWI5YzE5YmRhNGY3MzgwMDA5M2E3NDdiMmJjODg=");

        map.add("grant_type", "authorization_code");
        map.add("code", CODE);
        map.add("redirect_uri", "https://www.getpostman.com/oauth2/callback");

//        map.add("client_id", clientId);
//        map.add("client_secret", secret);
//        map.add("code", code);
//        map.add("grant_type", "authorization_code");


        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<MultiValueMap<String, String>>(map, headers);
        try {
            System.out.println("try");
            template = new RestTemplate();
            AccessToken accessToken = template.postForObject(baseUrl, requestEntity, AccessToken.class);
            //accessToken = response.getAccessToken() + " - " + response.getTokenType();
            System.out.println("FEITOOOOOOOOO " + accessToken.getAccessToken());
        } catch (RestClientResponseException e) {
            System.out.println(e.getMessage());
            System.out.println("Fail " + e.getResponseBodyAsString());
        }


        //return token;
    }

    @Test
    public void getTokenNew() {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Basic M2JjZDNmMDVmYTBiNDVkOWE4MTY4ZmFmMjNhYjg3Mjk6Yzc4YWI5YzE5YmRhNGY3MzgwMDA5M2E3NDdiMmJjODg=");

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", CODE);
        requestBody.add("redirect_uri", "https://www.getpostman.com/oauth2/callback");

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
        try {
            AccessToken response = restTemplate.postForObject("https://accounts.spotify.com/api/token", httpEntity, AccessToken.class);
            System.out.println("success: " + response);
            //httpServletResponse.setHeader("Location", url); // redirect to success page
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void userDetails() {

        String url = "https://api.spotify.com/v1/me/";
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        //headers.add("Authorization", "Bearer BQASoTIOtYvWn1TiZ8Qx71XANzaahJOHTkNhQeuGq_Az8DMbwxrFLNNsaXy-Tt_Yg8_Qz4DQSKqseYkWeuasLKbe48-jlSc0ezh_NPkvq6P-qtBe290pJbnzGHyXMQwZ8rcR-cdoWiSVL4ZkrMAgupSD_pNuJuPkxXNfm3G8--qFxk_tmC0oOdmBiAs1T0O0PaQs2_PpNoFmKKfp3vj4fkaHjIWwSmEARbGofh_8WAQ");
        headers.add("Authorization", "Bearer BQD9WuN0MLe-k22vCTZ18L0Km9e2uIuoKqHGNzwepJ_dAKdbuQjdRDkMHK7l2X_ZHVHwZULs7oPSnAPST4TU7pXogzXLl5NHn3bJmombu5SIPerWHAQeol39nl6JU13L1hwR_NyFCt5cGAId3OBP2jVn0FXkjUGg7aqUs6q2mAXM9EY_Kn8LS0OLb3bl9o38nZ93fj3-em8OY6Wycj_RSmy3TfP0UNxwNCJo6nfpVw8");
        //headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        //map.add("Authorization", "Basic M2JjZDNmMDVmYTBiNDVkOWE4MTY4ZmFmMjNhYjg3Mjk6Yzc4YWI5YzE5YmRhNGY3MzgwMDA5M2E3NDdiMmJjODg=");
//        map.add("grant_type", "authorization_code");
//        map.add("code", CODE);
//        map.add("redirect_uri", "https://www.getpostman.com/oauth2/callback");
        try {
            HttpEntity<String> entity = new HttpEntity<>("paramenters", headers);
            ResponseEntity<User> response = template.exchange(url, HttpMethod.GET, entity, User.class);
            //String user = template.exchange(url, HttpMethod.GET, headers, String.class);
            //accessToken = response.getAccessToken() + " - " + response.getTokenType();
            System.out.println("FEITOOOOOOOOO " + response.getStatusCode() + " " + response.getBody());
        } catch (Exception e) {
            System.out.println("deu r u i m");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void CreatePlaylistManual() {
        String URL = "https://api.spotify.com/v1/users/12162320634/playlists";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", AUTHORIZATION);
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        try {
            System.out.println("create json");
            jsonObject.put("name", "nova");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);

        try {
            System.out.println("postForObject");
            String result = restTemplate.postForObject(URL, request, String.class);
            JsonNode root = objectMapper.readTree(result);
            System.out.println("?? " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void CreatePlaylistPojo() {
        String URL = "https://api.spotify.com/v1/users/12162320634/playlists";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", AUTHORIZATION);
        headers.setContentType(MediaType.APPLICATION_JSON);

        //ObjectMapper objectMapper = new ObjectMapper();

        CreatePlaylist createPlaylist = new CreatePlaylist("play 22");
        createPlaylist.setDescription("this is a description");
        HttpEntity<CreatePlaylist> request = new HttpEntity<>(createPlaylist, headers);

        try {
            System.out.println("postForObject");
            PlaylistItem result = restTemplate.postForObject(URL, request, PlaylistItem.class);
            //JsonNode root = objectMapper.readTree(result);
            System.out.println("?? " + result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
