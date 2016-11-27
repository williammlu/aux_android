package org.mobiledevsberkeley.auxmusic;

/**
 * Created by Young on 11/21/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

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
                    //check that it's active and that it's not the active one
                    if (aux.hasActive) {
                        new MaterialDialog.Builder(context)
                                .title(R.string.hasCurrentPlaylistDialog)
                                .content(R.string.joinPlaylistDialogMessage)
                                .positiveText(R.string.joinAnyways)
                                .negativeText(R.string.justView)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        //leave the old playlist
                                        //TODO: implement after merging with wilbur
                                        //join this playlist
                                        joinPlaylist();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        //just view this playlist
                                        //TODO: make sure using the right method with wilbur
                                    }
                                })
                                .show();

                    }
                    else {
                        joinPlaylist();
                    }

                }
            });
        }

        private void joinPlaylist() {
            final Playlist playlist = playlists.get(getLayoutPosition());
            final String password = playlist.getPassword();
            if (password != null && !password.equals("")) {
                new MaterialDialog.Builder(context)
                        .title(R.string.passwordTitle)
                        .content(R.string.passwordText)
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .input("", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (input.toString().equals(password)) {
                                    actuallyJoinPlaylist(playlist);
                                }
                            }
                        }).show();
            }
            else {
                actuallyJoinPlaylist(playlist);
            }

        }
        private void actuallyJoinPlaylist(Playlist playlist) {
            String key = playlist.getPlaylistKey();
            aux.setCurrentPlaylist(playlist, key);
            aux.addUserToPlaylist(aux.getCurrentUser());
            ((SearchPlaylistsActivity) context).playlistIntent();
        }


    }
}
