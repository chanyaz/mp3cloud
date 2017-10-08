package info.trongdat.mp3cloud.Views.Tabs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vistrav.pop.Pop;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Playlist;
import info.trongdat.mp3cloud.Presenters.Adapters.PlayListAdapter;
import info.trongdat.mp3cloud.Presenters.Adapters.SongAdapter3;
import info.trongdat.mp3cloud.Presenters.SQLiteHandler;
import info.trongdat.mp3cloud.Presenters.SongPresenter;
import info.trongdat.mp3cloud.R;

public class PlaylistView extends Fragment {
    RecyclerView recyclerView, lstSong;
    TextView txtPlayList;

    LinearLayout linearLayout;
    PlayListAdapter playListAdapter;
    SongPresenter songPresenter;
    Button btnCreatePlaylist;
    EditText edtAlbumName;
    SQLiteHandler db;
    SongAdapter3 adapter3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.lstPlayList);
        recyclerView.setHasFixedSize(true);
        btnCreatePlaylist = (Button) view.findViewById(R.id.btnCreatePlaylist);


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        txtPlayList = (TextView) view.findViewById(R.id.txtPlayList);

        songPresenter = new SongPresenter(getActivity());

        ArrayList<Playlist> playlists = songPresenter.getPlaylists();
        txtPlayList.setText("PLAY LISTS : " + playlists.size());
        playListAdapter = new PlayListAdapter(getActivity(), playlists);

        recyclerView.setAdapter(playListAdapter);

        btnCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pop.on(getActivity())
                        .with()
                        .cancelable(false)
                        .layout(R.layout.layout_add_playlist)
                        .when(new Pop.Yah() {
                            @Override
                            public void clicked(DialogInterface dialog, View view) {
                                String albummName = edtAlbumName.getText().toString();
                                db.execute("INSERT INTO Playlists VALUES(null,'" + albummName + "',' ')");
                                Playlist playlist = new Playlist();
                                playlist.setName(albummName);
                                playListAdapter.add(playlist, 0);

//                                Pop.on(getActivity())
//                                        .with()
//                                        .cancelable(false)
//                                        .layout(R.layout.layout_add_song)
//                                        .when(new Pop.Yah() {
//                                            @Override
//                                            public void clicked(DialogInterface dialog, View view) {
//                                            }
//                                        })
//                                        .when(new Pop.Nah() { // ignore if dont need negative button
//                                            @Override
//                                            public void clicked(DialogInterface dialog, View view) {
//                                            }
//                                        })
//                                        .show(new Pop.View() { // assign value to view element
//                                            @Override
//                                            public void prepare(View view) {
//                                                lstSong = (RecyclerView) view.findViewById(R.id.lstSong);
//                                                adapter3 = new SongAdapter3(getActivity(), songPresenter.getSongs());
//                                                lstSong.setAdapter(adapter3);
//                                                ItemClickSupport.addTo(lstSong)
//                                                        .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//                                                            @Override
//                                                            public void onItemClick(RecyclerView parent, View view, int position, long id) {
//
//                                                            }
//                                                        });
//                                            }
//                                        });
                            }
                        })
                        .when(new Pop.Nah() { // ignore if dont need negative button
                            @Override
                            public void clicked(DialogInterface dialog, View view) {
                            }
                        })
                        .show(new Pop.View() { // assign value to view element
                            @Override
                            public void prepare(View view) {
                                edtAlbumName = (EditText) view.findViewById(R.id.edtAlbumName);
                            }
                        });
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getActivity());
    }
}
