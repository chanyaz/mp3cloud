package info.trongdat.mp3cloud.Views.Tabs;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import info.trongdat.mp3cloud.Library.GuillotineAnimation;
import info.trongdat.mp3cloud.Models.Entities.ItemSlide;
import info.trongdat.mp3cloud.Models.Entities.OnlineAlbum;
import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.Adapters.ItemClickSupport;
import info.trongdat.mp3cloud.Presenters.Adapters.OnlineAlbumAdapter;
import info.trongdat.mp3cloud.Presenters.Adapters.OnlinePlaylistAdapter;
import info.trongdat.mp3cloud.Presenters.Adapters.OnlineSongAdapter3;
import info.trongdat.mp3cloud.Presenters.Callbacks.AlbumLoadListener;
import info.trongdat.mp3cloud.Presenters.Callbacks.GuillotineListener;
import info.trongdat.mp3cloud.Presenters.Callbacks.MyService;
import info.trongdat.mp3cloud.Presenters.Callbacks.SlideLoadListener;
import info.trongdat.mp3cloud.Presenters.Callbacks.SongLoadListener;
import info.trongdat.mp3cloud.Presenters.MusicService;
import info.trongdat.mp3cloud.R;
import info.trongdat.mp3cloud.Views.AlbumMore;
import info.trongdat.mp3cloud.Views.HotSongMore;
import info.trongdat.mp3cloud.Views.NewSongMore;
import info.trongdat.mp3cloud.Views.PlaylistMore;
import info.trongdat.mp3cloud.Views.PlaylistTop100;
import info.trongdat.mp3cloud.Views.SearchDetail;
import info.trongdat.mp3cloud.Views.SongCharts;
import info.trongdat.mp3cloud.Views.SongPlay;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static info.trongdat.mp3cloud.Views.Tabs.OfflineMusic.TAG;

/**
 * Created by Alone on 9/29/2016.
 */

public class OnlineMusic extends AppCompatActivity implements ViewPagerEx.OnPageChangeListener, View.OnClickListener {
    private SliderLayout mDemoSlider;
    private ArrayList<ItemSlide> itemSlides;
    private StaggeredGridLayoutManager lmAlbum, lmPlaylist;
    private RecyclerView lstAlbum, lstPlaylist, lstSongHot, lstSongNew;
    private ImageButton btnAlbum, btnPlaylist, btnSongHot, btnSongNew;
    private OnlineSongAdapter3 songHotAdapter, songNewAdapter;
    private static long back_pressed;
    private View contentHamburger;
    private View guillotineMenu;
    private GuillotineAnimation guillotineAnimation;
    private TextView title, txtHomeOff, txtHomeOn, txtHot, txtCharts, txtTop, txtSettings;
    private ColorDrawable colorTransparent;
    private ColorDrawable colorPrimary;
    private CoordinatorLayout root;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private static final long RIPPLE_DURATION = 250;

    private static final int TIME_DELAY = 2000;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_music_main);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);

        itemSlides = new ArrayList<>();
        initialize();

        LoadSlide loadSlide = new LoadSlide(new SlideLoadListener() {
            @Override
            public void SlideLoadListener(ArrayList<ItemSlide> list) {
                itemSlides = list;
                HashMap<String, String> mapSlide = new HashMap<String, String>();
                for (ItemSlide slide : list) {
                    mapSlide.put(slide.getName(), slide.getImage());
                }

                for (String name : mapSlide.keySet()) {
                    TextSliderView textSliderView = new TextSliderView(OnlineMusic.this);
                    // initialize a SliderLayout
                    textSliderView
                            .description(name)
                            .image(mapSlide.get(name))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Toast.makeText(OnlineMusic.this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();

                                }
                            });

                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra", name);

                    mDemoSlider.addSlider(textSliderView);
                }
//                Log.d("ddddd", "onCreateView: " + list.size());
            }

        });
        loadSlide.execute("http://mp3.zing.vn");

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Fade);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(6999);
        mDemoSlider.addOnPageChangeListener(this);

    }


