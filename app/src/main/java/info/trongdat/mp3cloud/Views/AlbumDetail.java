package info.trongdat.mp3cloud.Views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.OnlineAlbum;
import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.Adapters.ItemClickSupport;
import info.trongdat.mp3cloud.Presenters.Adapters.OnlineSongAdapter;
import info.trongdat.mp3cloud.Presenters.Callbacks.AlbumLoadListener;
import info.trongdat.mp3cloud.Presenters.Callbacks.MyService;
import info.trongdat.mp3cloud.Presenters.Callbacks.SongLoadListener;
import info.trongdat.mp3cloud.Presenters.MusicService;
import info.trongdat.mp3cloud.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static info.trongdat.mp3cloud.Views.Tabs.OfflineMusic.TAG;

public class AlbumDetail extends AppCompatActivity {
    String albumURL = "";
    String albumID = "";
    TextView txtAlbum, txtArtist, txtGenre, txtView;
    ImageView imgAlbum;
    RecyclerView lstSong;
    OnlineSongAdapter onlineSongAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        Bundle extras = getIntent().getExtras();
        albumURL = extras.getString("url");

        initialize();
    }

    public void initialize() {
        txtAlbum = (TextView) findViewById(R.id.txtAlbum);
        txtArtist = (TextView) findViewById(R.id.txtArtist);
        txtGenre = (TextView) findViewById(R.id.txtGenre);
        txtView = (TextView) findViewById(R.id.txtView);
        imgAlbum = (ImageView) findViewById(R.id.imgAlbum);

        AlbumAsyncTask loadAlbum = new AlbumAsyncTask(new AlbumLoadListener() {
            @Override
            public void AlbumLoadListener(ArrayList<OnlineAlbum> list) {
                txtAlbum.setText(list.get(0).getName());
                txtArtist.setText(list.get(0).getArtist());
                txtGenre.setText(list.get(0).getGenre());
                txtView.setText(list.get(0).getView());
                Glide.with(AlbumDetail.this)
                        .load(list.get(0).getImage())
                        .asBitmap()
                        .placeholder(R.drawable.aw_ic_default_album)
                        .error(R.drawable.album_thumbnail2)
                        .into(imgAlbum);
            }
        });
        loadAlbum.execute(albumURL);

        lstSong = (RecyclerView) findViewById(R.id.lstSong);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lstSong.setLayoutManager(linearLayoutManager);
        SongAsyncTask hotSongAsyncTask = new SongAsyncTask(new SongLoadListener() {
            @Override
            public void SongLoadListener(ArrayList<Song> list) {
                onlineSongAdapter = new OnlineSongAdapter(AlbumDetail.this, list);
                lstSong.setAdapter(onlineSongAdapter);
            }
        });
        hotSongAsyncTask.execute(albumURL);
        ItemClickSupport.addTo(lstSong)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View view, int position, long id) {
                        MusicService.setTracks(AlbumDetail.this, onlineSongAdapter.getList());
                        MusicService.playTrack(AlbumDetail.this, position);
                        MusicService.setState(AlbumDetail.this, true);

                        Intent intent = new Intent(AlbumDetail.this, SongPlay.class);
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
    }

    // CLASS ASYNC LOAD ALBUM.
    private class AlbumAsyncTask extends AsyncTask<String, OnlineAlbum, ArrayList<OnlineAlbum>> {
        AlbumLoadListener albumLoadListener;

        public AlbumAsyncTask(AlbumLoadListener slideLoadListener) {
            this.albumLoadListener = slideLoadListener;

        }

        @Override
        protected ArrayList<OnlineAlbum> doInBackground(String... strings) {
            final ArrayList<OnlineAlbum> list = new ArrayList<>();
            try {
                // Dùng JSoup và kết nối tới link.
                Connection con = Jsoup.connect(strings[0])
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                        .timeout(10000);
                Connection.Response resp = con.execute();
                Document doc = null;
                if (resp.statusCode() == 200) {
                    doc = con.get();
                    Element albumContainer = doc.getElementsByClass("info-top-play").first();

                    final OnlineAlbum album1 = new OnlineAlbum();

                    String image = albumContainer.select("img").attr("src");
                    String titleName = albumContainer.select("img").attr("alt");

                    String link = "";
                    Element artistInfo = albumContainer.getElementsByClass("info-artist").first();
                    String artistName = artistInfo.select("a").text();

                    Element songInfo = albumContainer.getElementsByClass("info-song-top").first();
                    Log.d("dddd", "doInBackground: cccccc" + songInfo.html());
                    Elements items = songInfo.getElementsByClass("txt-info");
                    String genre = "";
                    for (Element item : items) {
                        genre += item.text() + ", ";
                    }
                    if (genre.endsWith(", ")) genre = genre.substring(0, genre.length() - 2);
//                    Element func=doc.getElementsByClass("media-func").first();
//                    Log.d("dddddddd", "doInBackground: zzzzz\n"+func.html());
                    Element element = songInfo.getElementsByAttributeValue("itemprop", "copyrightYear").first();
                    String view = "2016";
                    if (element != null)
                        view = element.text();

                    Log.d("dddddddddddddd", "doInBackground: album" + artistName + "  " + titleName + "      " + image + "  " + view + "  " + genre);
                    album1.setName(titleName).setImage(image).setArtist(artistName).setGenre(genre).setView(view);

                    list.add(album1);
                }
                return list;

            } catch (IOException e) {
                Log.e("dddddd", "doInBackground: " + e);
                return list;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<OnlineAlbum> onlineAlbums) {
            albumLoadListener.AlbumLoadListener(onlineAlbums);
        }

    }


    // CLASS ASYNC LOAD HOT SONG
    private class SongAsyncTask extends AsyncTask<String, Song, ArrayList<Song>> {
        SongLoadListener songLoadListener;
        MyService myService;

        public SongAsyncTask(SongLoadListener songLoadListener) {
            this.songLoadListener = songLoadListener;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.mp3.zing.vn/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            myService = retrofit.create(MyService.class);
        }

        @Override
        protected ArrayList<Song> doInBackground(String... strings) {
            final ArrayList<Song> list = new ArrayList<>();
            try {
                // Dùng JSoup và kết nối tới link.
                Connection con = Jsoup.connect(strings[0])
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                        .timeout(10000);
                Connection.Response resp = con.execute();
                Document doc = null;
                if (resp.statusCode() == 200) {
                    doc = con.get();

                    Elements playlist = doc.getElementsByClass("playlist");
                    Log.d("ddddd", "doInBackground: ssssssssss\n" + playlist.size() + "  \n" + playlist.html());
                    Elements items = playlist.last().getElementsByTag("li");
                    Log.d("ddddd", "doInBackground: ssssss size" + items.size());
                    for (Element item : items) {
                        Song song = new Song();
                        String id = item.attr("data-id");
                        String image = item.select("img").attr("src");
                        String titleName = item.select(".fn-name").text();
                        String link = item.select(".fn-name").attr("href");
                        String artist = "";
                        Elements artists = item.getElementsByClass("wrap-h4");
                        for (Element element : artists) {
                            artist += element.select("a").text() + ", ";
                        }
                        if (artist.endsWith(", "))
                            artist = artist.substring(0, artist.length() - 2);
                        Log.d("dddddddddddddd", "doInBackground hot song: " + "  " + titleName + "      " + image + "   " + link);

                        song.setSongIDencode(id).setSongName(titleName).setArtist(artist).setCover(image).setLink(link);


//                        // Get các thông tin của bài hát tương ứng với mã bài hát (qua API Zing cung cấp ).
                        final String songID = "{\"id\":\"" + id + "\"}";
                        myService.getSong(songID).enqueue(new Callback<Song>() {

                            @Override
                            public void onResponse(Call<Song> call, Response<Song> response) {
                                song.setPath(response.body().getPath())
                                        .setCover("http://image.mp3.zdn.vn/thumb/240_240/" + response.body().getCover());
                                Log.e("DAT", response.body().getSongName() + "\n" + response.body().getPath().getS128()
                                        + "\n" + response.body().getPath().getS320()
                                        + "\n" + response.body().getPath().getLossless()
                                        + "\n" + call.request().url());

                                Log.d(TAG, "onResponse: url=" + call.request().url());
                            }

                            @Override
                            public void onFailure(Call<Song> call, Throwable t) {
                                Log.e("DAT", t.getMessage()+"");
                            }
                        });

                        // get lyrics of song
                        myService.getLyrics(songID).enqueue(new Callback<Song>() {

                            @Override
                            public void onResponse(Call<Song> call, Response<Song> response) {
                                Log.d(TAG, "onResponse: " + response.body().getLyrics());
                                song.setLyrics(response.body().getLyrics());
                                Log.d(TAG, "onResponse: url=" + call.request().url());
                            }

                            @Override
                            public void onFailure(Call<Song> call, Throwable t) {
                                Log.e("DAT", t.getMessage()+"");
                            }
                        });
                        list.add(song);
                    }

                }
                return list;

            } catch (IOException e) {
                Log.e("dddddd", "doInBackground: " + e);
                return list;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Song> songs) {
            songLoadListener.SongLoadListener(songs);
        }

    }
}
