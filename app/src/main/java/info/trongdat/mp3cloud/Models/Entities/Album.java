package info.trongdat.mp3cloud.Models.Entities;

import android.net.Uri;

/**
 * Created by Alone on 10/14/2016.
 */

public class Album extends SongList {
    public Album(){}

    public Album(int id, String name, String description, Uri image) {
        super(id, name, description, image);
    }
}
