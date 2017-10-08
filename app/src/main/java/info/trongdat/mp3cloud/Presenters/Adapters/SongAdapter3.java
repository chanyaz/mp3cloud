package info.trongdat.mp3cloud.Presenters.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.SQLiteHandler;
import info.trongdat.mp3cloud.R;

/**
 * Created by Alone on 10/4/2016.
 */

public class SongAdapter3 extends RecyclerView.Adapter<SongAdapter3.ViewHolder> {
    SQLiteHandler db;
    private Context mContext;
    private ArrayList<Song> songs;
    private int lastPosition = 0;

    public SongAdapter3(Context context, ArrayList<Song> dataSet) {
        mContext = context;
        songs = dataSet;
        db = new SQLiteHandler(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_5, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.txtTitle.setText(songs.get(position).getSongName());
        holder.cbxSong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
        setAnimation(holder.view, position);
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in_item);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public ArrayList<Song> getList() {
        return songs;
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void remove(int position) {
        songs.remove(position);
        notifyItemRemoved(position);
    }

    public void add(Song song, int position) {
        songs.add(position, song);
        notifyItemInserted(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public TextView txtTitle;
        public CheckBox cbxSong;

        public ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            cbxSong = (CheckBox) itemView.findViewById(R.id.cbxSong);
            txtTitle = (TextView) itemView.findViewById(R.id.txtSong);

            txtTitle.setSelected(true);
            txtTitle.setEnabled(true);
            txtTitle.setFocusable(false);
        }
    }
}
