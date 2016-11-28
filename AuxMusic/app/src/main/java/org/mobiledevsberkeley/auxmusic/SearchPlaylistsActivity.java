package org.mobiledevsberkeley.auxmusic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
//import android.support.v7.widget.SearchView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class SearchPlaylistsActivity extends AppCompatActivity {
    SearchView searchView;
    TextView mTextView;
    RecyclerView mRecyclerView;
    ArrayList<Playlist> searchResults;
    AuxSingleton aux = AuxSingleton.getInstance();
    PlaylistAdapter playlistAdapter;
    com.google.firebase.database.Query queryRef;
    ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_playlists);
        setTitle("Find Playlists");
        searchView();
        setRecyclerViewByName();
        mTextView = (TextView) findViewById(R.id.noPlaylistsFoundText);
    }

    private void searchView() {
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchResults = new ArrayList<>();
        searchView.setIconified(false);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (queryRef != null) queryRef.removeEventListener(childEventListener);
                searchResults.clear();
                String regexed = s.replaceAll("[^A-Za-z]", "").toLowerCase();
                DatabaseReference playlistRef = aux.getDataBaseReference().child("playlists");
                queryRef = playlistRef.orderByChild("regexedPlaylistName").equalTo(regexed);
                childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Playlist playlist = dataSnapshot.getValue(Playlist.class);
                        System.out.println(playlist.getPlaylistKey());
                        if (playlist.getActive()) {
                            mTextView.setVisibility(View.INVISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            searchResults.add(playlist);
                            playlistAdapter.notifyDataSetChanged();
                            Log.d("debug", "found the damn playlist it's called " + playlist.getPlaylistName() + " and key " + playlist.getPlaylistKey());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                queryRef.addChildEventListener(childEventListener);

                // change the "no playlists found" thingy
                queryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            mTextView.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });

    }

    private void setRecyclerViewByName() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewByName);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                getResources().getConfiguration().orientation);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        playlistAdapter = new PlaylistAdapter(this, searchResults, PlaylistAdapter.SEARCH_VIEW);

        mRecyclerView.setAdapter(playlistAdapter);
    }

}
