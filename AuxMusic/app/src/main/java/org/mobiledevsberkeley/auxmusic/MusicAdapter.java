package org.mobiledevsberkeley.auxmusic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Young on 10/8/2016.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.CustomViewHolder> {
    List<Song> list;
    /**
     * List of songs that are checked
     */
    List<Song> checkedList;
    Context context;
    View view;
    SparseBooleanArray checkedIndices = new SparseBooleanArray();

    public static final int SEARCH_TO_ADD = 0;
    public static final int DISPLAY_PLAYLIST = 1;
    public static final int DISPLAY_PLAYLIST_GUEST = 2;

    public int displayType;


    public MusicAdapter(Context applicationContext, List<Song> list, int type, View view) {
        context = applicationContext;
        this.list = list;
        this.displayType = type;
        this.view = view;
        this.checkedList = new ArrayList<>();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        Song song = list.get(position);
        holder.songTitle.setText(song.getSongName());
        holder.artistTitle.setText(song.getArtistName());
//        holder.albumTitle.setText(song.getAlbumName());
        holder.song = list.get(position);

        holder.addSongCheckBox.setChecked(holder.isChecked());

        holder.addSongCheckBox.setOnCheckedChangeListener(null);

        holder.addSongCheckBox.setChecked(checkedIndices.get(position));

        holder.addSongCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedIndices.put(holder.getAdapterPosition(), isChecked);
            }
        });


        // async add album art to each row of recyclerview
//        int sideLengthPx = (int) context.getResources().getDimension(R.dimen.search_album_image_side);
//        new DownloadImageTask(holder.img).execute(song.getImageUrl(sideLengthPx));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        //        ImageView img;
        TextView songTitle;
        TextView artistTitle;
        //        TextView albumTitle;
        Song song;
        ImageView closeImageView;
        CheckBox addSongCheckBox;


        public CustomViewHolder(View v) {
            super(v);


//            img = (ImageView) v.findViewById(R.id.imageView);
            songTitle = (TextView) v.findViewById(R.id.songName);
            artistTitle = (TextView) v.findViewById(R.id.artistName);
//            albumTitle = (TextView) v.findViewById(R.id.albumName);
            closeImageView = (ImageView) v.findViewById(R.id.closeImageView);
            addSongCheckBox = (CheckBox) v.findViewById(R.id.addSongCheckBox);


            if (displayType == DISPLAY_PLAYLIST) {
                closeImageView.setVisibility(View.VISIBLE);
                closeImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // dialog to confirm removing
                        final int songIndex = getLayoutPosition();
                        final String songName = getSongAtIndex(songIndex).getSongName();
                        final Snackbar snackbarRemove = Snackbar.make(view, "Removing song: " + songName, Snackbar.LENGTH_SHORT);
                        String message = "Are you sure you want to remove: " +
                                songTitle.getText().toString();
                        new MaterialDialog.Builder(context)
                                .title(R.string.removeSongDialogTitle)
                                .content(message)
//                                    .icon(img.getDrawable())
                                .positiveText(R.string.SongDialogPositive)
                                .negativeText(R.string.SongDialogNegative)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        AuxSingleton aux = AuxSingleton.getInstance();
                                        int currentSongIndex = aux.getCurrentPlaylist().getCurrentSongIndex();
                                        if (songIndex < currentSongIndex) {
                                            aux.getCurrentPlaylist().setCurrentSongIndex(currentSongIndex - 1);
                                        } else if (songIndex == currentSongIndex) {
                                            aux.getCurrentPlaylist().setCurrentSongIndex(Math.max(currentSongIndex, list.size() - 1));
                                            if (aux.getCurrentPlaylist().getSpotifySongIDList().size() != 0) {
                                                aux.getAuxPlayer().skipToTrack(aux.getCurrentPlaylist().getCurrentSongIndex());
                                            } else {
                                                aux.getSpotifyPlayer().pause(null); // pause playing if no songs there
                                            }
                                            // HA. GET FUCKED
                                        }
                                        Log.d("MusicAdapter", "Adding a song " + songName);
                                        snackbarRemove.show();
                                        Song targetSong = getSongAtIndex(getLayoutPosition());
                                        //TODO: put this back in once Singleton's addSong method works
                                        aux.removeSong(targetSong);
                                        notifyDataSetChanged();

                                        // reset player state if no songs to play
                                        if (aux.getCurrentPlaylist().getSpotifySongIDList().size() == 0) {
                                            aux.getCurrentSongView().clearAll();
                                            aux.getSpotifyPlayer().pause(null);
                                            aux.getPlayerView().setPlayButton(true);
                                        }
                                    }
                                })
                                .show();
                    }
                });
            } else if (displayType == SEARCH_TO_ADD) {
                addSongCheckBox.setVisibility(View.VISIBLE);

                // do checkbox when view touched
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addSongCheckBox.setChecked(!isChecked());
                        checkedIndices.append(getLayoutPosition(), addSongCheckBox.isChecked());
                        Song targetSong = getSongAtIndex(getLayoutPosition());
                        if (addSongCheckBox.isChecked()) {
                            checkedList.add(targetSong);
                        } else {
                            checkedList.remove(targetSong);
                        }
                        Log.d("MusicAdapter", "Touch view Checkbox at position " + getLayoutPosition() + " is now " + addSongCheckBox.isChecked() + " " + checkedList.size());
                    }
                });

                // do check when checkbox touched
                addSongCheckBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkedIndices.append(getLayoutPosition(), addSongCheckBox.isChecked());
                        Song targetSong = getSongAtIndex(getLayoutPosition());
                        if (addSongCheckBox.isChecked()) {
                            checkedList.add(targetSong);
                        } else {
                            checkedList.remove(targetSong);
                        }
                        Log.d("MusicAdapter", "Checkbox at position " + getLayoutPosition() + " is now " + addSongCheckBox.isChecked() + " " + checkedList.size());
                    }
                });
            }
        }
        public boolean isChecked() {
            return checkedIndices.get(getLayoutPosition());
        }

    }

    public List<Song> getCheckedSongs() {
        return checkedList;
    }

    public void clearCheckedSongs() {
        if (checkedList != null) {
            checkedList.clear();
            checkedIndices.clear();
        }
    }

    public Song getSongAtIndex(int index) {
        return list.get(index);
    }
}