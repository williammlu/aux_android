package org.mobiledevsberkeley.auxmusic;

import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;
import java.util.Random;

/**
 * Created by wml on 10/29/16.
 */

public class AuxSpotifyPlayer implements PlayerInterface{

    private SpotifyPlayer mPlayer;
    private Playlist mPlaylist;
//    private static final String CLIENT_ID = "687e297cd52c436eb680444a7b0519f9";
    private PlaybackState mPlaybackState;

    public AuxSpotifyPlayer(SpotifyPlayer sp, Playlist p){
        mPlayer = sp;
        mPlaylist = p;
        sp.addNotificationCallback(new Player.NotificationCallback() {
            @Override
            public void onPlaybackEvent(PlayerEvent playerEvent) {
                Log.d("AuxSpotifyPlayer", playerEvent.name());
                if (playerEvent.equals(PlayerEvent.kSpPlaybackNotifyAudioDeliveryDone)) {
                    // reached the end of Spotify playing queue
                    playNext();
                }
            }

            @Override
            public void onPlaybackError(Error error) {
                Log.e("AuxSpotifyPlayer", error.toString());
            }
        });

    }


    public void togglePlay() {

        if (!mPlaybackState.isPlaying) {
            if (mPlaybackState.isActiveDevice) {
                Log.d("AuxSpotifyPlayer", "Resuming");
                mPlayer.resume(null);
            } else {
                Log.d("AuxSpotifyPlayer", "Playing new song");
                mPlayer.playUri(null, mPlaylist.getCurrentSongID(), 0, (int) mPlaylist.getCurrentSongTime());
            }

        } else {
            Log.d("AuxSpotifyPlayer", "Pausing song");

            mPlayer.pause(null);
        }
        mPlaylist.setPlaying(!mPlaylist.getPlaying());
    }

    /**
     * Sets SpotifyPlayer to play next song in mPlaylist
     * @return true if started new song, false if no more songs left
     */
    public boolean playNext() {
        List<String> songs = mPlaylist.getSpotifySongIDList();
        int nextSongIndex = songs.indexOf(mPlaylist.getCurrentSongID());

        if (nextSongIndex >= songs.size()) {
            Log.e("AuxSpotifyPlayer", "No songs left in queue.");
            return false;
        }

        skipToTrack(nextSongIndex);
        return true;
    }

    public int skip() {
        return 0;
    }// TODO

    public int skipBack() {
        return 0;
    } // TODO

    public void skipToTrack(int targetTrack) {
        if (targetTrack > mPlaylist.getSpotifySongIDList().size() || targetTrack < 0) {
            throw new IndexOutOfBoundsException("Playlist does not have a " + targetTrack + "th song!");
        }
        else {
            List<String> songs = mPlaylist.getSpotifySongIDList();
            int nextSongIndex = songs.indexOf(mPlaylist.getCurrentSongID());
            mPlaylist.setCurrentSongTime(0);
            mPlaylist.setPlaying(true);
            mPlaylist.setCurrentSongID(songs.get(nextSongIndex));

            mPlayer.playUri(null, mPlaylist.getCurrentSongID(), 0, (int) mPlaylist.getCurrentSongTime());
        }

    }

    public void skipToTime(long ms) {
        mPlayer.seekToPosition(null, (int) ms);
    }

    public void shuffle() {
        // TODO implement in the future
    }

    public long getCurrentSongTime() {
        if (mPlaybackState != null) {
            return mPlaybackState.positionMs;
        }
        return -1; // error
    }
}
