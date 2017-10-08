package info.trongdat.mp3cloud.Views.Tabs;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import static info.trongdat.mp3cloud.Views.Tabs.OfflineMusic.TAG;

public class SongView extends Fragment {
    RecyclerView recyclerView;
    CardView cardSong;
    TextView txtSong;

    LinearLayout linearLayout;
    SongAdapter songAdapter;
    SongPresenter songPresenter;
    Button btnShuffle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.lstSong);
        recyclerView.setHasFixedSize(true);
        linearLayout = (LinearLayout) view.findViewById(R.id.fragment_song);
        Log.d(TAG, "onCreateView: song");
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        cardSong = (CardView) view.findViewById(R.id.cardSong);
        txtSong = (TextView) view.findViewById(R.id.txtSong);
        btnShuffle = (Button) view.findViewById(R.id.btnShuffle);
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        songPresenter = new SongPresenter(getActivity());
        ArrayList<Song> songs = songPresenter.getSongs();
        txtSong.setText("ALL SONG: " + songs.size());
        songAdapter = new SongAdapter(getActivity(), songs);

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

//        MusicService.setTracks(getActivity().getApplicationContext(), songs);
//        Toast.makeText(getActivity(),"create.. view",Toast.LENGTH_LONG).show();
        return view;
    }



    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(getActivity(),"create..",Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate: song");
    }
//
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        Toast.makeText(getActivity(),"attach.. 1",Toast.LENGTH_LONG).show();
//    }
//
    @Override
    public void onStart() {
        super.onStart();
//        Toast.makeText(getActivity(),"start..",Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart: song");
    }
//
    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(getActivity(),"destroy..",Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy: song");
    }
//

//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        Toast.makeText(getActivity(),"attach.. 2",Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        Toast.makeText(getActivity(),"detach..",Toast.LENGTH_LONG).show();
//    }
//
    @Override
    public void onStop() {
        super.onStop();
//        Toast.makeText(getActivity(),"stop..",Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStop: song");
    }
//
//
    @Override
    public void onResume() {
        super.onResume();
//        Toast.makeText(getActivity(),"Resume.. ok1",Toast.LENGTH_LONG).show();
        Log.d(TAG, "onResume: song");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: song");
//        Toast.makeText(getActivity(),"pause..  ok1",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: song");
//        Toast.makeText(getActivity(),"destroy.. view",Toast.LENGTH_LONG).show();
    }
    /**
     * Check if service is running.
     *
     * @param serviceClass
     * @return
     */

    private boolean isServiceRunning(@NonNull Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

}
