package info.trongdat.mp3cloud.Presenters.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Playlist;
import info.trongdat.mp3cloud.Presenters.SQLiteHandler;
import info.trongdat.mp3cloud.R;

/**
 * Created by Alone on 10/4/2016.
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
    SQLiteHandler db;
    private Context mContext;
    private ArrayList<Playlist> playlists;

    public PlayListAdapter(Context context, ArrayList<Playlist> dataSet) {
        mContext = context;
        playlists = dataSet;
        db = new SQLiteHandler(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final int pos = position;
        holder.txtPlaylist.setText(playlists.get(position).getName());
        Cursor songc = db.rawQuery("SELECT * FROM PlaylistDetails WHERE playlistID=" + playlists.get(position).getId());
        holder.txtSong.setText(songc.getCount() + "");

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, playlists.get(pos).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public void remove(int position) {
        playlists.remove(position);
        notifyItemRemoved(position);
    }

    public void add(Playlist playlist, int position) {
        playlists.add(position, playlist);
        notifyItemInserted(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView txtPlaylist, txtSong;

        public ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            txtPlaylist = (TextView) itemView.findViewById(R.id.txtPlaylist);
            txtSong = (TextView) itemView.findViewById(R.id.txtSong);
        }
    }
}
