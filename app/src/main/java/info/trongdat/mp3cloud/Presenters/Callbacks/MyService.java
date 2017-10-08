package info.trongdat.mp3cloud.Presenters.Callbacks;

import info.trongdat.mp3cloud.Models.Entities.Album;
import info.trongdat.mp3cloud.Models.Entities.Song;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Alone on 11/12/2016.
 */

public interface MyService {

    @GET("api/mobile/song/getsonginfo?")
    Call<Song> getSong(@Query("requestdata") String requetData);

    @GET("api/mobile/song/getlyrics?")
    Call<Song> getLyrics(@Query("requestdata") String requetData);

    @GET("api/mobile/playlist/getplaylistinfo?")
    Call<Album> getAlbum(@Query("requestdata") String requetData);


}
