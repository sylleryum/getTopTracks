<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!doctype html>
<html lang="en">

<head>

    <script>

        document.addEventListener('DOMContentLoaded', function () {


            <c:choose>
            <c:when test="${noToken==true}">
            document.getElementById("no-token").classList.remove("block-style");
            document.getElementById("no-token").classList.add("no-token");
            </c:when>

            <c:otherwise>
            document.getElementById("main-window").classList.remove("block-style");
            document.getElementById("main-window").classList.add("program-container");
            </c:otherwise>
            </c:choose>

        }, false);


        function radioChecker() {
            if (document.getElementById("rNew").checked) {
                document.getElementById("new-playlist").classList.remove("block-style");
                document.getElementById("existing-playlist").classList.add("block-style");
            } else {
                document.getElementById("new-playlist").classList.add("block-style");
                document.getElementById("existing-playlist").classList.remove("block-style");
            }
        }

        function validation() {
            var errors = '';
            if (document.getElementById("rNew").checked) {

                if (document.getElementById("playlistName").value === "") {
                    errors = "Please choose a name for the new playlist";
                }
            } else if (!document.getElementById("rNew").checked && !document.getElementById("rExisting").checked) {
                errors = "Please select new or existing playlist";
            }

            if (document.getElementById("searchNameArtist").value === "" && document.getElementById("searchNameAlbum").value === "") {
                errors += "\rPlease insert artist(s) or album(s)";
            }

            if (errors === "") {
                return true;
            } else {
                alert(errors);
                return false;
            }

        }
    </script>


    <title>Get top tracks from Artists and albums</title>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
          integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

    <!-- main CSS-->
    <link rel="stylesheet" href="resources/css/styles.css">

</head>

<body class="background-master">
<form action="searchTrack" id="submit-btn" onsubmit="return validation()">
    <div class="container-fluid h-97">


        <div class="row align-items-center justify-content-center h-97">
            <div id="no-token" class="block-style square row align-items-center justify-content-center"
                 onclick="location.href='/authorize';" style="cursor:pointer;">
                <a href="/authorize">Click here to start</a>
            </div>
            <%--            <div id="no-token" class="block-style square bg-5 d-flex align-items-center justify-content-center" onclick="location.href='/authorize';" style="cursor:pointer;">--%>
            <%--                <a href="/authorize">Click here to start</a>--%>
            <%--            </div>--%>
            <div class="h-80 col-md-10 bg-4 text-1 rounded-lg pl-4 pr-4 pt-2 overflow-auto block-style"
                 id="main-window">

                <div class="row main-select pb-1 justify-content-center">
                    <label><input type="radio" id="rNew" name="playlistRadio" value="new"
                                  onchange="radioChecker()">New
                        playlist&nbsp;&nbsp;&nbsp;</label>
                    <label><input type="radio" id="rExisting" name="playlistRadio" value="existing"
                                  onchange="radioChecker()">Existing
                        playlist</label>
                </div>
                <div class="row main-playlist pb-2 justify-content-center">
                    <div id="new-playlist" class="block-style">
                        Playlist name: <input type="text" class="rounded-lg border-0 bg-5 text-1" id="playlistName"
                                              name="playlistName"/>
                    </div>
                    <div id="existing-playlist" class="block-style">
                        <span>Your playlists:</span>
                        <select name="selectPlaylist" class="rounded-lg border-0 bg-5 text-1">
                            <c:forEach var="albumTracksItems" items="${playlists}">
                                <option value="${albumTracksItems.getId()}">${albumTracksItems.getName()}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="row pb-2 justify-content-center">
                    Search Artists or/and Albums:
                </div>
                <div class="row main-textarea form-group pr-1 mb-0 pb-2">
                    <div class="col-md-6 p-0 mb-1">
                                   <textarea class="textarea-search form-control md-textarea bg-5 text-1 border-0"
                                             rows="5" id="searchNameArtist"
                                             name="searchNameArtist"
                                             placeholder="Paste desired artists here"></textarea>
                    </div>
                    <div class="col-md-6 p-0 small-padding">
                                   <textarea class="textarea-search form-control md-textarea bg-5 text-1 border-0"
                                             rows="5" id="searchNameAlbum"
                                             name="searchNameAlbum" placeholder="Paste desired albums here"></textarea>
                    </div>
                </div>
                <div class="row track-amount pb-2 justify-content-center text-center">
                    <label><input type="radio" id="rAmount1" name="amountRadio" value="1" onchange="">1 track&nbsp;&nbsp;&nbsp;</label>
                    <label><input type="radio" id="rAmount2" name="amountRadio" value="2" onchange="">2 tracks&nbsp;&nbsp;&nbsp;</label>
                    <label><input type="radio" id="rAmount3" name="amountRadio" value="3" checked onchange="">3 tracks&nbsp;&nbsp;&nbsp;</label>
                    <label><input type="radio" id="rAmount4" name="amountRadio" value="4" onchange="">4 tracks&nbsp;&nbsp;&nbsp;</label>
                    <label><input type="radio" id="rAmount5" name="amountRadio" value="5" onchange="">5 tracks&nbsp;&nbsp;&nbsp;</label>
                </div>
                <div class="row main-go pb-2 justify-content-center">
                    <button type="submit" class="btn bg-2 btn-sm btn-block text-uppercase font-weight-bold text-4"
                            id="submit-button">
                        Give me my playlist!
                    </button>
                </div>

                <div class="row results-menu">
                    <div class="success col-md-6 p-0">
                        <c:if test="${failedSuccessAlbums.size() >0}">
                            <h4 class="font-weight-bold">Album songs added(${failedSuccessAlbums.get(true).size()}):</h4>
                            <c:forEach var="success" items="${failedSuccessAlbums.get(true)}">
                                <p>${success}</p>
                            </c:forEach>
                        </c:if>

                        <c:if test="${failedSuccessArtists.size() >0}">
                            <h4 class="font-weight-bold">Artists' songs added(${failedSuccessArtists.get(true).size()}):</h4>
                            <c:forEach var="success" items="${failedSuccessArtists.get(true)}">
                                <p>${success}</p>
                            </c:forEach>
                        </c:if>
                    </div>
                    <div class="failure col-md-6 p-0">
                        <c:if test="${failedSuccessAlbums.size() >0}">
                            <h4 class="font-weight-bold">Albums not found (${failedSuccessAlbums.get(false).size()}):</h4>
                            <c:forEach var="fail" items="${failedSuccessAlbums.get(false)}">
                                <p>${fail}</p>
                            </c:forEach>
                        </c:if>

                        <c:if test="${failedSuccessArtists.size() >0}">
                            <h4 class="font-weight-bold">Artists not found (${failedSuccessArtists.get(false).size()}):</h4>
                            <c:forEach var="fail" items="${failedSuccessArtists.get(false)}">
                                <p>${fail}</p>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>

            </div>

        </div>

    </div>
</form>
<footer class="text-center m-0 p-0 bg-4 text-1 footer-clear">
    <h6 class="p-0 m-0 text-w">Find more information and source code at: <a href="https://github.com/sylleryum/getTopTracks">
        https://github.com/sylleryum/getTopTracks</a></h6>

</footer>


<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
        integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
        integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
        crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
        integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
        crossorigin="anonymous"></script>
</body>

</html>