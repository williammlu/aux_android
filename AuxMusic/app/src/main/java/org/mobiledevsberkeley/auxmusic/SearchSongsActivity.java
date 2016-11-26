package org.mobiledevsberkeley.auxmusic;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public class SearchSongsActivity extends AppCompatActivity {
    ArrayList<Song> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_songs);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                getResources().getConfiguration().orientation);
        recyclerView.addItemDecoration(dividerItemDecoration);

        final MusicAdapter musicAdapter = new MusicAdapter(this, list, MusicAdapter.SEARCH_TO_ADD);

        recyclerView.setAdapter(musicAdapter);



        final SearchView searchView = (SearchView) findViewById(R.id.searchSongView);
        searchView.setIconified(false);
        searchView.setQueryHint(getResources().getString(R.string.songSearchHint));
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean searchByQuery(String query, final boolean closeOnComplete) {

                Log.d("SearchSongsActivity", "searching for song of name " + query);
                SpotifyService spotify = AuxSingleton.getSpotifyService();
                String searchQuery = query;
                spotify.searchTracks(searchQuery, new Callback<TracksPager>(){
                    @Override
                    public void success(TracksPager tPager, Response response) {
                        Log.d("SearchActivity", "Searched for songs, loaded " + tPager.tracks.items.size()  + " songs" + tPager.toString());

                        // display textview that says "No Songs Found" if no tracks match query
                        int noSongsTextVisiblity = (tPager.tracks.items.size() == 0) ? View.VISIBLE: View.INVISIBLE;
                        findViewById(R.id.noSongsFound).setVisibility(noSongsTextVisiblity);

                        List<Track> tracks = tPager.tracks.items;
                        list.clear();
                        for (Track t : tracks) {
                            list.add(new Song(t));
                        }

                        musicAdapter.notifyDataSetChanged();

                        // hide keyboard when complete
                        if (closeOnComplete) {
                            searchView.clearFocus();
                        }
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("Search Song failure", error.toString());
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return searchByQuery(query, true);
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // don't do text changed things because of Spotify rate limiting..
//                return searchByQuery(query, false);

                return false;
            }
        });

    }
}
