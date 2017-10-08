package info.trongdat.mp3cloud.Views.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.Adapters.ItemClickSupport;
import info.trongdat.mp3cloud.Presenters.Adapters.SongAdapter;
import info.trongdat.mp3cloud.Presenters.MusicService;
import info.trongdat.mp3cloud.Presenters.SongPresenter;
import info.trongdat.mp3cloud.R;
import info.trongdat.mp3cloud.Views.SongPlay;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Alone on 10/4/2016.
 */

public class FavoriteView extends Fragment {
    RecyclerView recyclerView;
    TextView txtFavorite;

    SongAdapter songAdapter;
    SongPresenter songPresenter;
    Button btnShuffle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.lstFavorite);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        txtFavorite = (TextView) view.findViewById(R.id.txtFavorite);
        btnShuffle = (Button) view.findViewById(R.id.btnShuffle);
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        songPresenter = new SongPresenter(getActivity().getApplicationContext());

        ArrayList<Song> songs = songPresenter.getFavorites();
        txtFavorite.setText("FAVORITES: " + songs.size());
        songAdapter = new SongAdapter(getActivity().getApplication(), songs);

        recyclerView.setAdapter(songAdapter);
        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView parent, View view, int position, long id) {
                        MusicService.setTracks(getActivity(), songs);
                        MusicService.playTrack(getActivity(), position);
                        MusicService.setState(getActivity(), true);

                        Intent intent = new Intent(getActivity(), SongPlay.class);
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(intent);
                    }
                });
        return view;
    }


}