package org.mobiledevsberkeley.auxmusic;

/**
 * Created by Young on 11/21/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.CustomViewHolder>{
    ArrayList<Playlist> playlists;
    Context context;
    int displayType;
    public static final int SEARCH_VIEW = 0;
    public static final int PASTPLAYLISTS_VIEW = 1;

    public PlaylistAdapter(Context applicationContext, ArrayList<Playlist> playlists, int displayType) {
        context = applicationContext;
        this.displayType = displayType;
        this.playlists = playlists;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (displayType) {
            case SEARCH_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_search_row_view, parent, false);
                break;
            case PASTPLAYLISTS_VIEW:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_near_me_row_view, parent, false);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_search_row_view, parent, false);
                break;
        }
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.playlistName.setText(playlist.getPlaylistName());
        holder.hostName.setText(playlist.getHostDeviceID());

        new DownloadImageTask(holder.img).execute(playlist.getCoverArtURL());
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView playlistName;
        TextView hostName;
        AuxSingleton aux = AuxSingleton.getInstance();

        public CustomViewHolder(View v) {
            super(v);
            img = (ImageView) v.findViewById(R.id.imageView);
            playlistName = (TextView) v.findViewById(R.id.playlistName);
            hostName = (TextView) v.findViewById(R.id.hostName);
            if (displayType == SEARCH_VIEW) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Playlist playlist = playlists.get(getLayoutPosition());
                        String key = playlist.getPlaylistKey();
                        aux.setCurrentPlaylist(playlist, key);
                        aux.addUserToPlaylist(aux.getCurrentUser());
                        aux.addPastPlaylist(playlist);
                        context.startActivity(new Intent(context, PlaylistActivity.class));
//                        ((SearchPlaylistsActivity) context).playlistIntent();
                        // ADD PLAYLIST TO PAST PLAYLISTS
                    }
                });
            }
        }
    }
}
