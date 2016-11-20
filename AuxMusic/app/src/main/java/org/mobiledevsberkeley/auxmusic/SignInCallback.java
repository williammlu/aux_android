package org.mobiledevsberkeley.auxmusic;

/**
 * Created by wilbu on 11/20/2016.
 */

public interface SignInCallback {
    public void userOnComplete();
    public void playlistOnComplete(boolean hasCurrentPlaylist);
}
