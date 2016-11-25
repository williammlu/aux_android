package org.mobiledevsberkeley.auxmusic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wml on 11/19/16.
 */

class DownloadSongsInfoTask extends AsyncTask<List<String>, Void, List<Song>> {
    MusicAdapter musicAdapter;
    Playlist playlist;

    public DownloadSongsInfoTask(MusicAdapter ma, Playlist p) {
        this.musicAdapter = ma;
        this.playlist = p;
    }


    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected List<Song> doInBackground(List<String>... lists) {
        List<String> songIds = lists[0];
        List<Song> songList = new ArrayList<>();
        try {
            songList = AuxSingleton.getInstance().getSongs(songIds);
            Log.d("DownloadSongsInfoTask", "Downloading song info for " + songIds.size() + " songs.");
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return songList;
    }

    @Override
    protected void onPostExecute(List<Song> result) {
        super.onPostExecute(result);
        if (result != null) {
            musicAdapter.list = result;
            playlist.setSpotifySongList(result);
            musicAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}