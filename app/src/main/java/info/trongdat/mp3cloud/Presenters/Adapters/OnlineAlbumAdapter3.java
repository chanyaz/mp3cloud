package info.trongdat.mp3cloud.Presenters.Adapters;

import android.app.Activity;
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

public class OnlineAlbumAdapter3 extends RecyclerView.Adapter<OnlineAlbumAdapter3.ViewHolder> {
    SQLiteHandler db;
    private Context mContext;
    private ArrayList<OnlineAlbum> albums;

    public OnlineAlbumAdapter3(Context context, ArrayList<OnlineAlbum> dataSet) {
        mContext = context;
        albums = dataSet;
        db = new SQLiteHandler(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int width=((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
        Glide.with(mContext)
                .load(albums.get(position).getImage())
                .asBitmap().override(width/2,width/2)
                .placeholder(R.drawable.aw_ic_default_album)
                .error(R.drawable.album_thumbnail2)
                .into(holder.imgAlbum);

        holder.txtAlbum.setText(albums.get(position).getName());
        holder.txtArtist.setText(albums.get(position).getArtist());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlbumDetail.class);
                intent.putExtra("url", albums.get(position).getLink());
                intent.putExtra("id", albums.get(position).getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

//
//    holder.view.setOnClickListener(new View.OnClickListener()
//
//    {
//        @Override
//        public void onClick (View view){
////                Song song = songs.get(position);
//
//    }
//    }
//
//    );


    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public ImageView imgAlbum;
        public TextView txtAlbum, txtArtist;

        public ViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;
            imgAlbum = (ImageView) itemView.findViewById(R.id.imageAlbum);
            txtAlbum = (TextView) itemView.findViewById(R.id.txtAlbum);
            txtArtist = (TextView) itemView.findViewById(R.id.txtArtist);
        }
    }
}
