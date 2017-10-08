package info.trongdat.mp3cloud.Presenters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Album;
import info.trongdat.mp3cloud.Models.Entities.Artist;
import info.trongdat.mp3cloud.Models.Entities.Composer;
import info.trongdat.mp3cloud.Models.Entities.Genre;
import info.trongdat.mp3cloud.Models.Entities.Playlist;
import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Models.Entities.Source;

/**
 * Created by Alone on 10/4/2016.
 */

public class SongPresenter {
    Context context;
    SQLiteHandler db;


    public SongPresenter(Context context) {
        this.context = context;
        db = new SQLiteHandler(context);

    }

    public ArrayList<Song> getSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Songs ORDER BY songName");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int genreID = cursor.getInt(3);
            String songName = cursor.getString(1);
            int duration = cursor.getInt(5);
            String path = cursor.getString(6);
            String cover = cursor.getString(7);
            int favorite=cursor.getInt(cursor.getColumnIndex("favorite"));
            Song song = new Song()
                    .setSongID(id)
                    .setGenreID(genreID)
                    .setSongName(songName)
                    .setLyrics(" ")
                    .setDescription(" ")
                    .setDuration(duration)
                    .setPath(new Source().setLossless(path))
                    .setCover(cover)
                    .setPlay(false)
                    .setFavorite(favorite);

            songs.add(song);
        }
        cursor.close();
        return songs;
    }

    public ArrayList<Song> getSongOfAlbum(int albumID) {
        ArrayList<Song> songs = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Songs WHERE songID IN (SELECT songID FROM AlbumDetails WHERE albumID="+albumID+") ORDER BY songName ");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int genreID = cursor.getInt(3);
            String songName = cursor.getString(1);
            int duration = cursor.getInt(5);
            String path = cursor.getString(6);
            String cover = cursor.getString(7);

            Song song = new Song()
                    .setSongID(id)
                    .setGenreID(genreID)
                    .setSongName(songName)
                    .setLyrics(" ")
                    .setDescription(" ")
                    .setDuration(duration)
                    .setPath(new Source().setLossless(path))
                    .setCover(cover)
                    .setPlay(false);

            songs.add(song);
        }
        cursor.close();
        return songs;
    }

    public ArrayList<Song> getSongOfArtist(int artistID) {
        ArrayList<Song> songs = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Songs WHERE songID IN (SELECT songID FROM ArtistSong WHERE artistID="+artistID+") ORDER BY songName ");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int genreID = cursor.getInt(3);
            String songName = cursor.getString(1);
            int duration = cursor.getInt(5);
            String path = cursor.getString(6);
            String cover = cursor.getString(7);

            Song song = new Song()
                    .setSongID(id)
                    .setGenreID(genreID)
                    .setSongName(songName)
                    .setLyrics(" ")
                    .setDescription(" ")
                    .setDuration(duration)
                    .setPath(new Source().setLossless(path))
                    .setCover(cover)
                    .setPlay(false);

            songs.add(song);
        }
        cursor.close();
        return songs;
    }

    public int getCountOfSong() {
        return getSongs().size();
    }

    public Song getSong(int index) {
        return getSongs().get(index);
    }


    public ArrayList<Album> getAlbums() {

        ArrayList<Album> albums = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Albums");
//        Cursor cursor = db.rawQuery("SELECT * FROM Albums ORDER BY albumName");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String albumName = cursor.getString(1);
            String cover = cursor.getString(3);
            Album album = (Album) new Album()
                    .setId(id)
                    .setName(albumName)
                    .setDescription("")
                    .setImage(Uri.parse(cover));
            albums.add(album);
        }
        cursor.close();
        return albums;
    }

    public Album getAlbum(int index) {
        return getAlbums().get(index);
    }

    public ArrayList<Artist> getArtists() {
        ArrayList<Artist> artists = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Artists ORDER BY artistName");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String artistName = cursor.getString(1);

            Artist artist = (Artist) new Artist()
                    .setId(id)
                    .setName(artistName)
                    .setSex(false)
                    .setBirthYear(0)
                    .setCountry("")
                    .setImage("");
            artists.add(artist);
        }
        cursor.close();
        return artists;
    }

    public Artist getArtist(int id) {
        ArrayList<Artist> artists = getArtists();
        for (Artist artist1 : artists) {
            if (artist1.getId() == id)
                return artist1;
        }
        return null;
    }

    public ArrayList<Composer> getComposers() {
        ArrayList<Composer> composers = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Composers ORDER BY composerName");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String composerName = cursor.getString(1);

            Composer composer = (Composer) new Composer()
                    .setId(id)
                    .setName(composerName)
                    .setSex(false)
                    .setBirthYear(0)
                    .setCountry("")
                    .setImage("");;
            composers.add(composer);
        }
        cursor.close();
        return composers;
    }

    public Composer getComposer(int index) {
        return getComposers().get(index);
    }


    public ArrayList<Genre> getGenres() {
        ArrayList<Genre> genres = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Genres ORDER BY genreName");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String genreName = cursor.getString(1);

            Genre genre = new Genre()
                    .setTypeID(id)
                    .setTypeName(genreName)
                    .setDescription("");

            genres.add(genre);
        }
        cursor.close();
        return genres;
    }

    public Genre getGenre(int index) {
        return getGenres().get(index);
    }

    public ArrayList<Playlist> getPlaylists() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Playlists ORDER BY playlistName");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String playlistName = cursor.getString(1);

            Playlist playlist = (Playlist) new Playlist()
                    .setId(id)
                    .setName(playlistName)
                    .setDescription("")
                    .setImage(Uri.parse(""));
            playlists.add(playlist);
        }
        cursor.close();
        return playlists;
    }

    public ArrayList<Song> getFavorites() {
        ArrayList<Song> songs = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Songs WHERE favorite=1  ORDER BY songName");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int genreID = cursor.getInt(3);
            String songName = cursor.getString(1);
            int duration = cursor.getInt(5);
            String path = cursor.getString(6);
            String cover = cursor.getString(7);

            int favorite=cursor.getInt(cursor.getColumnIndex("favorite"));
            Song song = new Song()
                    .setSongID(id)
                    .setGenreID(genreID)
                    .setSongName(songName)
                    .setLyrics(" ")
                    .setDescription(" ")
                    .setDuration(duration)
                    .setPath(new Source().setLossless(path))
                    .setCover(cover)
                    .setPlay(false)
                    .setFavorite(favorite);
            songs.add(song);
        }
        cursor.close();
        return songs;
    }

    public ArrayList<Song> getDownloads() {
        ArrayList<Song> songs = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Downloads  ORDER BY songName");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int genreID = cursor.getInt(3);
            String songName = cursor.getString(1);
            int duration = cursor.getInt(5);
            String path = cursor.getString(6);
            String cover = cursor.getString(7);
            int favorite=cursor.getInt(cursor.getColumnIndex("favorite"));
            Song song = new Song()
                    .setSongID(id)
                    .setGenreID(genreID)
                    .setSongName(songName)
                    .setLyrics(" ")
                    .setDescription(" ")
                    .setDuration(duration)
                    .setPath(new Source().setLossless(path))
                    .setCover(cover)
                    .setPlay(false)
                    .setFavorite(favorite);
            songs.add(song);
        }
        cursor.close();
        return songs;
    }

}
