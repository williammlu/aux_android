package org.mobiledevsberkeley.auxmusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by Young on 11/25/2016.
 */

public class CurrentSongView extends RelativeLayout {
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
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }
}
