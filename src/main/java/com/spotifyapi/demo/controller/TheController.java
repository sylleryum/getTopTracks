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
    public String teste() {
        return "test";
    }

    @GetMapping("/searchTrack")
    public String searchTrack(@RequestParam(value = "searchNameAlbum", required = false) String searchNameAlbum,
                              @RequestParam(value = "searchNameArtist", required = false) String searchNameArtist,
                              @RequestParam(value = "playlistName", required = false) String playlistName,
                              @RequestParam(value = "selectPlaylist", required = false) String selectPlaylist,
                              @RequestParam(value = "playlistRadio", required = false) String playlistRadio,
                              @RequestParam(value = "searchRadio") Integer searchRadio,
                              @RequestParam(value = "amountRadio", required = false) Integer amountRadio,
                              @RequestParam(value = "amountChart", required = false) Integer amountChart,
                              Model theModel) {
        if (searchNameAlbum.isEmpty() && searchNameArtist.isEmpty()) {
            System.out.println("no songs");
            return "home";
        }
        if (amountChart == null) {
            amountChart = 3;
        }

        List<String> artistsToFind;
        List<String> albumsToFind;

        if (!searchNameArtist.equalsIgnoreCase("")) {
            artistsToFind = Arrays.asList(searchNameArtist.split("\\r?\\n"));
        } else {
            artistsToFind = null;
        }
        if (!searchNameAlbum.equalsIgnoreCase("")) {
            albumsToFind = Arrays.asList(searchNameAlbum.split("\\r?\\n"));
        } else {
            albumsToFind = null;
        }

        Map<Short, List<String>> mapRym = null;
//        if (searchRadio==1){
//            System.out.println();
//            serviceApi.testMap(artistsToFind);
//        }

        if (searchRadio == 2) {
            try {
                if (artistsToFind != null && albumsToFind != null) {
                    mapRym = serviceApi.getRYM(artistsToFind.get(0), ServiceApi.getRYM_SEARCH_BOTH, amountChart);
                    System.out.println();
//                    ArrayList<String> listArtist = new ArrayList<String>(mapRym.get(ServiceApi.getRYM_SEARCH_ARTIST).keySet());
//                    ArrayList<String> listAlbum = new ArrayList<String>(mapRym.get(ServiceApi.getRYM_SEARCH_ALBUM).keySet());
                    System.out.println();
                    theModel.addAttribute("rymArtists", mapRym.get(ServiceApi.getRYM_SEARCH_ARTIST));
                    theModel.addAttribute("rymAlbums", mapRym.get(ServiceApi.getRYM_SEARCH_ALBUM));
                    theModel.addAttribute("playlists", listPlaylist);
                    return "home";

                } else if (artistsToFind != null && albumsToFind == null) {
                    mapRym = serviceApi.getRYM(artistsToFind.get(0), ServiceApi.getRYM_SEARCH_ARTIST, amountChart);
                    //ArrayList<String> listArtist = new ArrayList<>(mapRym.get(ServiceApi.getRYM_SEARCH_ARTIST).keySet());
                    System.out.println();
                    theModel.addAttribute("rymArtists", mapRym.get(ServiceApi.getRYM_SEARCH_ARTIST));
                    theModel.addAttribute("playlists", listPlaylist);
                    return "home";
                } else if (artistsToFind == null && albumsToFind != null) {
                    mapRym = serviceApi.getRYM(albumsToFind.get(0), ServiceApi.getRYM_SEARCH_ALBUM, amountChart);
                    //ArrayList<String> listAlbum = new ArrayList<>(mapRym.get(ServiceApi.getRYM_SEARCH_ALBUM).keySet());
                    System.out.println();
                    theModel.addAttribute("rymAlbums", mapRym.get(ServiceApi.getRYM_SEARCH_ALBUM));
                    theModel.addAttribute("playlists", listPlaylist);
                    return "home";
                }
            } catch (Exception e) {
                theModel.addAttribute("rymError", true);
                System.out.println();
                return "home";
            }

        }

        Map<Integer, Map<Boolean, List<String>>> mapReturn;
        if (playlistRadio.equalsIgnoreCase("existing")) {
            if (searchRadio==3){
                mapReturn = serviceApi.submitAddAllTracks(albumsToFind, artistsToFind, selectPlaylist, amountRadio, false);
            } else {
                mapReturn = serviceApi.submitAddAllTracks(albumsToFind, artistsToFind, selectPlaylist, amountRadio, false);
            }
        } else {
            PlaylistItem playlist = serviceApi.createPlaylist(playlistName);
            listPlaylist = serviceApi.getPlaylists();
            if (playlist != null) {
                if (searchRadio==3){
                    mapReturn = serviceApi.submitAddAllTracks(albumsToFind, artistsToFind, selectPlaylist, amountRadio, false);
                } else {
                    mapReturn = serviceApi.submitAddAllTracks(albumsToFind, artistsToFind, playlist.getId(), amountRadio, false);
                }

            } else {
                mapReturn = null;
            }
        }

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
