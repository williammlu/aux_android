package org.mobiledevsberkeley.auxmusic;

/**
 * Created by Young on 11/19/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.CustomViewHolder>{
    ArrayList<Playlist> list;
    Context context;

    public PlaylistAdapter(Context applicationContext, ArrayList<Playlist> list) {
        context = applicationContext;
        this.list = list;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_near_me_row_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Playlist playlist = list.get(position);
        holder.playlistName.setText(playlist.getPlaylistName());
        holder.hostName.setText(playlist.getHostDeviceID());
        //might need to make host name a attribute of playlist
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView playlistName;
        TextView hostName;

        public CustomViewHolder(View v) {
            super(v);
//            img = (ImageView) v.findViewById(R.id.imageView);
            playlistName = (TextView) v.findViewById(R.id.playlistName);
            hostName = (TextView) v.findViewById(R.id.hostName);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Playlist p: list) {
                        if (p.getPlaylistName().equals(playlistName.toString())) {
                            AuxSingleton.getInstance().setCurrentPlaylist(p, "");
                            break;
                        }
                    }
                }
            });
        }
    }
}
