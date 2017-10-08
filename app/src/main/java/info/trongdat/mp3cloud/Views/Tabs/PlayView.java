package info.trongdat.mp3cloud.Views.Tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.MusicService;
import info.trongdat.mp3cloud.Presenters.SQLiteHandler;
import info.trongdat.mp3cloud.R;
import info.trongdat.mp3cloud.Utils.TimeUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by Alone on 11/15/2016.
 */

public class PlayView extends Fragment implements View.OnClickListener {
    private View view;
    private AudioVisualization audioVisualization;
    private FrameLayout detail_play;
    private TextView txtTitle;
    private ShineButton btnPlayPause, btnFavorite;
    private ImageButton btnNext, btnPrevious, btnRepeat;
    private SeekBar seekDuration;
    private boolean lopping = false;
    private List<Song> songs;
    public static int position = 0;
    private CircleImageView imageAlbum;

    private BroadcastReceiver brUpdate;
    private BroadcastReceiver brPlayPause;
    private BroadcastReceiver brPlayTrack;
    private BroadcastReceiver brSeekPosition;

    private TextView txtCurrentTime, txtDuration;
    private SQLiteHandler db;
    private Song song;
    int favorite = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_detail_play, container, false);
        detail_play = (FrameLayout) view.findViewById(R.id.detail_play);
        audioVisualization = (AudioVisualization) new GLAudioVisualizationView.Builder(getContext())
                .setBubblesSize(R.dimen.bubble_size)
                .setBubblesRandomizeSize(true)
                .setWavesHeight(R.dimen.wave_height)
                .setWavesFooterHeight(R.dimen.footer_height)
                .setWavesCount(7)
                .setLayersCount(5)
                .setBackgroundColorRes(R.color.av_color_bg)
                .setLayerColors(R.array.av_colors)
                .setBubblesPerLayer(50)
                .build();
        audioVisualization.linkTo(DbmHandler.Factory.newVisualizerHandler(getContext(), 0));

        detail_play.addView((View) audioVisualization, 0);

        initialize();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getActivity());
    }

    public void initialize() {
        txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        imageAlbum = (CircleImageView) view.findViewById(R.id.imageAlbum);

        seekDuration = (SeekBar) view.findViewById(R.id.seekDuration);
        txtCurrentTime = (TextView) view.findViewById(R.id.txtCurrentTime);
        txtDuration = (TextView) view.findViewById(R.id.txtDuration);

        btnPlayPause = (ShineButton) view.findViewById(R.id.btnPlayPause);
        btnPlayPause.setOnClickListener(this);

        btnNext = (ImageButton) view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
        btnPrevious = (ImageButton) view.findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(this);
        btnRepeat = (ImageButton) view.findViewById(R.id.btnRepeat);
        btnRepeat.setOnClickListener(this);
        btnFavorite = (ShineButton) view.findViewById(R.id.btnFavorite);

        btnFavorite.setOnClickListener(this);

        songs = MusicService.getTracks();
        position = MusicService.getCurrentSong() != -1 ? MusicService.getCurrentSong() : 0;
        refresh();


        seekDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b)
                    MusicService.seekTo(getActivity(), i);
                seekDuration.setProgress(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


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

        brUpdate = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processReceive(context, intent);
            }
        };
        brSeekPosition = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processReceive(context, intent);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_PLAY_TRACK);
        filter.addAction(MusicService.ACTION_PLAY_PAUSE);
        filter.addAction(MusicService.ACTION_SEEK_POSITION);

        getActivity().registerReceiver(brPlayTrack, filter);
        getActivity().registerReceiver(brPlayPause, filter);
        getActivity().registerReceiver(brUpdate, filter);
        getActivity().registerReceiver(brSeekPosition, filter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlayPause:
                MusicService.playPauseClick(getActivity());
//                MusicService.setState(getActivity(), false);
                break;
            case R.id.btnNext:
                MusicService.nextClick(getActivity());
                break;
            case R.id.btnPrevious:
                MusicService.previousClick(getActivity());
                break;
            case R.id.btnRepeat:
                lopping = !lopping;
                MusicService.loopingClick(lopping);
                if (lopping) {
                    btnRepeat.setImageResource(R.drawable.ic_play_mode_single);
                } else {
                    btnRepeat.setImageResource(R.drawable.ic_play_mode_list);
                }
                break;
            case R.id.btnFavorite:
                String sql;
                if (favorite == 0) {
                    btnFavorite.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp2);
//                    btnFavorite.setChecked(true);
                    favorite = 1;
                    sql = "UPDATE Songs SET favorite=1 WHERE songID=" + song.getSongID();
                } else {
                    btnFavorite.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
//                    btnFavorite.setChecked(false);
                    favorite = 0;
                    sql = "UPDATE Songs SET favorite=0 WHERE songID=" + song.getSongID();
                }
                db.execute(sql);

                break;
        }
    }


    public void refresh() {
//        mediaPlayer = MusicService.getMediaPlayer();

//        duration = mediaPlayer.getDuration();
//        Toast.makeText(getActivity(), "Leng of song: " + mediaPlayer.getDuration(), Toast.LENGTH_LONG).show();
        song = songs.get(position);
        txtTitle.setText(song.getSongName());

        Picasso.with(getActivity())
                .load(songs.get(position).getCover())
                .placeholder(R.drawable.album_cover_two_door)
                .error(R.drawable.album_cover_two_door)
                .into(imageAlbum);

        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), Uri.parse(song.getPath().getLossless()));
        seekDuration.setMax(mediaPlayer.getDuration());
        txtDuration.setText(TimeUtils.formatDuration(mediaPlayer.getDuration()));
        Cursor cursor = db.rawQuery("SELECT favorite FROM Songs WHERE songID=" + song.getSongID());
        if (cursor.moveToFirst()&&cursor.getCount()!=0) {
            favorite = cursor.getInt(0);
            if (favorite == 0) {
                btnFavorite.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                btnFavorite.setChecked(false);
            } else {
                btnFavorite.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp2);
                btnFavorite.setChecked(true);
            }
        }

    }

    public void processReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        switch (intent.getAction()) {
            case MusicService.ACTION_PLAY_TRACK:
                int index = intent.getExtras().getInt("playingIndex");
                position = index;
                refresh();
                break;
            case MusicService.ACTION_PLAY_PAUSE:
                boolean playing = intent.getExtras().getBoolean("playing");
                if (playing) {
                    btnPlayPause.setBackgroundResource(R.drawable.ic_pause);
                    btnPlayPause.setChecked(true);
                } else {
                    btnPlayPause.setBackgroundResource(R.drawable.ic_play);
                    btnPlayPause.setChecked(false);
                }
//                Toast.makeText(context, "PLAYING: " + playing, Toast.LENGTH_LONG).show();
                break;
            case MusicService.ACTION_SEEK_POSITION:
                int currentPosition = intent.getExtras().getInt("currentPosition");
                Log.d(TAG, "processReceive: pos" + currentPosition);
                seekDuration.setProgress(currentPosition);
                txtCurrentTime.setText(TimeUtils.formatDuration(currentPosition));
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        audioVisualization.onResume();
        MusicService.setState(getActivity(), false);

        if (MusicService.isPaused()) {
            btnPlayPause.setChecked(false);
            btnPlayPause.setBackgroundResource(R.drawable.ic_play);
        } else {
            btnPlayPause.setChecked(true);
            btnPlayPause.setBackgroundResource(R.drawable.ic_pause);
        }


    }

    @Override
    public void onPause() {
        audioVisualization.onPause();
        MusicService.setState(getActivity(), true);
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MusicService.setState(getActivity(), true);
    }

    @Override
    public void onDestroyView() {
        audioVisualization.release();
        MusicService.setState(getActivity(), true);
        getActivity().unregisterReceiver(brPlayTrack);
        getActivity().unregisterReceiver(brPlayPause);
        getActivity().unregisterReceiver(brUpdate);
        getActivity().unregisterReceiver(brSeekPosition);
        super.onDestroyView();
    }

}
