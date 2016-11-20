package org.mobiledevsberkeley.auxmusic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

/**
 * Created by Young on 10/8/2016.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.CustomViewHolder>{
    ArrayList<Song> list;
    Context context;

    public static final int SEARCH_TO_ADD = 0;
    public static final int DISPLAY_PLAYLIST = 1;

    public int displayType;


    public MusicAdapter(Context applicationContext, ArrayList<Song> list, int type ) {
        context = applicationContext;
        this.list = list;
        this.displayType = type;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Song song = list.get(position);
        holder.songTitle.setText(song.getSongName());
        holder.artistTitle.setText(song.getArtistName());
        holder.albumTitle.setText(song.getAlbumName());
        holder.song = list.get(position);

        // async add album art to each row of recyclerview
        int sideLengthPx = (int) context.getResources().getDimension(R.dimen.search_album_image_side);
        new DownloadImageTask(holder.img).execute(song.getImageUrl(sideLengthPx));
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
        Song song;


        public CustomViewHolder(View v) {
            super(v);

            img = (ImageView) v.findViewById(R.id.imageView);
            songTitle = (TextView) v.findViewById(R.id.songName);
            artistTitle = (TextView) v.findViewById(R.id.artistName);
            albumTitle = (TextView) v.findViewById(R.id.albumName);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (displayType) {
                        case SEARCH_TO_ADD:
                            // dialog to confirm playing
                            String message = "Are you sure you want to add: " +
                                    songTitle.getText().toString();
                            new MaterialDialog.Builder(context)
                                    .title(R.string.addSongDialogTitle)
                                    .content(message)
//                                    .icon(img.getDrawable())
                                    .positiveText(R.string.addSongDialogPositive)
                                    .negativeText(R.string.addSongDialogNegative)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            Log.d("MusicAdapter", "Adding a song " + getSongAtIndex(getLayoutPosition()).getSongName());
                                            Song targetSong = getSongAtIndex(getLayoutPosition());
                                            //TODO: put this back in once Singleton's addSong method works
                                            AuxSingleton.getInstance().addSong(targetSong);

                                        }
                                    })
                                    .show();
                            break;
                        case DISPLAY_PLAYLIST:
                            break;
                            // what to do when clicked in playlist activity?


                    }
//                    do we want to do anything here
                }
            });
        }
    }

    public Song getSongAtIndex(int index) {
        return list.get(index);
    }
}