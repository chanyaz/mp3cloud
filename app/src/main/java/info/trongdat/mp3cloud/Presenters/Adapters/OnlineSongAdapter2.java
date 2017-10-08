package info.trongdat.mp3cloud.Presenters.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.SQLiteHandler;
import info.trongdat.mp3cloud.R;

import static info.trongdat.mp3cloud.Views.Tabs.OfflineMusic.TAG;

/**
 * Created by Alone on 10/4/2016.
 */

public class OnlineSongAdapter2 extends RecyclerView.Adapter<OnlineSongAdapter2.ViewHolder> {
    SQLiteHandler db;
    private Context mContext;
    private ArrayList<Song> songs;

    private int lastPosition = 0;

    public OnlineSongAdapter2(Context context, ArrayList<Song> dataSet) {
        mContext = context;
        songs = dataSet;
        db = new SQLiteHandler(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_4, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (songs.get(position).getCover() != "")
            Picasso.with(mContext).load(songs.get(position).getCover())
                    .placeholder(R.drawable.album_thumbnail2)
                    .error(R.drawable.album_thumbnail2)
                    .into(holder.imgCurrent);

        holder.txtTitle.setText(songs.get(position).getSongName());
        holder.txtSinger.setText(songs.get(position).getArtist());
        Log.d(TAG, "onBindViewHolder: item" + position);
        holder.txtTop.setText((position + 1) + "");
        switch (position) {
            case 0:
                holder.txtTop.setBackgroundResource(R.color.colorTop1);
                break;
            case 1:
                holder.txtTop.setBackgroundResource(R.color.colorTop2);
                break;
            case 2:
                holder.txtTop.setBackgroundResource(R.color.colorTop3);
                break;
            default:
                holder.txtTop.setBackgroundResource(R.color.colorTopDefault);
                break;
        }
//        if (position==0)  holder.txtTop.setBackgroundResource(R.color.colorTop1);
//        if (position==1)  holder.txtTop.setBackgroundResource(R.color.colorTop2);
//        if (position==2)  holder.txtTop.setBackgroundResource(R.color.colorTop3);
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

        public ImageView imgCurrent;
        public TextView txtTitle;
        public TextView txtSinger;
        public TextView txtTop;

        public ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            imgCurrent = (ImageView) itemView.findViewById(R.id.imgCurrent);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTile);
            txtSinger = (TextView) itemView.findViewById(R.id.txtSinger);
            txtTop = (TextView) itemView.findViewById(R.id.txtTop);
            txtTitle.setSelected(true);
            txtTitle.setEnabled(true);
            txtTitle.setFocusable(false);
        }
    }
}
