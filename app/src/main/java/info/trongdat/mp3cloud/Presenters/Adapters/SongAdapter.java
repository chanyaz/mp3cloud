package info.trongdat.mp3cloud.Presenters.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

/**
 * Created by Alone on 10/4/2016.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    SQLiteHandler db;
    private Context mContext;
    private ArrayList<Song> songs;
    private int lastPosition = 0;

    public SongAdapter(Context context, ArrayList<Song> dataSet) {
        mContext = context;
        songs = dataSet;
        db = new SQLiteHandler(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        final int pos = position;
        Picasso.with(mContext).load(R.drawable.ic_music).into(holder.imgCurrent);
        holder.txtTitle.setText(songs.get(position).getSongName());

        Cursor cursor = db.rawQuery("SELECT artistID, artistName FROM Artists " +
                " WHERE artistID IN (SELECT artistID FROM ArtistSong WHERE songID=" + songs.get(position).getSongID() + ")");

//        Artist artist = new SongPresenter(mContext).getArtist(mDataSet.get(position).getArtistID());
//        if (artist != null)
        if (cursor.moveToFirst())
            holder.txtSinger.setText(cursor.getString(1));
        cursor.close();
        holder.txtDuration.setText(songs.get(position).getDurationTime());
        setAnimation(holder.view, position);
//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Song song = songs.get(position);
//                if (!isServiceRunning(MusicService.class)) {
//                    MusicService.setTracks(mContext, songs);
//                } else
//                    MusicService.playTrack(mContext, position);
//
//                Intent intent = new Intent(mContext, SongPlay.class);
//                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);
//                Toast.makeText(mContext, songs.get(position).getSongName(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

//    /**
//     * Check if service is running.
//     *
//     * @param serviceClass
//     * @return
//     */
//    private boolean isServiceRunning(@NonNull Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//
//        return false;
//    }

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

    public void add(Song song, int position) {
        songs.add(position, song);
        notifyItemInserted(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public ImageView imgCurrent;
        public TextView txtTitle;
        public TextView txtSinger;
        public TextView txtDuration;
        public CardView itemSong;

        public ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            imgCurrent = (ImageView) itemView.findViewById(R.id.imgCurrent);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTile);
            txtSinger = (TextView) itemView.findViewById(R.id.txtSinger);
            txtDuration = (TextView) itemView.findViewById(R.id.txtDuration);
            itemSong = (CardView) itemView.findViewById(R.id.itemSong);

            txtTitle.setSelected(true);
            txtTitle.setEnabled(true);
            txtTitle.setFocusable(false);
        }
    }
}
