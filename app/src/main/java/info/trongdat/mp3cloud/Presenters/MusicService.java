package info.trongdat.mp3cloud.Presenters;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import info.trongdat.mp3cloud.AudioWidget.AudioWidget;
import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.R;
import info.trongdat.mp3cloud.Views.MainCloud;
import info.trongdat.mp3cloud.Views.SongPlay;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Simple implementation of music service.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, AudioWidget.OnControlsClickListener, AudioWidget.OnWidgetStateChangedListener {

    private static final String TAG = "MusicService";
    public static final String ACTION_CHANGE_STATE = "ACTION_CHANGE_STATE";
    public static final String ACTION_COMPLETE = "ACTION_COMPLETE";
    public static final String ACTION_SET_TRACK = "ACTION_SET_TRACK";
    public static final String ACTION_PLAY_TRACK = "ACTION_PLAY_TRACK";
    public static final String ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";
    public static final String ACTION_SEEK_POSITION = "ACTION_SEEK_POSITION";
    private static final String EXTRA_SELECT_TRACK = "EXTRA_SELECT_TRACK";
    private static final String EXTRA_CHANGE_STATE = "EXTRA_CHANGE_STATE";


    private static final long UPDATE_INTERVAL = 1000;
    private static final String KEY_POSITION_X = "position_x";
    private static final String KEY_POSITION_Y = "position_y";
    private static ArrayList<Song> tracks;
    private static final ArrayList<Song> items = new ArrayList<>();
    private static AudioWidget audioWidget;
    public static MediaPlayer mediaPlayer;
    private boolean preparing;
    private static int playingIndex = -1;
    private static boolean paused;
    private static Timer timer;
    private static CropCircleTransformation cropCircleTransformation;
    private static SharedPreferences preferences;

    private Handler mHandler = new Handler();
    private static Context mContext;

    public static void setTracks(@NonNull Context context, @NonNull ArrayList<Song> tracks) {
        mContext = context;

        Intent intent = new Intent(ACTION_SET_TRACK, null, context, MusicService.class);
        MusicService.tracks = tracks;
        Log.e("DATTTTTTTTTTTT", "set trackssssssssssssssssssssssssssssss");
        context.startService(intent);
    }

    public static void playTrack(@NonNull Context context, @NonNull int position) {
        mContext = context;
        Intent intent = new Intent(ACTION_PLAY_TRACK, null, context, MusicService.class);

        Log.e("DATTTTTTTTTTTT", "PLAY trackssssssssssssssssssssssssssssss");
        intent.putExtra("position", position);
        context.startService(intent);
    }

    public static void setState(@NonNull Context context, boolean isShowing) {
        mContext = context;

        Intent intent = new Intent(ACTION_CHANGE_STATE, null, context, MusicService.class);
        intent.putExtra(EXTRA_CHANGE_STATE, isShowing);
        context.startService(intent);
    }


    public static void playPauseClick(@NonNull Context context) {
        mContext = context;

        Intent intent = new Intent(ACTION_PLAY_PAUSE, null, context, MusicService.class);
        context.startService(intent);
    }

    public static boolean isPaused() {
        return paused;
    }

    public static int getPlayingIndex() {
        return playingIndex;
    }

    public static void nextClick(@NonNull Context context) {
        mContext = context;


        Intent intent = new Intent(ACTION_NEXT, null, context, MusicService.class);
        context.startService(intent);
    }

    public static void previousClick(@NonNull Context context) {
        mContext = context;


        Intent intent = new Intent(ACTION_PREVIOUS, null, context, MusicService.class);
        context.startService(intent);
    }

    public static void seekTo(@NonNull Context context, int position) {
        mContext = context;

        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        } else {
            Toast.makeText(context, "Not song", Toast.LENGTH_SHORT).show();
        }
    }

    public static void loopingClick(boolean loop) {
        if (playingIndex != -1) {
            mediaPlayer.setLooping(loop);
        }
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static ArrayList<Song> getTracks() {
        return items;
    }

    public static int getCurrentSong() {
        return playingIndex;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        audioWidget = new AudioWidget.Builder(this).build();
        audioWidget.controller().onControlsClickListener(this);
        audioWidget.controller().onWidgetStateChangedListener(this);
        cropCircleTransformation = new CropCircleTransformation(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_SET_TRACK: {
                    updateTracks();
                    break;
                }
                case ACTION_PLAY_TRACK: {
                    selectNewTrack(intent);
                    break;
                }
                case ACTION_PLAY_PAUSE: {
                    playPauseClick();
                    break;
                }
                case ACTION_NEXT: {
                    onNextClicked();
                    break;
                }
                case ACTION_PREVIOUS: {
                    onPreviousClicked();
                    break;
                }
                case ACTION_CHANGE_STATE: {
                    if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))) {
                        boolean show = intent.getBooleanExtra(EXTRA_CHANGE_STATE, false);
                        if (show) {
                            audioWidget.show(preferences.getInt(KEY_POSITION_X, 100), preferences.getInt(KEY_POSITION_Y, 100));
                        } else {
                            audioWidget.hide();
                        }
                    } else {
                        Log.w(TAG, "Can't change audio widget state! Device does not have drawOverlays permissions!");
                    }
                    break;
                }
            }
        }
        return START_STICKY;
    }

    private void selectNewTrack(Intent intent) {
        if (preparing) {
            return;
        }
//        Song item = intent.getParcelableExtra(EXTRA_SELECT_TRACK);
        int position = intent.getExtras().getInt("position");

        Log.d(TAG, "selectNewTrack: xxxxxxxxxxxxxxxxxxxxxxxxxxx " + position);

        Song item = items.get(position);

        //Nếu không lấy được item và media chưa phát hoặc đang phát mà chọn bài hiện tại.
        if (item == null && playingIndex == -1 || playingIndex != -1 && items.get(playingIndex).equals(item)) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                audioWidget.controller().start();
            } else {
                mediaPlayer.start();
                audioWidget.controller().pause();
            }
