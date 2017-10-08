package info.trongdat.mp3cloud.Views.Tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Artist;
import info.trongdat.mp3cloud.Presenters.Adapters.ArtistAdapter;
import info.trongdat.mp3cloud.Presenters.SongPresenter;
import info.trongdat.mp3cloud.R;

public class ArtistView extends Fragment {
    RecyclerView recyclerView;
    CardView cardArtist;
    TextView txtArtist;

    LinearLayout linearLayout;
    ArtistAdapter artistAdapter;
    SongPresenter songPresenter;
    Button btnSort;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.lstArtist);
        recyclerView.setHasFixedSize(true);
        linearLayout = (LinearLayout) view.findViewById(R.id.fragment_song);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        cardArtist = (CardView) view.findViewById(R.id.cardArtist);
        txtArtist = (TextView) view.findViewById(R.id.txtArtist);
        btnSort=(Button)view.findViewById(R.id.btnSort);
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        songPresenter = new SongPresenter(getActivity());

        ArrayList<Artist> artists = songPresenter.getArtists();
        txtArtist.setText("ARTISTS : " + artists.size());
        artistAdapter = new ArtistAdapter(getActivity(), artists);

        recyclerView.setAdapter(artistAdapter);
        return view;
    }

}
