package info.trongdat.mp3cloud.Views;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import info.trongdat.mp3cloud.Presenters.Adapters.ViewPagerAdapter;
import info.trongdat.mp3cloud.Presenters.MusicService;
import info.trongdat.mp3cloud.R;
import info.trongdat.mp3cloud.Views.Tabs.CurrentPlayList;
import info.trongdat.mp3cloud.Views.Tabs.LyricsView;
import info.trongdat.mp3cloud.Views.Tabs.PlayView;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Alone on 10/19/2016.
 */

public class SongPlay extends AppCompatActivity {

    CircleIndicator indicator;
    ViewPager viewpager;
    ViewPagerAdapter mPageAdapter;
    TextView txtTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_play);
//        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));


        initialize();
    }

    @Override
    protected void onResume() {
//        MusicService.setState(this, false);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicService.setState(this, true);
    }

    public void initialize() {

        viewpager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        mPageAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mPageAdapter.addFragment(new CurrentPlayList(), "Current PlayList");
        mPageAdapter.addFragment(new PlayView(), "Play");
        mPageAdapter.addFragment(new LyricsView(), "Lyrics");
        txtTitle=(TextView)findViewById(R.id.txtTitle);
        viewpager.setAdapter(mPageAdapter);
        indicator.setViewPager(viewpager);
        viewpager.setCurrentItem(1);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                switch (position)
//                {
//                    case 0:
//                        txtTitle.setText("Danh sách phát");
//                        break;
//                    case 1:
//                        txtTitle.setText("");
//                        break;
//                    case 2:
//                        txtTitle.setText("Lời bài hát");
//                        break;
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
