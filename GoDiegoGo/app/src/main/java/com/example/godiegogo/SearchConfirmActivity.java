package com.example.godiegogo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchConfirmActivity extends AppCompatActivity {
    public ArrayList<String> playlist_songs;
    public String url;
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_confirm);

        playlist_songs = new ArrayList<String>();
        playlist_songs.add("Song Name 1");
        playlist_songs.add("Song Name 2");
        playlist_songs.add("Song Name 3");
        playlist_songs.add("Song Name 4");
        playlist_songs.add("Song Name 5");
        playlist_songs.add("Song Name 6");
        playlist_songs.add("Song Name 6");

        Bundle b = this.getIntent().getExtras();
        url = b.getString("url");
        String transfer_type = b.getString("transfer_type");

        ListView playlist_preview = (ListView) findViewById(R.id.playlist_preview);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playlist_songs);
        playlist_preview.setAdapter(itemsAdapter);

        final Button button = findViewById(R.id.search_cancel_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

}
