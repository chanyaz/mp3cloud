package info.trongdat.mp3cloud.Views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import info.trongdat.mp3cloud.Library.GuillotineAnimation;
import info.trongdat.mp3cloud.Presenters.Callbacks.GuillotineListener;
import info.trongdat.mp3cloud.Presenters.MusicService;
import info.trongdat.mp3cloud.R;

public class MainCloud extends AppCompatActivity implements View.OnClickListener {
    ActionBar actionBar;
    View contentHamburger;
    Toolbar toolbar;
    CoordinatorLayout root;
    FrameLayout container;
    private static final long RIPPLE_DURATION = 250;

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    //    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    View guillotineMenu;
    GuillotineAnimation guillotineAnimation;
    TextView title, txtHomeOff, txtHomeOn, txtHot, txtCharts, txtTop, txtSettings;
    ColorDrawable colorTransparent;
    ColorDrawable colorPrimary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cloud);
//        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));

        define();
        setup();
    }

    public void define() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        root = (CoordinatorLayout) findViewById(R.id.root);
        container = (FrameLayout) findViewById(R.id.container);
        contentHamburger = (View) findViewById(R.id.content_hamburger);
        title = (TextView) findViewById(R.id.title);

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


    public void setup() {
//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.container, new OfflineMusic()).commit();

    }

    @Override
    public void onClick(View view) {
        title.setText("");
        actionBar.setBackgroundDrawable(colorTransparent);
        switch (view.getId()) {
            case R.id.txtHomeOffline:
                transColorItem(0);
//                FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.container, new OfflineMusic());
//                transaction.addToBackStack(null);
//                transaction.commit();
                break;
            case R.id.txtHomeOnline:
                transColorItem(1);
                title.setText("Home Online");
                actionBar.setBackgroundDrawable(colorPrimary);
//                getSupportFragmentManager().beginTransaction().replace(R.id.container, new OnlineMusic()).addToBackStack(null).commit();
//                FragmentTransaction transaction1 =getSupportFragmentManager().beginTransaction();
//                transaction1.replace(R.id.container, new OnlineMusic());
//                transaction1.addToBackStack(null);
//                transaction1.commit();
                break;
            case R.id.txtHotMusic:
                startActivity(new Intent(MainCloud.this, HotSongMore.class));
                break;
            case R.id.txtTop100:
                startActivity(new Intent(MainCloud.this, PlaylistTop100.class));
                break;
            case R.id.txtTopCharts:
                startActivity(new Intent(MainCloud.this, SongCharts.class));
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


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MusicService.getPlayingIndex() != -1)
            MusicService.setState(this, true);
        else MusicService.setState(this, false);
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
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_cloud, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setIconifiedByDefault(true);
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

}
