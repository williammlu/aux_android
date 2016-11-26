package org.mobiledevsberkeley.auxmusic;

import android.util.Log;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.List;

/**
 * Created by wml on 10/29/16.
 */

// TODO: refine how the playlist current song time works....

public class AuxSpotifyPlayer implements PlayerInterface{

    private Player mPlayer;
    private Playlist mPlaylist;
    AuxSingleton aux = AuxSingleton.getInstance();

    public AuxSpotifyPlayer(Player sp, Playlist p){
        mPlayer = sp;
        mPlaylist = p;
        if (sp != null && p != null) {
            sp.addNotificationCallback(new Player.NotificationCallback() {
                @Override
                public void onPlaybackEvent(PlayerEvent playerEvent) {
                    Log.d("AuxSpotifyPlayer", playerEvent.name());
                    if (playerEvent.equals(PlayerEvent.kSpPlaybackNotifyAudioDeliveryDone)) {
                        // reached the end of Spotify playing queue
                        Log.e("AuxSpotifyPlayer", "Finshed the end of a song, will be playing next..");
                        playNext();
                    }
                }

                @Override
                public void onPlaybackError(Error error) {
                    Log.e("AuxSpotifyPlayer", error.toString());
                }
            });
        }
    }


    public boolean togglePlay() {
        String returnMessage = "";
        if (getPlaybackState() == null) {
            Log.e("AuxSpotifyPlayer", "playback state is null");
            return false;
        }
        if (!getPlaybackState().isPlaying) {
            if (mPlaylist.getCurrentSongTime() != 0) {
                Log.d("AuxSpotifyPlayer", "Resuming at " + mPlaylist.getCurrentSongTime());
                mPlayer.resume(null);
                return true;
            } else {
                if (mPlaylist.getSpotifySongList().size() == 0) {
                    Log.d("AuxSpotifyPlayer", "No Songs in queue");
                    return false;
                } else {
                    Log.e("AuxSpotifyPlayer", "Playing new song");
                    mPlayer.playUri(null, mPlaylist.getCurrentSongURI(), 0, (int) mPlaylist.getCurrentSongTime());

                    // update song on PlaylistActivity
                    aux.updateCurrentSongView(aux.getCurrentSong());
                    return true;
                }
            }
        } else {
            long curTime = getCurrentSongTime();
            Log.d("AuxSpotifyPlayer", "Pausing song at " + getCurrentSongTime());
            mPlaylist.setCurrentSongTime(curTime);
            mPlayer.pause(null);
            Log.d("AuxSpotifyPlayer", "Paused at " + curTime);
            return false;
        }
    }

    /**
     * Sets SpotifyPlayer to play next song in mPlaylist
     * @return true if started new song, false if no more songs left
     */
    public boolean playNext() {
//        List<String> songs = mPlaylist.getSpotifySongIDList();
        List<Song> songs = mPlaylist.getSpotifySongList();
        int nextSongIndex = mPlaylist.getCurrentSongIndex() + 1;

        if (nextSongIndex >= songs.size()) {
            Log.e("AuxSpotifyPlayer", "No songs left in queue.");
            return false;
        }

        skipToTrack(nextSongIndex);
        return true;
    }

    public boolean skip() {
        int trackIndex = mPlaylist.getCurrentSongIndex();
        return skipToTrack(trackIndex + 1);

    }

    public boolean skipBack() {

        int trackIndex = mPlaylist.getCurrentSongIndex();

        // jump back a song if there is a back song, and is more than 1 second into the song
        if (trackIndex != 0 && mPlayer.getPlaybackState().positionMs < 3000) {
            Log.e("AuxSpotifyPlayer", "Going back 1 song: " + mPlayer.getPlaybackState().positionMs);
            return skipToTrack(trackIndex - 1);
        } else {
            // "skip" to current, track, which restarts to the beginning
            Log.e("AuxSpotifyPlayer", "return to start of song " + mPlayer.getPlaybackState().positionMs);
            return skipToTrack(trackIndex);

        }
    }

    public boolean skipToTrack(int targetTrack) {
        if (targetTrack >= mPlaylist.getSpotifySongList().size() || targetTrack < 0) {
            Log.e("AuxSpotifyPlayer", "Playlist does not have a " + targetTrack + "th song!");
            return false;
        }
        else {
//            List<String> songs = mPlaylist.getSpotifySongIDList();
            List<Song> songs = mPlaylist.getSpotifySongList();
            mPlaylist.setCurrentSongTime(0);
            mPlaylist.setActive(true);
            mPlaylist.setCurrentSongIndex(targetTrack);
            mPlaylist.setCurrentSongID(songs.get(targetTrack).getSongURI());
            // update song on PlaylistActivity
            AuxSingleton.getInstance().updateCurrentSongView(songs.get(mPlaylist.getCurrentSongIndex()));

            mPlayer.playUri(null, mPlaylist.getCurrentSongURI(), 0, (int) mPlaylist.getCurrentSongTime());
            return true;
        }
    }

    public void skipToTime(long ms) {
        mPlayer.seekToPosition(null, (int) ms);
    }

    public void shuffle() {
        // TODO implement in the future
    }

    public long getCurrentSongTime() {
        return getPlaybackState().positionMs;
    }

    public PlaybackState getPlaybackState() {
        return mPlayer.getPlaybackState();
    }

}
