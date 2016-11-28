package org.mobiledevsberkeley.auxmusic;

/**
 * Created by Young on 11/21/2016.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

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

    public class CustomViewHolder extends RecyclerView.ViewHolder implements DialogOutputter {
        TextView playlistName;
        TextView hostName;
        AuxSingleton aux = AuxSingleton.getInstance();
        DialogOutputter dialogOutputter;

        public CustomViewHolder(View v){
            super(v);
            dialogOutputter = this;
            playlistName = (TextView) v.findViewById(R.id.playlistName);
            hostName = (TextView) v.findViewById(R.id.hostName);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Playlist playlist = playlists.get(getLayoutPosition());
                    //check that it's active and that it's not the active one
                    if (aux.hasActive) {
                        aux.checkIfJoinPlaylist(dialogOutputter, playlist);
                    }
                    else {
                        viewAndJoinActivity(playlist);
                    }
                }
            });
        }

        @Override
        public void outputDialog(final Playlist playlist) {
            new MaterialDialog.Builder(context)
                    .title(R.string.hasCurrentPlaylistDialog)
                    .content(R.string.joinPlaylistDialogMessage)
                    .positiveText(R.string.joinAnyways)
                    .negativeText(R.string.justView)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            viewAndJoinActivity(playlist);

                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            viewOnly(playlist);
                        }
                    })
                    .show();

        }

        private void viewOnly(Playlist playlist) {
            aux.isCurrentActive = false;
            ((SearchPlaylistsActivity) context).playlistIntent();

        }

        @Override
        public void viewAndJoinActivity(final Playlist playlist) {
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
            aux.isCurrentActive = true;
            ((SearchPlaylistsActivity) context).playlistIntent();
        }
    }
}
