package org.mobiledevsberkeley.auxmusic;

/**
 * Created by wml on 10/26/16.
 */

/**
 * Interface that Spotify or Soundcloud player class will implement.
 * Purely for manipulating how the song is played.
 */
public interface PlayerInterface {

    String togglePlay();
    int skip();
    int skipBack();
    boolean skipToTrack(int targetTrack);
    void skipToTime(long ms);


    //tentative
    void shuffle();


    // getters
//    Song getCurrentSong();
    long getCurrentSongTime();

}
