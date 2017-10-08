package info.trongdat.mp3cloud.Presenters.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.SQLiteHandler;
import info.trongdat.mp3cloud.R;

/**
 * Created by Alone on 10/4/2016.
 */

public class OnlineSongAdapter4 extends RecyclerView.Adapter<OnlineSongAdapter4.ViewHolder> {
    SQLiteHandler db;
    private Context mContext;
    private ArrayList<Song> songs;

    private int lastPosition = 0;

    public OnlineSongAdapter4(Context context, ArrayList<Song> dataSet) {
        mContext = context;
        songs = dataSet;
        db = new SQLiteHandler(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_6, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtTitle.setText(songs.get(position).getSongName());
        holder.txtGenre.setText(songs.get(position).getArtist());
    }


    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void remove(int position) {
        songs.remove(position);
        notifyItemRemoved(position);
    }

    public void clearAnimation(View viewToAnimate) {
        viewToAnimate.clearAnimation();
    }

    public void add(Song song, int position) {
        songs.add(position, song);
        notifyItemInserted(position);
    }

    public ArrayList<Song> getList() {
        return songs;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView txtTitle;
        public TextView txtGenre;

        public ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;

            txtTitle = (TextView) itemView.findViewById(R.id.txtTile);
            txtGenre = (TextView) itemView.findViewById(R.id.txtGenre);
//            txtTitle.setSelected(true);
//            txtTitle.setEnabled(true);
//            txtTitle.setFocusable(false);
        }
    }
}
