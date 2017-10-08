package info.trongdat.mp3cloud.Models.Entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Alone on 12/1/2016.
 */

public class OnlineAlbum implements Serializable {
    @SerializedName("playlist_id")
    private int id;
    @SerializedName("title")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("cover")
    private String image;
    @SerializedName("link")
    private String link;
    @SerializedName("artist")
    private String artist;
    @SerializedName("genre_name")
    private String genre;

    @SerializedName("total_play")
    private String view;

    public OnlineAlbum() {
    }

    public OnlineAlbum(int id, String name, String description, String image, String link, String artist) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.link = link;
        this.artist = artist;
    }

    public int getId() {
        return id;
    }

    public OnlineAlbum setId(int id) {
        this.id = id;
        return this;
    }

    public String getView() {
        return view;
    }

    public OnlineAlbum setView(String view) {
        this.view = view;
        return this;
    }

    public String getGenre() {
        return genre;
    }

    public OnlineAlbum setGenre(String genre) {
        this.genre = genre;
        return this;
    }

    public String getName() {
        return name;
    }

    public OnlineAlbum setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public OnlineAlbum setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getImage() {
        return image;
    }

    public OnlineAlbum setImage(String image) {
        this.image = image;
        return this;
    }

    public String getLink() {
        return link;
    }

    public OnlineAlbum setLink(String link) {
        this.link = link;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public OnlineAlbum setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
