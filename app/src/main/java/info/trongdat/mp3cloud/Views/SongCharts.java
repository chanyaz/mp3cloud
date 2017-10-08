package info.trongdat.mp3cloud.Views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

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
import info.trongdat.mp3cloud.Presenters.Adapters.OnlineSongAdapter2;
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

public class SongCharts extends AppCompatActivity {
    Spinner spinPlaylist;
    RecyclerView lstPlaylist;
    ArrayAdapter<OnlineAlbum> adapter;
    ImageView imgTop1, imgPlay;
    OnlineSongAdapter2 adapterSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_charts);
        initialize();
    }

    public void initialize() {
        imgTop1 = (ImageView) findViewById(R.id.imgTop1);
        imgPlay = (ImageView) findViewById(R.id.imgPlay);
        spinPlaylist = (Spinner) findViewById(R.id.spinAlbum);
        lstPlaylist = (RecyclerView) findViewById(R.id.lstAlbum);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lstPlaylist.setLayoutManager(linearLayoutManager);
        lstPlaylist.setNestedScrollingEnabled(false);


        ArrayList<OnlineAlbum> list = new ArrayList<>();
        list.add(new OnlineAlbum().setName("Việt Nam").setLink("http://mp3.zing.vn/bang-xep-hang/bai-hat-Viet-Nam/IWZ9Z08I.html"));
        list.add(new OnlineAlbum().setName("Âu Mỹ").setLink("http://mp3.zing.vn/bang-xep-hang/bai-hat-Au-My/IWZ9Z0BW.html"));
        list.add(new OnlineAlbum().setName("Hàn Quốc").setLink("http://mp3.zing.vn/bang-xep-hang/bai-hat-Han-Quoc/IWZ9Z0BO.html"));

        adapter = new ArrayAdapter<>(SongCharts.this, R.layout.simple_spinner_item_1, list);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinPlaylist.setAdapter(adapter);

        spinPlaylist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadAlbum(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPlay.setAnimation(new ScaleAnimation(1,1.5f,1,1.5f,50,50));
                MusicService.setTracks(SongCharts.this, adapterSong.getList());
                MusicService.playTrack(SongCharts.this, 0);
                MusicService.setState(SongCharts.this, true);

                Intent intent = new Intent(SongCharts.this, SongPlay.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    public void loadAlbum(int position) {
//        adapter.getItem(position);

        lstPlaylist.clearAnimation();
        Log.d(Introduction.TAG, "loadAlbum: ddddd" + adapter.getItem(position).getName());
        new SongAsyncTask(new SongLoadListener() {
            @Override
            public void SongLoadListener(ArrayList<Song> list) {
                Glide.with(SongCharts.this).load(list.get(0).getCover())
                        .placeholder(R.drawable.album_thumbnail2)
                        .error(R.drawable.album_thumbnail2)
                        .override(320, 150)
                        .into(imgTop1);
                adapterSong = new OnlineSongAdapter2(SongCharts.this, list);
                lstPlaylist.setAdapter(adapterSong);
            }
        }).execute(adapter.getItem(position).getLink());
        ItemClickSupport.addTo(lstPlaylist)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View view, int position, long id) {
                        MusicService.setTracks(SongCharts.this, adapterSong.getList());
                        MusicService.playTrack(SongCharts.this, position);
                        MusicService.setState(SongCharts.this, true);

                        Intent intent = new Intent(SongCharts.this, SongPlay.class);
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
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

                    Elements playlist = doc.getElementsByClass("table-body");
                    Log.d("ddddd", "doInBackground: ssssssssss\n" + playlist.size() + "  \n" + playlist.html());
                    Elements items = playlist.first().getElementsByTag("li");
                    Log.d("ddddd", "doInBackground: ssssss size" + items.size());
                    for (Element item : items) {
                        Song song = new Song();
                        String id = item.attr("data-id");
                        String image = item.select("img").attr("src");
                        String titleName = item.select(".title-item").select("a").text();
                        String link = item.select(".fn-name").attr("href");
                        String artist = "";
                        Elements artists = item.getElementsByClass("title-sd-item");
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
