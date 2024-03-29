# Important: as of November 28th this project needs to be ran locally as Heroku which was the hosting it, has closed its free tier services.

# getTopTracks
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/sylleryum/getTopTracks/blob/master/LICENSE.txt)

Available at: http://gettoptracks.herokuapp.com/
<br>Paste a list of artists and/or albums to get top tracks from them (full description below)

<strike>You can also paste a chart link from RYM go get its artists/albums, I.E: https://rateyourmusic.com/charts/top/album/2017</strike>
![system working](https://github.com/sylleryum/getTopTracks/blob/master/demo.gif)

## Why this is useful

* Suppose you are looking for new songs/artists/albums, if you receive single song recommendations, you simply listen to it and that's it. Now let's suppose you receive the recommendation of jeff wayne's musical version of the war of the worlds which is 94:54 long, or you have a large number of artists to listen to see if you like it or not. So how can you decide what to listen to from these albums/artists?
* **This application tries to help in such scenarios giving you the best evaluated songs (up to 5) of artists/albums provided (see video above for demonstration)**

## Full description/features

* Simply press in "Get started" to authorize this app to create playlists/add songs to your spotify<br>
* This application let's you paste several artists and/or albums and then adds to the selected or created Spotify playlist
* If there are songs duplicated between pasted artist and the album, it just adds the song once (E.g. if one pastes artist A and Album B from same artist and 2 of the 3 top songs from album and artist are the same, these 2 songs are added only once)
* You can also paste a chart from Rate Your Music Website I.E: https://rateyourmusic.com/charts/top/album/2017 and the application will add the number of the best songs selected (see Issues/next steps)
* You can either create a new playlist or add songs to an existing one<br>
* One album/artist per line<br>

## Issues/next steps
* The feature of getting top songs from RYM is not available <strike>through http://gettoptracks.herokuapp.com/ as RYM blocks the access, to use the same you have to clone the repo and run it locally </strike>
