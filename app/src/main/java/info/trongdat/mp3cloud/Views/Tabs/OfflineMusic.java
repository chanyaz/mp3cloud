package info.trongdat.mp3cloud.Views.Tabs;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gigamole.navigationtabbar.ntb.NavigationTabBar;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Library.GuillotineAnimation;
import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.Adapters.ViewPagerAdapter;
import info.trongdat.mp3cloud.Presenters.Callbacks.GuillotineListener;
import info.trongdat.mp3cloud.Presenters.MusicService;
import info.trongdat.mp3cloud.R;
import info.trongdat.mp3cloud.Views.HotSongMore;
import info.trongdat.mp3cloud.Views.PlaylistTop100;
import info.trongdat.mp3cloud.Views.SearchDetail;
import info.trongdat.mp3cloud.Views.SongCharts;
import info.trongdat.mp3cloud.Views.SongPlay;

/**
 * Created by Alone on 9/29/2016.
 */

public class OfflineMusic extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "ptdcloud.com";
//    View view;

    private TextView txtTitle;
    private NavigationTabBar navigationTabBar;
    private ViewPager viewPager;

    private static long back_pressed;
    private BroadcastReceiver brPlayTrack;
    private BroadcastReceiver brPlayPause;
    private FloatingActionButton btnPlayPause;
    private ViewPagerAdapter pagerAdapter;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_music_main);

        define();

        brPlayTrack = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                processReceive(context, intent);
            }
        };
        brPlayPause = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                processReceive(context, intent);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_PLAY_TRACK);
        filter.addAction(MusicService.ACTION_PLAY_PAUSE);

        registerReceiver(brPlayTrack, filter);
        registerReceiver(brPlayPause, filter);
    }

    //    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate: offline");
////        Toast.makeText(getActivity(), "create", Toast.LENGTH_LONG).show();
//    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.offline_music_main, container, false);
//        Log.d(TAG, "onCreateView: off");
//        define();
////        setup();
////        Toast.makeText(getActivity(), "create view", Toast.LENGTH_LONG).show();
//
//        return view;
//    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: offline");
        setup();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: offline");
