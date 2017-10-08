package info.trongdat.mp3cloud.Views.Tabs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.Adapters.ItemClickSupport;
import info.trongdat.mp3cloud.Presenters.Adapters.SongAdapter2;
import info.trongdat.mp3cloud.Presenters.MusicService;
import info.trongdat.mp3cloud.R;

//import android.support.design.widget.FloatingActionButton;

//import android.support.design.widget.FloatingActionButton;

/**
 * Created by Alone on 11/15/2016.
 */

public class CurrentPlayList extends Fragment {
    private View view;
    private ArrayList<Song> songs;
    private int position = 0;
    private RecyclerView recyclerView;
    private SongAdapter2 songAdapter;
    private BroadcastReceiver brPlayTrack;
    TextView txtSong;
    FloatingActionButton btnPlayPause;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_detail_playlist, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.lstSong);

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        txtSong = (TextView) view.findViewById(R.id.txtSong);
        btnPlayPause=(FloatingActionButton)view.findViewById(R.id.btnPlayPause);
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicService.playPauseClick(getContext());
            }
        });
        songs = MusicService.getTracks();

        Song song = songs.get(PlayView.position);
        txtSong.setText(song.getSongName());

        brPlayTrack = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processReceive(context,intent);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.ACTION_PLAY_TRACK);
        filter.addAction(MusicService.ACTION_PLAY_PAUSE);
        getActivity().registerReceiver(brPlayTrack, filter);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void refresh() {
        Song song = songs.get(position);
        txtSong.setText(song.getSongName());
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
                    btnPlayPause.setImageResource(R.drawable.aw_ic_pause);
                } else {
                    btnPlayPause.setImageResource(R.drawable.aw_ic_play);
                }
//                Toast.makeText(context, "PLAYING: " + playing, Toast.LENGTH_LONG).show();
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
//        songs = MusicService.getTracks();

        songAdapter = new SongAdapter2(getActivity(), songs);
        recyclerView.setAdapter(songAdapter);

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View view, int position, long id) {
                        MusicService.playTrack(getActivity(), position);
//                        Toast.makeText(getActivity(),"SELECT "+position,Toast.LENGTH_LONG).show();
                    }
                });

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
