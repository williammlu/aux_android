package org.mobiledevsberkeley.auxmusic;

/**
 * Created by Young on 11/21/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlaylistAdapterSearch extends RecyclerView.Adapter<PlaylistAdapterSearch.CustomViewHolder>{
    ArrayList<Playlist> playlists;
    Context context;

    public PlaylistAdapterSearch(Context applicationContext, ArrayList<Playlist> playlists) {
        context = applicationContext;
        this.playlists = playlists;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_search_row_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.playlistName.setText(playlist.getPlaylistName());
        holder.hostName.setText(playlist.getHostSpotifyName());


    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView playlistName;
        TextView hostName;
        AuxSingleton aux = AuxSingleton.getInstance();

        public CustomViewHolder(View v) {
            super(v);
            playlistName = (TextView) v.findViewById(R.id.playlistName);
            hostName = (TextView) v.findViewById(R.id.hostName);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Playlist playlist = playlists.get(getLayoutPosition());
                    String key = playlist.getPlaylistKey();
                    aux.setCurrentPlaylist(playlist, key);
                    aux.addUserToPlaylist(aux.getCurrentUser());
                    ((ActualStartActivity) context).playlistIntent();
                }
            });
        }
    }
}
