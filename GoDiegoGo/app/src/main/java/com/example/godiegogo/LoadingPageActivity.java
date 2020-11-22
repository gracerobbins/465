package com.example.godiegogo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.content.Intent;
import java.util.LinkedList;
import java.util.ArrayList;
import com.example.godiegogo.R;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingPageActivity extends AppCompatActivity {
    public ArrayList<String> copied_songs;
    public ArrayList<String> failed_songs;
    public ArrayList<String> checked_playlists;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);

        Bundle b = this.getIntent().getExtras();
        checked_playlists=b.getStringArrayList("checked_playlists");

        copied_songs = new ArrayList<String>();
        copied_songs.add("Song Name 1");
        copied_songs.add("Song Name 2");
        copied_songs.add("Song Name 3");
        copied_songs.add("Song Name 1");
        copied_songs.add("Song Name 2");
        copied_songs.add("Song Name 3");
        copied_songs.add("Song Name 1");
        copied_songs.add("Song Name 2");
        copied_songs.add("Song Name 3");
        copied_songs.add("Song Name 1");
        copied_songs.add("Song Name 2");
        copied_songs.add("Song Name 3");

        failed_songs = new ArrayList<String>();
        failed_songs.add("Song Name 4");
        failed_songs.add("Song Name 5");
        failed_songs.add("Song Name 6");
        failed_songs.add("Song Name 7");
        failed_songs.add("Song Name 8");
        failed_songs.add("Song Name 9");
        failed_songs.add("Song Name 10");
        failed_songs.add("Song Name 11");
        failed_songs.add("Song Name 12");
        failed_songs.add("Song Name 13");

        ListView lv = (ListView) findViewById(R.id.songlist);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, copied_songs);
        lv.setAdapter(itemsAdapter);

        final Button button = findViewById(R.id.cancel_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button.setBackgroundColor(Color.BLUE);
                finish();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // yourMethod();
                Bundle b = new Bundle();
                b.putStringArrayList("failed_songs", failed_songs);
                Intent intent = new Intent(LoadingPageActivity.this, LoadingPageResultsActivity.class);
                intent.putExtras(b);

                //Intent intent = new Intent(this, LoadingPageResultsActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);   //5 seconds
    }
}