//        onStop();
//        onDestroyView();
//        onDestroy();
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        Toast.makeText(getActivity(), "Resume ok1", Toast.LENGTH_LONG).show();
//
//
//    }
//
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        Toast.makeText(getActivity(), "attach 1", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        Toast.makeText(getActivity(), "start", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Toast.makeText(getActivity(), "destroy", Toast.LENGTH_LONG).show();
//    }
//
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        Toast.makeText(getActivity(), "attach 2", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        Toast.makeText(getActivity(), "detach", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        Toast.makeText(getActivity(), "stop", Toast.LENGTH_LONG).show();
//
//    }
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        Toast.makeText(getActivity(), "pause  ok1", Toast.LENGTH_LONG).show();
//    }

    public void define() {
        Log.d(TAG, "define: offline");
        viewPager = (ViewPager) findViewById(R.id.vp_vertical_ntb);
        navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_vertical);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        btnPlayPause = (FloatingActionButton) findViewById(R.id.btnPlayPause);
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicService.playPauseClick(OfflineMusic.this);

                if (MusicService.isPaused()) {
                    Intent intent = new Intent(OfflineMusic.this, SongPlay.class);
                    startActivity(intent);
                }
            }
        });


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
        txtHomeOff.setTextColor(getResources().getColor(R.color.colorAccent));
        txtHomeOff.setOnClickListener(this);
        txtHomeOn = (TextView) guillotineMenu.findViewById(R.id.txtHomeOnline);
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
            title.setText("");
            actionBar.setBackgroundDrawable(colorTransparent);
        }

    }

    @Override
    public void onClick(View view) {
        title.setText("");
        actionBar.setBackgroundDrawable(colorTransparent);
        switch (view.getId()) {
            case R.id.txtHomeOffline:
                break;
            case R.id.txtHomeOnline:
                Intent intent = new Intent(OfflineMusic.this, OnlineMusic.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.txtHotMusic:
                startActivity(new Intent(OfflineMusic.this, HotSongMore.class));
                break;
            case R.id.txtTop100:
                startActivity(new Intent(OfflineMusic.this, PlaylistTop100.class));
                break;
            case R.id.txtTopCharts:
                startActivity(new Intent(OfflineMusic.this, SongCharts.class));
                break;
        }

        guillotineAnimation.close();
    }

    public void transColorItem(int position) {
        switch (position) {
            case 0:
                txtHomeOff.setTextColor(getResources().getColor(R.color.colorAccent));
                txtHomeOn.setTextColor(Color.WHITE);
                txtHot.setTextColor(Color.WHITE);
                txtCharts.setTextColor(Color.WHITE);
                txtTop.setTextColor(Color.WHITE);
                txtSettings.setTextColor(Color.WHITE);
                break;
            case 1:
                txtHomeOff.setTextColor(Color.WHITE);
                txtHomeOn.setTextColor(getResources().getColor(R.color.colorAccent));
                txtHot.setTextColor(Color.WHITE);
                txtCharts.setTextColor(Color.WHITE);
                txtTop.setTextColor(Color.WHITE);
                txtSettings.setTextColor(Color.WHITE);
                break;
            case 2:
                txtHomeOff.setTextColor(Color.WHITE);
                txtHomeOn.setTextColor(Color.WHITE);
                txtHot.setTextColor(getResources().getColor(R.color.colorAccent));
                txtCharts.setTextColor(Color.WHITE);
                txtTop.setTextColor(Color.WHITE);
                txtSettings.setTextColor(Color.WHITE);
                break;
            case 3:
                txtHomeOff.setTextColor(Color.WHITE);
                txtHomeOn.setTextColor(Color.WHITE);
                txtHot.setTextColor(Color.WHITE);
                txtCharts.setTextColor(getResources().getColor(R.color.colorAccent));
                txtTop.setTextColor(Color.WHITE);
                txtSettings.setTextColor(Color.WHITE);
                break;
            case 4:
                txtHomeOff.setTextColor(Color.WHITE);
                txtHomeOn.setTextColor(Color.WHITE);
                txtHot.setTextColor(Color.WHITE);
                txtCharts.setTextColor(Color.WHITE);
                txtTop.setTextColor(getResources().getColor(R.color.colorAccent));
                txtSettings.setTextColor(Color.WHITE);
                break;
            case 5:
                txtHomeOff.setTextColor(Color.WHITE);
                txtHomeOn.setTextColor(Color.WHITE);
                txtHot.setTextColor(Color.WHITE);
                txtCharts.setTextColor(Color.WHITE);
                txtTop.setTextColor(Color.WHITE);
                txtSettings.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
        }
    }


    public void setup() {

        txtTitle.setSelected(true);
        txtTitle.setEnabled(true);
        txtTitle.setFocusable(false);

        final String[] colors = getResources().getStringArray(R.array.vertical_ntb);

        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_song),
                        Color.parseColor(colors[0]))
                        .badgeTitle("Songs")
                        .title("Songs")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_album2),
                        Color.parseColor(colors[1]))
                        .badgeTitle("Albums")
                        .title("Albums")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_playlist2),
                        Color.parseColor(colors[2]))
                        .badgeTitle("Artists")
                        .title("Artists")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_playlist3),
                        Color.parseColor(colors[3]))
                        .badgeTitle("Playlists")
                        .title("Playlists")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_favorite),
                        Color.parseColor(colors[4]))
                        .badgeTitle("Favorite")
                        .title("Favorite")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_download),
                        Color.parseColor(colors[5]))
                        .badgeTitle("Download")
                        .title("Download")
                        .build()
        );

        Log.d(TAG, "setup: add models");
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new SongView(), "Songs");
        pagerAdapter.addFragment(new AlbumView(), "Albums");
        pagerAdapter.addFragment(new ArtistView(), "Artists");
        pagerAdapter.addFragment(new PlaylistView(), "Playlist");
        pagerAdapter.addFragment(new FavoriteView(), "Favorite");
        pagerAdapter.addFragment(new DownloadView(), "Download");
        viewPager.setAdapter(pagerAdapter);
        Log.d(TAG, "setup: add fragment");
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        Log.d(TAG, "setup: setviewpager");
    }

    public void setupViewPager() {

//        viewPager.setPageTransformer(true, new AccordionTransformer());
    }

    public void processReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case MusicService.ACTION_PLAY_TRACK:
                int index = intent.getExtras().getInt("playingIndex");
                ArrayList<Song> songs = MusicService.getTracks();
                txtTitle.setText(songs.get(index).getSongName());
                btnPlayPause.setImageResource(R.drawable.aw_ic_pause);
                break;
            case MusicService.ACTION_PLAY_PAUSE:
                boolean playing = intent.getExtras().getBoolean("playing");
                if (playing) {
                    btnPlayPause.setImageResource(R.drawable.aw_ic_pause);
                } else {
                    btnPlayPause.setImageResource(R.drawable.aw_ic_play);
                }
//                Toast.makeText(context, "PLAYING: " + playing, Toast.LENGTH_LONG).show();
                break;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_cloud, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setIconifiedByDefault(true);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                if (searchView.isIconfiedByDefault()) actionBar.setBackgroundDrawable(colorPrimary);
                else actionBar.setBackgroundDrawable(colorTransparent);

                return false;
            }
        });
//        searchView.setSubmitButtonEnabled(true);
        searchView.setElevation(5);
//        searchView.setBackgroundResource(R.color.colorPrimaryDark);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                actionBar.setBackgroundDrawable(colorTransparent);
                Intent intent = new Intent(OfflineMusic.this, SearchDetail.class);
                intent.putExtra("query", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                actionBar.setBackgroundDrawable(colorPrimary);
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

    @Override
    protected void onDestroy() {
        unregisterReceiver(brPlayTrack);
        unregisterReceiver(brPlayPause);

        super.onDestroy();
    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        getActivity().unregisterReceiver(brPlayTrack);
//        getActivity().unregisterReceiver(brPlayPause);
////        Toast.makeText(getActivity(), "destroy view", Toast.LENGTH_LONG).show();
//        pagerAdapter.removeAll();
//    }

}
