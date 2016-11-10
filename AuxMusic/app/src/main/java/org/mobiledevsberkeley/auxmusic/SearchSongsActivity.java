package org.mobiledevsberkeley.auxmusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import java.util.ArrayList;

public class SearchSongsActivity extends AppCompatActivity {
    ArrayList<Song> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_songs);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        long i = 0;
        list.add(new Song("bob","boblite", "bobo", "hardcoded", "wheee", i));

        MusicAdapter musicAdapter = new MusicAdapter(this, list);

        recyclerView.setAdapter(musicAdapter);

//        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) findViewById(R.id.searchView);
////        searchView.setQuery("Search for songs, artists, albums, or public playlists", false);
//        searchView.setQueryHint("Search for songs, artists, albums, or public playlists");
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // do something with spotify api
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // do something with spotify api
//                return false;
//            }
//        });

    }
}
