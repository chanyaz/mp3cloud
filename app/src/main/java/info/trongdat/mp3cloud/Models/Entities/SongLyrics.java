package info.trongdat.mp3cloud.Models.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alone on 10/4/2016.
 */

public class SongLyrics implements Parcelable {

    @SerializedName("id")
    private int songID;

    @SerializedName("content")
    private String lyrics;

    @SerializedName("author")
    private String author;


    public SongLyrics() {
        super();
    }

    public SongLyrics(int songID, String lyrics, String author) {
        this.songID = songID;
        this.lyrics = lyrics;
        this.author = author;
    }

    public int getSongID() {
        return songID;
    }

    public SongLyrics setSongID(int songID) {
        this.songID = songID;
        return this;
    }

    public String getLyrics() {
        return lyrics;
    }

    public SongLyrics setLyrics(String lyrics) {
        this.lyrics = lyrics;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public SongLyrics setAuthor(String author) {
        this.author = author;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.songID);
        parcel.writeString(this.lyrics);
    }

    protected SongLyrics(Parcel in) {
        this.songID = in.readInt();
        this.lyrics = in.readString();
    }

    public static final Creator<SongLyrics> CREATOR = new Creator<SongLyrics>() {
        public SongLyrics createFromParcel(Parcel source) {
            return new SongLyrics(source);
        }

        public SongLyrics[] newArray(int size) {
            return new SongLyrics[size];
        }
    };

}
