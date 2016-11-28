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
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

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
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final Playlist playlist = playlists.get(position);
        holder.playlistName.setText(playlist.getPlaylistName());
//        holder.hostName.setText(playlist.getHostDeviceID());
        playlist.setImageUrl(new Playlist.PlaylistImageLoadCallback() {
            @Override
            public void urlOnComplete() {
                new DownloadImageTask(holder.img).execute(playlist.getCoverArtURL());
            }
        });

    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements DialogOutputter{
        ImageView img;
        TextView playlistName;
//        TextView hostName;
        DialogOutputter dialogOutputter;
        AuxSingleton aux = AuxSingleton.getInstance();

        public CustomViewHolder(View v) {
            super(v);
            dialogOutputter = this;
            img = (ImageView) v.findViewById(R.id.imageView);
            playlistName = (TextView) v.findViewById(R.id.playlistName);
//            hostName = (TextView) v.findViewById(R.id.hostName);
            if (displayType == SEARCH_VIEW) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Playlist playlist = playlists.get(getLayoutPosition());
//                        if (aux.hasActive) {
//                            aux.checkIfJoinPlaylist(dialogOutputter, playlist);
//                        } else {
//                            viewAndJoinActivity(playlist);
//                        }
                        actuallyJoinPlaylist(playlist);
                    }
                });
            }
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
            aux.setCurrentPlaylistViewOnly(playlist);
            context.startActivity(new Intent(context, PlaylistActivity.class));
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
            aux.addPastPlaylist(playlist);
            context.startActivity(new Intent(context, PlaylistActivity.class));
        }


//                        Playlist playlist = playlists.get(getLayoutPosition());
//                        String key = playlist.getPlaylistKey();
//                        aux.setCurrentPlaylist(playlist, key);
//                        aux.addUserToPlaylist(aux.getCurrentUser());
//                        aux.addPastPlaylist(playlist);
//                        context.startActivity(new Intent(context, PlaylistActivity.class));
//                        ((SearchPlaylistsActivity) context).playlistIntent();
                        // ADD PLAYLIST TO PAST PLAYLISTS

    }
}
