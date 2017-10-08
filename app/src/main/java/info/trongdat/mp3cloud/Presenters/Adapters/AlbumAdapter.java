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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.Album;
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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    SQLiteHandler db;
    private Context mContext;
    private ArrayList<Album> albums;

    public AlbumAdapter(Context context, ArrayList<Album> dataSet) {
        mContext = context;
        albums = dataSet;
        db = new SQLiteHandler(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final int pos = position;


//        AlbumUtils albumUtils = new AlbumUtils(mContext);
//        Bitmap bitmap = albumUtils.getCover(albums.get(position).getId());
//        holder.imgAlbum.setImageBitmap(bitmap);

        Glide.with(mContext)
                .load(albums.get(position).getImage())
                .asBitmap()
                .placeholder(R.drawable.aw_ic_default_album)
                .error(R.drawable.album_thumbnail2)
                .into(holder.imgAlbum);

        holder.txtAlbum.setText(albums.get(position).getName());
        // get songs of album, get artist of album.
        Cursor songc = db.rawQuery("SELECT * FROM AlbumDetails WHERE albumID=" + albums.get(position).getId());
        holder.txtSong.setText(songc.getCount() + "");
        if (songc.moveToFirst()) {
            int songID = songc.getInt(songc.getColumnIndex("songID"));
            Cursor artistc = db.rawQuery("SELECT artistName FROM Artists WHERE artistID IN (SELECT artistID FROM ArtistSong WHERE songID=" + songID + ")");
            if (artistc.moveToFirst()) {
                holder.txtArtist.setText(artistc.getString(0));
            }
        }


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Song song = songs.get(position);
                SongPresenter songPresenter = new SongPresenter(mContext);
                ArrayList<Song> songs = songPresenter.getSongOfAlbum(albums.get(position).getId());
                MusicService.setTracks(mContext, songs);
                MusicService.playTrack(mContext, 0);

                Intent intent = new Intent(mContext, SongPlay.class);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
//                Toast.makeText(mContext, "size of album"+songs.size(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public void remove(int position) {
        albums.remove(position);
        notifyItemRemoved(position);
    }

    public void add(Album album, int position) {
        albums.add(position, album);
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

        public ImageView imgAlbum;
        public TextView txtAlbum, txtArtist, txtSong;

        public ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            imgAlbum = (ImageView) itemView.findViewById(R.id.imgAlbum);
            txtAlbum = (TextView) itemView.findViewById(R.id.txtAlbum);
            txtArtist = (TextView) itemView.findViewById(R.id.txtArtist);
            txtSong = (TextView) itemView.findViewById(R.id.txtSong);
        }
    }
}
