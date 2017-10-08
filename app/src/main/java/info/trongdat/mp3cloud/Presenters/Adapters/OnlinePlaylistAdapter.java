package info.trongdat.mp3cloud.Presenters.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import info.trongdat.mp3cloud.Models.Entities.OnlineAlbum;
import info.trongdat.mp3cloud.Presenters.SQLiteHandler;
import info.trongdat.mp3cloud.R;
import info.trongdat.mp3cloud.Views.AlbumDetail;

/**
 * Created by Alone on 10/4/2016.
 */

public class OnlinePlaylistAdapter extends RecyclerView.Adapter<OnlinePlaylistAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<OnlineAlbum> playlists;

    public OnlinePlaylistAdapter(Context context, ArrayList<OnlineAlbum> dataSet) {
        mContext = context;
        playlists = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item_3, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Glide.with(mContext)
                .load(playlists.get(position).getImage())
                .asBitmap()
                .placeholder(R.drawable.aw_ic_default_album)
                .error(R.drawable.album_thumbnail2)
                .into(holder.imgPlaylist);

        holder.txtPlaylist.setText(playlists.get(position).getName());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlbumDetail.class);
                intent.putExtra("url", playlists.get(position).getLink());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public ImageView imgPlaylist;
        public TextView txtPlaylist;

        public ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            imgPlaylist = (ImageView) itemView.findViewById(R.id.imagePlaylist);
            txtPlaylist = (TextView) itemView.findViewById(R.id.txtPlaylist);
        }
    }
}
