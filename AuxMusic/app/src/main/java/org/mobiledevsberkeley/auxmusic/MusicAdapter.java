package org.mobiledevsberkeley.auxmusic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Young on 10/8/2016.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.CustomViewHolder>{
    ArrayList<Song> list;
    Context context;

    public MusicAdapter(Context applicationContext, ArrayList<Song> list) {
        context = applicationContext;
        this.list = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Song song = list.get(position);
        holder.songTitle.setText(song.getSongName());
        holder.artistTitle.setText(song.getArtistName());
        holder.albumTitle.setText(song.getAlbumName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView songTitle;
        TextView artistTitle;
        TextView albumTitle;

        public CustomViewHolder(View v) {
            super(v);
//            img = (ImageView) v.findViewById(R.id.imageView);
            songTitle = (TextView) v.findViewById(R.id.songName);
            artistTitle = (TextView) v.findViewById(R.id.artistName);
            albumTitle = (TextView) v.findViewById(R.id.albumName);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    do we want to do anything here
                }
            });
        }
    }
}