package info.trongdat.mp3cloud.Views;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.Adapters.ItemClickSupport;
import info.trongdat.mp3cloud.Presenters.Adapters.OnlineSongAdapter4;
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

public class SearchDetail extends AppCompatActivity {
    private TextView txtRequest;
    private RecyclerView lstResult;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private String url = "http://mp3.zing.vn/tim-kiem/bai-hat.html?q=";
    OnlineSongAdapter4 adapterSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_detail);
        txtRequest = (TextView) findViewById(R.id.txtRequest);
        lstResult = (RecyclerView) findViewById(R.id.lstResult);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Search");

        Bundle extras = getIntent().getExtras();
        String query = extras.getString("query");

        txtRequest.setText("Result for: " + query);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lstResult.setLayoutManager(linearLayoutManager);

        new SongAsyncTask(new SongLoadListener() {
            @Override
            public void SongLoadListener(ArrayList<Song> list) {
                adapterSong = new OnlineSongAdapter4(SearchDetail.this, list);
                lstResult.setAdapter(adapterSong);
            }
        }).execute(url+query.replaceAll("", "%20"));

        ItemClickSupport.addTo(lstResult)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View view, int position, long id) {
                        MusicService.setTracks(SearchDetail.this, adapterSong.getList());
                        MusicService.playTrack(SearchDetail.this, position);
                        MusicService.setState(SearchDetail.this, true);

                        Intent intent = new Intent(SearchDetail.this, SongPlay.class);
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_cloud, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                txtRequest.setText("Result for: " + newText);
                new SongAsyncTask(new SongLoadListener() {
                    @Override
                    public void SongLoadListener(ArrayList<Song> list) {
                        adapterSong = new OnlineSongAdapter4(SearchDetail.this, list);
                        lstResult.setAdapter(adapterSong);
                        adapterSong.notifyDataSetChanged();
                    }
                }).execute(url + newText.replace(" ","%20"));
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

                    Elements items = doc.getElementsByClass("item-song");
                    for (Element item : items) {
                        Song song = new Song();
                        String id = item.attr("data-id");
//                        String image = item.select("img").attr("src");
                        String titleName = item.select(".title-song").select("a").attr("title");
                        String link = item.select(".txt-primary").attr("href");
                        String artist = "";
                        Elements artists = item.getElementsByClass("info-meta");
                        for (Element element : artists) {
                            artist += element.select("a").text() + ", ";
                        }
                        if (artist.endsWith(", "))
                            artist = artist.substring(0, artist.length() - 2);

                        song.setSongIDencode(id).setSongName(titleName).setArtist(artist).setLink(link);

//                        // Get các thông tin của bài hát tương ứng với mã bài hát (qua API Zing cung cấp ).
                        final String songID = "{\"id\":\"" + id + "\"}";
                        myService.getSong(songID).enqueue(new Callback<Song>() {
                            @Override
                            public void onResponse(Call<Song> call, Response<Song> response) {
                                song.setPath(response.body().getPath()).setCover("http://image.mp3.zdn.vn/thumb/240_240/" + response.body().getCover());
                            }

                            @Override
                            public void onFailure(Call<Song> call, Throwable t) {
                                Log.e("DAT", t.getMessage() + "");
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
                                Log.e("DAT", t.getMessage() + "");
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
