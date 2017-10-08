package info.trongdat.mp3cloud.Models.Entities;

import android.net.Uri;

/**
 * Created by Alone on 10/14/2016.
 */

public class SongList {
    private int id;
    private String name, description;
    private Uri image;

    public SongList() {
    }

    public SongList(int id, String name, String description, Uri image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public SongList setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public SongList setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SongList setDescription(String description) {
        this.description = description;
        return this;
    }

    public Uri getImage() {
        return image;
    }

    public SongList setImage(Uri image) {
        this.image = image;
        return this;
    }
}