//            Toast.makeText(getApplication().getApplicationContext(), "Null Song", Toast.LENGTH_LONG).show();
            return;
        }

        Log.e("DATTTTTTTTTTTT", "SELECT trackssssssssssssssssssssssssssssss");
//        playingIndex = items.indexOf(item);
        playingIndex = position;


        Log.e("DATTTTTTTTTTTT", items.size() + "INDEXXXXXXXXXXXXXX " + playingIndex + " K " + items.indexOf(item));
        startCurrentTrack();
    }

    private void startCurrentTrack() {
        Log.e("DATTTTTTTTTTTT", "PLAY trackssssssssssssssssssssssssssssss");

        if (mediaPlayer.isPlaying() || paused) {
            mediaPlayer.stop();
            paused = false;
        }
        mediaPlayer.reset();
        if (playingIndex < 0) {

            Log.e("DATTTTTTTTTTTT", "PLAY <0 ? trackssssssssssssssssssssssssssssss");
            return;
        }
//        mediaPlayer=MediaPlayer.create(this, items.get(playingIndex).getPath());
//        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(this, Uri.parse(items.get(playingIndex).getPath().getLossless()));

            mediaPlayer.prepareAsync();
//            mediaPlayer.prepare();
//            mediaPlayer.start();
            preparing = true;

//             Send broadcast when play track.
            Intent in = new Intent(ACTION_PLAY_TRACK);
            in.putExtra("playingIndex", playingIndex);
//            in.putExtra("duration",mediaPlayer.getDuration());
            sendBroadcast(in);

            //Make sure you update Seekbar on UI thread
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition();
//                        mSeekBar.setProgress(mCurrentPosition);
                        Intent in = new Intent(ACTION_SEEK_POSITION);
                        in.putExtra("currentPosition", mCurrentPosition);
                        sendBroadcast(in);
                    }
                    mHandler.postDelayed(this, 1000);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "startCurrentTrack: xxxxxxxxxx " + playingIndex);
        Log.e("DATTTTTTTTTTTT", "PLAY ABC trackssssssssssssssssssssssssssssss");
    }

    private void updateTracks() {
        Song playingItem = null;
//        if (playingIndex != -1) {
//            playingItem = tracks.get(playingIndex);
//        }
        items.clear();
        if (MusicService.tracks != null) {
            items.addAll(MusicService.tracks);
            Log.e("trACK", "NOT NULLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
            MusicService.tracks = null;
        }
        if (playingItem == null) {
            playingIndex = -1;
        } else {
            playingIndex = this.items.indexOf(playingItem);
        }
        if (playingIndex == -1 && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

    }

    @Override
    public void onDestroy() {
        audioWidget.controller().onControlsClickListener(null);
        audioWidget.controller().onWidgetStateChangedListener(null);
        audioWidget.hide();
        audioWidget = null;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        stopTrackingPosition();
        cropCircleTransformation = null;
        preferences = null;
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        preparing = false;
        mediaPlayer.start();
        if (!audioWidget.isShown()) {
            audioWidget.show(preferences.getInt(KEY_POSITION_X, 100), preferences.getInt(KEY_POSITION_Y, 100));
        }
        audioWidget.controller().start();
        audioWidget.controller().position(0);
        audioWidget.controller().duration(mediaPlayer.getDuration());
        stopTrackingPosition();
        startTrackingPosition();
        int size = getResources().getDimensionPixelSize(R.dimen.cover_size);
        Glide.with(this)
                .load(items.get(playingIndex).getCover())
                .asBitmap()
                .override(size, size)
                .centerCrop()
                .transform(cropCircleTransformation)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (audioWidget != null) {
                            audioWidget.controller().albumCoverBitmap(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (audioWidget != null) {
                            audioWidget.controller().albumCover(null);
                        }
                    }
                });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (playingIndex == -1) {
            audioWidget.controller().stop();
            return;
        }
        playingIndex++;
        if (playingIndex >= items.size()) {
            playingIndex = 0;
            if (items.size() == 0) {
                audioWidget.controller().stop();
                return;
            }
        }
        Log.d(TAG, "onCompletion: xxxxxxxxx" + playingIndex);
        startCurrentTrack();

//        // Send broadcast when completion.
//        Intent intent = new Intent(ACTION_COMPLETE);
//        intent.putExtra("playingIndex", playingIndex);
//        sendBroadcast(intent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        preparing = true;
        return false;
    }

    @Override
    public boolean onPlaylistClicked() {
        Intent intent = new Intent(this, MainCloud.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return false;
    }

    @Override
    public void onPlaylistLongClicked() {
        Log.d(TAG, "playlist long clicked");
    }

    @Override
    public void onPreviousClicked() {
        if (items.size() == 0)
            return;
        playingIndex--;
        if (playingIndex < 0) {
            playingIndex = items.size() - 1;
        }
        startCurrentTrack();
    }

    @Override
    public void onPreviousLongClicked() {
        Log.d(TAG, "previous long clicked");
    }

    @Override
    public boolean onPlayPauseClicked() {
        if (playingIndex == -1) {
            Toast.makeText(this, R.string.song_not_selected, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (mediaPlayer.isPlaying()) {
            stopTrackingPosition();
            mediaPlayer.pause();
            audioWidget.controller().start();
            paused = true;
        } else {
            startTrackingPosition();
            audioWidget.controller().pause();
            mediaPlayer.start();
            paused = false;
        }

        // Send broadcast when play pause clicked.
        Intent intent = new Intent(ACTION_PLAY_PAUSE);
        intent.putExtra("playing", !paused);
        sendBroadcast(intent);
        return false;
    }

    public void playPauseClick() {
        if (playingIndex == -1) {
            Toast.makeText(this, R.string.song_not_selected, Toast.LENGTH_SHORT).show();
        }
        if (mediaPlayer.isPlaying()) {
            stopTrackingPosition();
            mediaPlayer.pause();
            audioWidget.controller().pause();
            paused = true;
        } else {
            startTrackingPosition();
            audioWidget.controller().start();
            mediaPlayer.start();
            paused = false;
        }

        // Send broadcast when play pause clicked.
        Intent intent = new Intent(ACTION_PLAY_PAUSE);
        intent.putExtra("playing", !paused);
        sendBroadcast(intent);

    }

    @Override
    public void onPlayPauseLongClicked() {
        Log.d(TAG, "play/pause long clicked");
    }

    @Override
    public void onNextClicked() {
        if (items.size() == 0)
            return;
        playingIndex++;
        if (playingIndex >= items.size()) {
            playingIndex = 0;
        }
        startCurrentTrack();
    }

    @Override
    public void onNextLongClicked() {
        Log.d(TAG, "next long clicked");
    }

    @Override
    public void onAlbumClicked() {
        Log.d(TAG, "album clicked");
        Intent intent = new Intent(this, SongPlay.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (playingIndex != -1)
            startActivity(intent);
    }

    @Override
    public void onAlbumLongClicked() {
        Log.d(TAG, "album long clicked");
    }

    private void startTrackingPosition() {
        timer = new Timer(TAG);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                AudioWidget widget = audioWidget;
                MediaPlayer player = mediaPlayer;
                if (widget != null) {
                    widget.controller().position(player.getCurrentPosition());
                }
            }
        }, UPDATE_INTERVAL, UPDATE_INTERVAL);
    }

    private void stopTrackingPosition() {
        if (timer == null)
            return;
        timer.cancel();
        timer.purge();
        timer = null;
    }

    @Override
    public void onWidgetStateChanged(@NonNull AudioWidget.State state) {

    }

    @Override
    public void onWidgetPositionChanged(int cx, int cy) {
        preferences.edit()
                .putInt(KEY_POSITION_X, cx)
                .putInt(KEY_POSITION_Y, cy)
                .apply();
    }
}
