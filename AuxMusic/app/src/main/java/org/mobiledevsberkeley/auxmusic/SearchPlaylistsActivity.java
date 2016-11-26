package org.mobiledevsberkeley.auxmusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class SearchPlaylistsActivity extends AppCompatActivity {
    SearchView searchView;
    ArrayList<Playlist> searchResults;
    AuxSingleton aux = AuxSingleton.getInstance();
    PlaylistAdapterSearch playlistAdapterSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_playlists);
        searchResults = new ArrayList<>();
        searchView.setIconified(false);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchResults.clear();
                String regexed = query.replaceAll("[^A-Za-z]","").toLowerCase();
                DatabaseReference playlistRef = aux.getDataBaseReference().child("playlists");
                com.google.firebase.database.Query queryRef = playlistRef.orderByChild("regexedPlaylistName").equalTo(regexed);

                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Playlist playlist = dataSnapshot.getValue(Playlist.class);
                        searchResults.add(playlist);
                        playlistAdapterSearch.notifyDataSetChanged();
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
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    private void setReyclerViewByName() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewByName);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                getResources().getConfiguration().orientation);
        recyclerView.addItemDecoration(dividerItemDecoration);

        playlistAdapterSearch = new PlaylistAdapterSearch(this, searchResults);

        recyclerView.setAdapter(playlistAdapterSearch);
    }
}