//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
////        view = inflater.inflate(R.layout.online_music_main, container, false);
//
//
//        return view;
//    }

    public void initialize() {


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        contentHamburger = (View) findViewById(R.id.content_hamburger);
        title = (TextView) findViewById(R.id.title);
        root = (CoordinatorLayout) findViewById(R.id.root);

        guillotineMenu = this.getLayoutInflater().inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .setGuillotineListener(new GuillotineListener() {
                    @Override
                    public void onGuillotineOpened() {
//                        container.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onGuillotineClosed() {
//                        container.setVisibility(View.VISIBLE);
                    }
                }).build();

        guillotineMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guillotineAnimation.close();
            }
        });


        txtHomeOff = (TextView) guillotineMenu.findViewById(R.id.txtHomeOffline);
        txtHomeOff.setOnClickListener(this);
        txtHomeOn = (TextView) guillotineMenu.findViewById(R.id.txtHomeOnline);
        txtHomeOn.setTextColor(getResources().getColor(R.color.colorAccent));
        txtHomeOn.setOnClickListener(this);
        txtHot = (TextView) guillotineMenu.findViewById(R.id.txtHotMusic);
        txtHot.setOnClickListener(this);
        txtCharts = (TextView) guillotineMenu.findViewById(R.id.txtTopCharts);
        txtCharts.setOnClickListener(this);
        txtTop = (TextView) guillotineMenu.findViewById(R.id.txtTop100);
        txtTop.setOnClickListener(this);
        txtSettings = (TextView) guillotineMenu.findViewById(R.id.txtSettings);
        txtSettings.setOnClickListener(this);

        colorTransparent = new ColorDrawable(Color.TRANSPARENT);
        colorPrimary = new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark));

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            actionBar = getSupportActionBar();
            actionBar.setTitle(null);
            title.setText("Home Online");
            actionBar.setBackgroundDrawable(colorPrimary);
        }

        lmAlbum = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

//        lmAlbum.supportsPredictiveItemAnimations();
//        lmAlbum.requestSimpleAnimationsInNextLayout();
//        lmAlbum.isSmoothScrolling();
        GridLayoutManager gridAlbum = new GridLayoutManager(OnlineMusic.this, 3, GridLayoutManager.VERTICAL, false);

//        gridAlbum.setSpanSizeLookup(
//                new GridLayoutManager.SpanSizeLookup() {
//                    @Override
//                    public int getSpanSize(int position) {
//                        // 2 column size for first row
//                        return (position == 0 ? 2 : 1);
//                    }
//                });


        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);

        lstAlbum = (RecyclerView) findViewById(R.id.lstAlbum);
        lstAlbum.setHasFixedSize(false);
        lstAlbum.setLayoutManager(gridAlbum);
        lstAlbum.setItemAnimator(itemAnimator);
        lstAlbum.setNestedScrollingEnabled(false);
        AlbumAsyncTask loadAlbum = new AlbumAsyncTask(new AlbumLoadListener() {
            @Override
            public void AlbumLoadListener(ArrayList<OnlineAlbum> list) {
                OnlineAlbumAdapter onlineAlbumAdapter = new OnlineAlbumAdapter(OnlineMusic.this, list);
                lstAlbum.setAdapter(onlineAlbumAdapter);
            }
        });
        loadAlbum.execute("http://mp3.zing.vn");

        lmPlaylist = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        lmPlaylist.supportsPredictiveItemAnimations();
