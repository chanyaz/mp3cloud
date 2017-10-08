package info.trongdat.mp3cloud.Views;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.OnlineAlbum;
import info.trongdat.mp3cloud.Presenters.Adapters.OnlineAlbumAdapter;
import info.trongdat.mp3cloud.Presenters.Adapters.OnlineAlbumAdapter2;
import info.trongdat.mp3cloud.Presenters.Callbacks.AlbumLoadListener;
import info.trongdat.mp3cloud.R;
import info.trongdat.mp3cloud.Views.Tabs.OnlineMusic;

public class AlbumMore extends AppCompatActivity {
    Spinner spinAlbum;
    RecyclerView lstAlbum;
    ArrayAdapter<OnlineAlbum> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_more);
        initialize();
    }

    public void initialize() {
        spinAlbum = (Spinner) findViewById(R.id.spinAlbum);
        lstAlbum = (RecyclerView) findViewById(R.id.lstAlbum);
        AlbumNameAsyncTask loadAlbumName = new AlbumNameAsyncTask(new AlbumLoadListener() {
            @Override
            public void AlbumLoadListener(ArrayList<OnlineAlbum> list) {
                adapter = new ArrayAdapter<>(AlbumMore.this, R.layout.simple_spinner_item_1, list);
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                spinAlbum.setAdapter(adapter);
            }
        });
        loadAlbumName.execute("http://mp3.zing.vn/the-loai-album.html");

        spinAlbum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadAlbum(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    public void loadAlbum(int position) {
//        adapter.getItem(position);
        GridLayoutManager gridAlbum = new GridLayoutManager(AlbumMore.this, 2 , GridLayoutManager.VERTICAL, false);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);

        lstAlbum.setHasFixedSize(true);
        lstAlbum.setLayoutManager(gridAlbum);
        lstAlbum.setItemAnimator(itemAnimator);
        Log.d(Introduction.TAG, "loadAlbum: ddddd" + adapter.getItem(position).getName());
        AlbumAsyncTask loadAlbum = new AlbumAsyncTask(new AlbumLoadListener() {
            @Override
            public void AlbumLoadListener(ArrayList<OnlineAlbum> list) {
                OnlineAlbumAdapter2 adapter = new OnlineAlbumAdapter2(AlbumMore.this, list);
                lstAlbum.setAdapter(adapter);
            }
        });
        loadAlbum.execute(adapter.getItem(position).getLink());
    }

    // CLASS ASYNC LOAD ALBUM.
    private class AlbumNameAsyncTask extends AsyncTask<String, OnlineAlbum, ArrayList<OnlineAlbum>> {
        AlbumLoadListener albumLoadListener;

        public AlbumNameAsyncTask(AlbumLoadListener slideLoadListener) {
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
                    Element albumContainer = doc.getElementsByClass("zcontent").first();
                    Elements items = albumContainer.getElementsByClass("title-section");
                    for (Element item : items) {
                        final OnlineAlbum album1 = new OnlineAlbum();

                        String titleName = item.select("a").text();
                        String link = item.select("a").attr("href") + "?sort=hot&filter=day";
                        Log.d("dddddddddddddd", "doInBackground:aaaaaaa album" + titleName + "      " + link);
                        album1.setName(titleName).setLink(link);
                        if (list.size() < 9)
                            list.add(album1);

                    }
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
                    Element albumContainer = doc.getElementsByClass("tab-pane").first();
                    Elements rows = albumContainer.getElementsByClass("row");
                    for (Element row : rows) {
                        Elements items = row.getElementsByClass("item");
                        for (Element item : items) {
                            final OnlineAlbum album1 = new OnlineAlbum();

                            String titleName = item.select(".title-item").first().select("a").text();
                            String image = item.select("img").attr("src");
                            String artistName = item.select(".title-sd-item").first().select("a").text();
                            String link = item.select(".thumb").attr("href");
                            Log.d("dddddddddddddd", "doInBackground:aaaaaaa album" + titleName + "  " + link);
                            album1.setName(titleName).setLink(link).setImage(image).setArtist(artistName);

                            list.add(album1);

                        }
                    }
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
}
