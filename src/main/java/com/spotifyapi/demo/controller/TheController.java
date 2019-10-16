package com.spotifyapi.demo.controller;


import com.spotifyapi.demo.entity.playlists.PlaylistItem;
import com.spotifyapi.demo.service.ServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class TheController {

    @Autowired
    ServiceApi serviceApi;
    List<PlaylistItem> listPlaylist;


    @GetMapping("/")
    public String home(Model model) {

        if (listPlaylist != null) {
            model.addAttribute("playlists", listPlaylist);
        }

        if (!serviceApi.isAccessToken()) {
            model.addAttribute("noToken", true);
        }

        return "home";
    }

    @GetMapping("/authorize")
    public String authorize() {

        return "redirect:" + ServiceApi.AUTHORIZE_URL;

    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code, Model model) {

        //        Cookie cookie = new Cookie("test", "avalue123");
        //        cookie.setMaxAge(60*60*24);
        //        response.addCookie(cookie);
        //System.out.println("code: " + code);
        //ServiceApiImpl serviceApi = new ServiceApiImpl();
        serviceApi.getAccessToken(code);

        listPlaylist = serviceApi.getPlaylists();
        model.addAttribute("playlists", listPlaylist);


        return "home";
    }

    @GetMapping("/userDetails")
    public String userDetails(Model model) {


        listPlaylist = serviceApi.getPlaylists();
        if (listPlaylist != null) {
            model.addAttribute("playlists", listPlaylist);
        } else {
            model.addAttribute("noToken", true);
        }

        System.out.println();

        return "home";
    }

    @GetMapping("/createPlaylist")
    public String createPlaylist(@RequestParam("playlistName") String playlistName) {
        serviceApi.createPlaylist(playlistName);
        return "home";
    }

    @GetMapping("teste")
    public String teste(){
        return "test";
    }

    @GetMapping("/searchTrack")
    public String searchTrack(@RequestParam(value = "searchNameAlbum", required = false) String searchNameAlbum, @RequestParam(value = "searchNameArtist", required = false) String searchNameArtist, @RequestParam(value = "playlistName", required = false) String playlistName,
                              @RequestParam(value = "selectPlaylist", required = false) String selectPlaylist, @RequestParam(value = "playlistRadio") String playlistRadio, @RequestParam(value = "amountRadio")int amountRadio, Model theModel) {
        if (searchNameAlbum.isEmpty() && searchNameArtist.isEmpty()) {
            System.out.println("no songs");
            return "home";
        }
        //TODO hover effect on homepage get started button

        List<String> artistsToFind;
        List<String> albumsToFind;
        if (!searchNameArtist.equalsIgnoreCase("")){
            artistsToFind = Arrays.asList(searchNameArtist.split("\\r?\\n"));
        } else { artistsToFind=null;}
        if (!searchNameAlbum.equalsIgnoreCase("")){
            albumsToFind = Arrays.asList(searchNameAlbum.split("\\r?\\n"));
        } else { albumsToFind=null;}


        System.out.println();
        //serviceApi.searchTracks(s);
        //Uri uri = new Uri(new String[]{"spotify:track:69GuasseR3zP2F9uOVh50i","spotify:track:5sICkBXVmaCQk5aISGR3x1"});
        //serviceApi.addTracks(uri,playlistName);

        Map<Integer, Map<Boolean, List<String>>> mapReturn;

        if (playlistRadio.equalsIgnoreCase("existing")) {
            mapReturn = serviceApi.submitAddAllTracks(albumsToFind, artistsToFind, selectPlaylist, amountRadio);
        } else {
            PlaylistItem playlist = serviceApi.createPlaylist(playlistName);
            listPlaylist = serviceApi.getPlaylists();
            if (playlist != null) {
                mapReturn = serviceApi.submitAddAllTracks(albumsToFind, artistsToFind, playlist.getId(), amountRadio);

            } else {
                mapReturn = null;
            }
        }

        //joining artists and albums
//        Map<Boolean, List<String>> mapHome = new HashMap<>();
//        List<String> listSuccess = new ArrayList<>();
//        List<String> listFail = new ArrayList<>();
//        if (mapReturn.containsKey(0)){
//            listSuccess = mapReturn.get(0).get(true);
//            listFail = mapReturn.get(0).get(false);
//        }
//        if (mapReturn.containsKey(1)){
//            listSuccess = Lists
//            listFail = Stream.concat(mapReturn.get(0).get(false).stream(), mapReturn.get(1).get(false).stream()).collect(Collectors.toList());
//
//        }
//        mapHome.put(true, listSuccess);
//        mapHome.put(false, listFail);

        if (mapReturn != null) {
            theModel.addAttribute("failedSuccessAlbums", mapReturn.get(0));
            theModel.addAttribute("failedSuccessArtists", mapReturn.get(1));
        } else {
            theModel.addAttribute("noToken", true);
        }

        theModel.addAttribute("playlists", listPlaylist);

        return "home";
    }
}
