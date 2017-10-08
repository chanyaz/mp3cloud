package info.trongdat.mp3cloud.Presenters.Adapters;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Artist;
import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.MusicService;
import info.trongdat.mp3cloud.Presenters.SQLiteHandler;
import info.trongdat.mp3cloud.Presenters.SongPresenter;
import info.trongdat.mp3cloud.R;
import info.trongdat.mp3cloud.Views.SongPlay;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Alone on 10/4/2016.
 */

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
    SQLiteHandler db;
    private Context mContext;
    private ArrayList<Artist> artists;

    public ArtistAdapter(Context context, ArrayList<Artist> dataSet) {
        mContext = context;
        artists = dataSet;
        db = new SQLiteHandler(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final int pos = position;
        holder.txtArtist.setText(artists.get(position).getName());
        Cursor songc = db.rawQuery("SELECT * FROM ArtistSong WHERE artistID=" + artists.get(position).getId());
        holder.txtSong.setText(songc.getCount() + "");

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongPresenter songPresenter = new SongPresenter(mContext);
                ArrayList<Song> songs = songPresenter.getSongOfArtist(artists.get(position).getId());
                MusicService.setTracks(mContext, songs);
                MusicService.playTrack(mContext, 0);

                Intent intent = new Intent(mContext, SongPlay.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

//                Toast.makeText(mContext, "size of artist"+songs.size(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(mContext, artists.get(pos).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public void remove(int position) {
        artists.remove(position);
        notifyItemRemoved(position);
    }

    public void add(Artist artist, int position) {
        artists.add(position, artist);
        notifyItemInserted(position);
    }

    /**
     * Check if service is running.
     *
     * @param serviceClass
     * @return
     */
    private boolean isServiceRunning(@NonNull Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView txtArtist, txtSong;

        public ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            txtArtist = (TextView) itemView.findViewById(R.id.txtArtist);
            txtSong = (TextView) itemView.findViewById(R.id.txtSong);
        }
    }
}