//        lmPlaylist.requestSimpleAnimationsInNextLayout();
//        lmPlaylist.isSmoothScrolling();
        GridLayoutManager gridPlaylist = new GridLayoutManager(OnlineMusic.this, 2, GridLayoutManager.VERTICAL, false);
        lstPlaylist = (RecyclerView) findViewById(R.id.lstPlayList);
        lstPlaylist.setLayoutManager(gridPlaylist);
        lstPlaylist.setItemAnimator(itemAnimator);
        lstPlaylist.setNestedScrollingEnabled(false);

        PlaylistAsyncTask loadPlaylist = new PlaylistAsyncTask(new AlbumLoadListener() {
            @Override
            public void AlbumLoadListener(ArrayList<OnlineAlbum> list) {
                OnlinePlaylistAdapter onlinePlaylistAdapter = new OnlinePlaylistAdapter(OnlineMusic.this, list);
                lstPlaylist.setAdapter(onlinePlaylistAdapter);
            }
        });
        loadPlaylist.execute("http://mp3.zing.vn");


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OnlineMusic.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lstSongHot = (RecyclerView) findViewById(R.id.lstSongHot);
        lstSongHot.setLayoutManager(linearLayoutManager);
        lstSongHot.setItemAnimator(itemAnimator);
        lstSongHot.setNestedScrollingEnabled(false);

        HotSongAsyncTask hotSongAsyncTask = new HotSongAsyncTask(new SongLoadListener() {
            @Override
            public void SongLoadListener(ArrayList<Song> list) {
                songHotAdapter = new OnlineSongAdapter3(OnlineMusic.this, list);
                lstSongHot.setAdapter(songHotAdapter);
            }
        });
        hotSongAsyncTask.execute("http://mp3.zing.vn");
        ItemClickSupport.addTo(lstSongHot)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View view, int position, long id) {
                        MusicService.setTracks(OnlineMusic.this, songHotAdapter.getList());
                        MusicService.playTrack(OnlineMusic.this, position);
                        MusicService.setState(OnlineMusic.this, true);

                        Intent intent = new Intent(OnlineMusic.this, SongPlay.class);
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });


        final LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(OnlineMusic.this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        lstSongNew = (RecyclerView) findViewById(R.id.lstSongNew);
        lstSongNew.setLayoutManager(linearLayoutManager2);
        lstSongNew.setItemAnimator(itemAnimator);
        lstSongNew.setNestedScrollingEnabled(false);
        NewSongAsyncTask newSongAsyncTask = new NewSongAsyncTask(new SongLoadListener() {
            @Override
            public void SongLoadListener(ArrayList<Song> list) {
                songNewAdapter = new OnlineSongAdapter3(OnlineMusic.this, list);
                lstSongNew.setAdapter(songNewAdapter);
            }
        });
        newSongAsyncTask.execute("http://mp3.zing.vn");
        ItemClickSupport.addTo(lstSongNew)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View view, int position, long id) {
                        MusicService.setTracks(OnlineMusic.this, songNewAdapter.getList());
                        MusicService.playTrack(OnlineMusic.this, position);
                        MusicService.setState(OnlineMusic.this, true);

                        Intent intent = new Intent(OnlineMusic.this, SongPlay.class);
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });


        btnAlbum = (ImageButton) findViewById(R.id.btnAlbum);
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnlineMusic.this, AlbumMore.class));
            }
        });
        btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnlineMusic.this, PlaylistMore.class));
            }
        });

        btnSongHot = (ImageButton) findViewById(R.id.btnSongHot);
        btnSongHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnlineMusic.this, HotSongMore.class));
            }
        });

        btnSongNew = (ImageButton) findViewById(R.id.btnSongNew);
        btnSongNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OnlineMusic.this, NewSongMore.class));
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtHomeOffline:
                Intent intent = new Intent(OnlineMusic.this, OfflineMusic.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.txtHomeOnline:
                break;
            case R.id.txtHotMusic:
                startActivity(new Intent(OnlineMusic.this, HotSongMore.class));
                break;
            case R.id.txtTop100:
                startActivity(new Intent(OnlineMusic.this, PlaylistTop100.class));
                break;
            case R.id.txtTopCharts:
                startActivity(new Intent(OnlineMusic.this, SongCharts.class));
                break;
        }

        guillotineAnimation.close();
    }


