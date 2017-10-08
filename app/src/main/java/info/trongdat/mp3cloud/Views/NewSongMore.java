package info.trongdat.mp3cloud.Views;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.OnlineAlbum;
import info.trongdat.mp3cloud.Presenters.Adapters.OnlineAlbumAdapter3;
import info.trongdat.mp3cloud.Presenters.Callbacks.AlbumLoadListener;
import info.trongdat.mp3cloud.R;

public class NewSongMore extends AppCompatActivity {
    private RecyclerView lstPlaylist;
    private TextView txtAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_song_more);
        initialize();
    }

    public void initialize() {
        lstPlaylist = (RecyclerView) findViewById(R.id.lstAlbum);
        txtAlbum=(TextView)findViewById(R.id.txtAlbum);
        txtAlbum.setText("PLAYLIST CHỌN LỌC NHẠC VIỆT MỚI");
        GridLayoutManager gridAlbum = new GridLayoutManager(NewSongMore.this, 2, GridLayoutManager.VERTICAL, false);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);

        lstPlaylist.setHasFixedSize(true);
        lstPlaylist.setLayoutManager(gridAlbum);
        lstPlaylist.setItemAnimator(itemAnimator);
//        Log.d(Introduction.TAG, "loadAlbum: ddddd" + adapter.getItem(position).getName());
        AlbumAsyncTask loadAlbum = new AlbumAsyncTask(new AlbumLoadListener() {
            @Override
            public void AlbumLoadListener(ArrayList<OnlineAlbum> list) {
                OnlineAlbumAdapter3 adapter = new OnlineAlbumAdapter3(NewSongMore.this, list);
                lstPlaylist.setAdapter(adapter);
            }
        });
        loadAlbum.execute("http://mp3.zing.vn/chu-de/nhac-viet-moi/IWZ9Z0ED.html?view=playlist");
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
                    Element albumContainer = doc.select(".section.mt0").first();
                    Elements rows = albumContainer.getElementsByClass("row");
                    for (Element row : rows) {
                        Elements items = row.getElementsByClass("album-item");
                        for (Element item : items) {
                            final OnlineAlbum album1 = new OnlineAlbum();

                            String titleName = item.select(".title-item").first().text();
                            String image = item.select("img").attr("src");
                            String artistName = item.select(".title-sd-item").first().text();
                            String link = item.select("a").attr("href");
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
