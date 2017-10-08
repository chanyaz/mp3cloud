package info.trongdat.mp3cloud.Presenters.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import info.trongdat.mp3cloud.Models.Entities.Song;
import info.trongdat.mp3cloud.Presenters.SQLiteHandler;
import info.trongdat.mp3cloud.R;

/**
 * Created by Alone on 10/4/2016.
 */

public class SongAdapter2 extends RecyclerView.Adapter<SongAdapter2.ViewHolder> {
    SQLiteHandler db;
    private Context mContext;
    private ArrayList<Song> songs;
    private int lastPosition = 0;

    public SongAdapter2(Context context, ArrayList<Song> dataSet) {
        mContext = context;
        songs = dataSet;
        db = new SQLiteHandler(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (songs.get(position).getCover() != "")
            Picasso.with(mContext).load(songs.get(position).getCover())
                    .placeholder(R.drawable.album_thumbnail2)
                    .error(R.drawable.album_thumbnail2)
                    .into(holder.imgCurrent);

//        Bitmap bitmap = ((BitmapDrawable)holder.imgCurrent.getDrawable()).getBitmap();
//        holder.imgCurrent.setImageBitmap(AlbumUtils.getCroppedBitmap(bitmap));

        holder.txtTitle.setText(songs.get(position).getSongName());
        Cursor cursor = db.rawQuery("SELECT artistID, artistName FROM Artists " +
                " WHERE artistID IN (SELECT artistID FROM ArtistSong WHERE songID=" + songs.get(position).getSongID() + ")");

//        Artist artist = new SongPresenter(mContext).getArtist(mDataSet.get(position).getArtistID());
//        if (artist != null)
        if (cursor.moveToFirst() && cursor.getCount() != 0)
            holder.txtSinger.setText(cursor.getString(1));
        else holder.txtSinger.setText(songs.get(position).getArtist());
        cursor.close();
        if (songs.get(position).getDuration() != 0)
            holder.txtDuration.setText(songs.get(position).getDurationTime());
        else holder.txtDuration.setText("");

        setAnimation(holder.view, position);
//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Song song = songs.get(position);
//                if (!isServiceRunning(MusicService.class)) {
//                    MusicService.setTracks(mContext, songs);
//                } else
//                    MusicService.playTrack(mContext, position);

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

        public CircleImageView imgCurrent;
        public TextView txtTitle;
        public TextView txtSinger;
        public TextView txtDuration;

        public ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            imgCurrent = (CircleImageView) itemView.findViewById(R.id.imgCurrent);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTile);
            txtSinger = (TextView) itemView.findViewById(R.id.txtSinger);
            txtDuration = (TextView) itemView.findViewById(R.id.txtDuration);

            txtTitle.setSelected(true);
            txtTitle.setEnabled(true);
            txtTitle.setFocusable(false);
        }
    }
}