//    @Override
//    public void onSliderClick(BaseSliderView slider) {
//        Toast.makeText(getActivity(), slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
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
                Intent intent=new Intent(OnlineMusic.this, SearchDetail.class);
                intent.putExtra("query",query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
    private class LoadSlide extends AsyncTask<String, ItemSlide, ArrayList<ItemSlide>> {
        SlideLoadListener slideLoadListener;

        public LoadSlide(SlideLoadListener slideLoadListener) {
            this.slideLoadListener = slideLoadListener;

        }

        @Override
        protected ArrayList<ItemSlide> doInBackground(String... strings) {
            final ArrayList<ItemSlide> list = new ArrayList<>();
            try {
                // Dùng JSoup và kết nối tới link.
                Connection con = Jsoup.connect(strings[0])
                        .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                        .timeout(10000);
                Connection.Response resp = con.execute();
//                Log.d("ddddddddddddd", "doInBackground: connected");
                Document doc = null;
                if (resp.statusCode() == 200) {
                    doc = con.get();

                    Elements els = doc.getElementsByClass("slide-thumb");
                    for (Element el : els) {
                        Elements s = el.getElementsByClass("dot");
                        for (Element m : s) {
                            final ItemSlide slide = new ItemSlide();

                            String image = m.select("img").attr("src");
                            String name = m.select("img").attr("alt");
                            slide.setName(name).setImage(image).setUrl("");
//                            Log.d("dddddddddddddd", "doInBackground: " + name + "      " + image);
                            list.add(slide);
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
        protected void onPostExecute(ArrayList<ItemSlide> itemSlides) {
            slideLoadListener.SlideLoadListener(itemSlides);
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
                    Element albumContainer = doc.getElementById("albumHot");
                    Elements rows = albumContainer.getElementsByClass("row");
                    for (Element row : rows) {
                        Elements items = row.getElementsByClass("album-item");
                        for (Element item : items) {
                            final OnlineAlbum album1 = new OnlineAlbum();

                            String image = item.select("img").attr("src");
                            String titleName = "";
                            String artistName = "";
                            String link = "";
                            Elements titles = item.getElementsByClass("des");
                            for (Element title : titles) {
                                titleName = title.select("a").text();
                                link = title.select("a").attr("href");
                            }

                            Elements artists = item.getElementsByClass("inblock");
                            for (Element artist : artists) {
                                artistName = artist.select("a").text();
                            }
                            Log.d("dddddddddddddd", "doInBackground: album" + artistName + "  " + titleName + "      " + image + "  " + link);
                            album1.setName(titleName).setImage(image).setArtist(artistName).setLink(link);
                            if (list.size() < 9)
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


    // CLASS ASYNC LOAD PLAYLIST.
    private class PlaylistAsyncTask extends AsyncTask<String, OnlineAlbum, ArrayList<OnlineAlbum>> {
        AlbumLoadListener albumLoadListener;

        public PlaylistAsyncTask(AlbumLoadListener slideLoadListener) {
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
                    Element albumContainer = doc.getElementById("albumCollection");
                    Elements sections = albumContainer.getElementsByClass("section");
                    Elements rows = sections.first().select(".row");
                    Elements actives = rows.first().select(".col-3");
                    for (Element active : actives) {
                        final OnlineAlbum album1 = new OnlineAlbum();

                        String image = active.select("img").attr("data-lazy");
                        String titleName = active.select("a").attr("title");
                        String link = "http://mp3.zing.vn" + active.select("a").attr("href");
                        Log.d("dddddddddddddd", "doInBackground playlist: " + "  " + titleName + "      " + image + "   " + link);
                        album1.setName(titleName).setImage(image).setLink(link);
                        if (list.size() < 4)
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


    // CLASS ASYNC LOAD HOT SONG
    private class HotSongAsyncTask extends AsyncTask<String, Song, ArrayList<Song>> {
        SongLoadListener songLoadListener;
        MyService myService;

        public HotSongAsyncTask(SongLoadListener songLoadListener) {
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

                    Element hotSong = doc.getElementById("viet-hot-song");
                    Elements items = hotSong.getElementsByClass("fn-song");

                    for (Element item : items) {
                        Song song = new Song();
                        String id = item.attr("data-id");
                        String image = item.select("img").attr("src");
                        String titleName = item.select(".txt-primary").first().select("a").text();
                        String link = item.select(".txt-primary").first().select("a").attr("href");
                        String artist = item.select(".inblock").first().select("a").text();
                        song.setSongName(titleName).setCover(image).setArtist(artist).setLink(link);

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

    @Override
    public void onBackPressed() {

        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } else {
            Toast.makeText(getApplicationContext(), "Press again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    private class NewSongAsyncTask extends AsyncTask<String, Song, ArrayList<Song>> {
        SongLoadListener songLoadListener;
        MyService myService;

        public NewSongAsyncTask(SongLoadListener songLoadListener) {
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

                    Element hotSong = doc.getElementById("viet-new-song");
                    Elements items = hotSong.getElementsByClass("fn-song");

                    for (Element item : items) {

                        Song song = new Song();
                        String id = item.attr("data-id");
                        String image = item.select("img").attr("src");
                        String titleName = item.select(".txt-primary").first().select("a").text();
                        String link = item.select(".txt-primary").first().select("a").attr("href");
                        String artist = item.select(".inblock").first().select("a").text();

                        Log.d("dddddddddddddd", "doInBackground new song: " + "  " + titleName + "      " + image + "   " + link);
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

