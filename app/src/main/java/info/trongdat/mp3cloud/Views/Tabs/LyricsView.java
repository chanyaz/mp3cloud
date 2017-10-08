package info.trongdat.mp3cloud.Views.Tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.MusicService;
import info.trongdat.mp3cloud.R;

/**
 * Created by Alone on 11/15/2016.
 */

public class LyricsView extends Fragment {
    View view;
    private BroadcastReceiver brPlayTrack;
    TextView txtSong, txtData;
    ArrayList<Song> songs;
    private int position = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_detail_lyrics, container, false);
        txtSong = (TextView) view.findViewById(R.id.txtSong);
        txtData = (TextView) view.findViewById(R.id.txtData);
        songs = MusicService.getTracks();

        Song song = songs.get(PlayView.position);
        txtSong.setText(song.getSongName());

        brPlayTrack = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processReceive(context, intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_PLAY_TRACK);
        filter.addAction(MusicService.ACTION_PLAY_PAUSE);
        getActivity().registerReceiver(brPlayTrack, filter);

        return view;
    }

    public void refresh() {
        Song song = songs.get(position);
        txtSong.setText(song.getSongName());
        String lyrics = song.getLyrics();
        if (lyrics.equals(" ")) {
            txtData.setText("No data.!");
        } else {
            txtData.setText(lyrics);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void processReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        switch (intent.getAction()) {
            case MusicService.ACTION_PLAY_TRACK:
                int index = intent.getExtras().getInt("playingIndex");
                position = index;
                refresh();

                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(brPlayTrack);
    }
}
