package org.mobiledevsberkeley.auxmusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Young on 11/25/2016.
 */

public class CurrentSongView extends RelativeLayout {
    private ImageView img;
    private TextView songTitle;
    private TextView artistTitle;
    private TextView albumTitle;
    AuxSingleton aux = AuxSingleton.getInstance();


    public CurrentSongView(Context context) {
        super(context);
        initializeViews(context);
    }



    public CurrentSongView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public CurrentSongView(Context context,
                      AttributeSet attrs,
                      int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.song_row_view, this);
        findViewById(R.id.closeImageView).setVisibility(GONE);
        img = (ImageView) findViewById(R.id.imageView);
        songTitle = (TextView) findViewById(R.id.songName);
        artistTitle = (TextView) findViewById(R.id.artistName);
        albumTitle = (TextView) findViewById(R.id.albumName);

        clearAll();

        Song currentSong = aux.getCurrentSong();
        if (currentSong != null) {
            Log.d("CurrentSongView", currentSong.toString());
            aux.setCurrentSongView(this);
            aux.updateCurrentSongView(currentSong);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }


    public void setSong(Song song) {
        new DownloadImageTask(img).execute(song.getImageUrl(300));
        songTitle.setText(song.getSongName());
        artistTitle.setText(song.getArtistName());
        albumTitle.setText(song.getAlbumName());
    }

    public void clearAll() {
        img.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_48px));

        songTitle.setText("Currently no song playing");
        artistTitle.setText("");
        albumTitle.setText("");
    }
}
