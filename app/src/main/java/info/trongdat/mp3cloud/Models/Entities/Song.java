package info.trongdat.mp3cloud.Models.Entities;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alone on 10/4/2016.
 */

public class Song implements Parcelable {

    private int songID;
    @SerializedName("song_id_encode")
    private String songIDencode;

    //    @SerializedName("genre_id")
    private int genreID;

    @SerializedName("artist")
    private String artist;

    @SerializedName("title")
    private String songName;

    @SerializedName("content")
    private String lyrics = " ";

    private String description;

    private int duration = 0;

    @SerializedName("source")
    private Source path;

    @SerializedName("link")
    private String link;

    @SerializedName("thumbnail")
    private String cover;

    private boolean isPlay;
    private int favorite;

    public Song() {
        super();
    }

    public Song(int songID, int genreID, String songName, String lyrics, String description, int duration, Source path, String cover, boolean isPlay) {
        this.songID = songID;
        this.genreID = genreID;
        this.songName = songName;
        this.lyrics = lyrics;
        this.description = description;
        this.duration = duration;
        this.path = path;
        this.cover = cover;
        this.isPlay = isPlay;
    }

    public String getLink() {
        return link;
    }

    public Song setLink(String link) {
        this.link = link;
        return this;
    }

    public int getFavorite() {
        return favorite;
    }

    public Song setFavorite(int favorite) {
        this.favorite = favorite;
        return this;
    }

    public String getSongIDencode() {
        return songIDencode;
    }

    public Song setSongIDencode(String songIDencode) {
        this.songIDencode = songIDencode;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public Song setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public int getGenreID() {
        return genreID;
    }

    public Song setGenreID(int genreID) {
        this.genreID = genreID;
        return this;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public Song setPlay(boolean play) {
        isPlay = play;
        return this;
    }

    public Source getPath() {
        return path;
    }

    public Song setPath(Source path) {
        this.path = path;
        return this;
    }

    public String getCover() {
        return cover;
    }

    public Song setCover(String cover) {
        this.cover = cover;
        return this;
    }

    public int getSongID() {
        return songID;
    }

    public Song setSongID(int songID) {
        this.songID = songID;
        return this;
    }

    public String getSongName() {
        return songName;
    }

    public Song setSongName(String songName) {
        this.songName = songName;
        return this;
    }

    public String getLyrics() {
        return lyrics;
    }

    public Song setLyrics(String lyrics) {
        this.lyrics = "\n"+lyrics;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Song setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public String getDurationTime() {
        String durationText = null;
        if (((this.duration / 1000) % 60) >= 10) {
            durationText = (this.duration / 60000) + ":" + ((this.duration / 1000) % 60);
        } else {
            durationText = (this.duration / 60000) + ":0" + ((this.duration / 1000) % 60);
        }
        return durationText;
    }

    public Song setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.songID);
        parcel.writeInt(this.genreID);
        parcel.writeString(this.songName);
        parcel.writeString(this.lyrics);
        parcel.writeString(this.description);
        parcel.writeInt(this.duration);
        parcel.writeParcelable(Uri.parse(this.path.getLossless()), 0);
        parcel.writeParcelable(Uri.parse(this.cover), 0);

    }

    protected Song(Parcel in) {
        this.songID = in.readInt();
        this.genreID = in.readInt();
        this.songName = in.readString();
        this.lyrics = in.readString();
        this.description = in.readString();
        this.duration = in.readInt();
        this.path = in.readParcelable(Uri.class.getClassLoader());
        this.cover = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };


}
