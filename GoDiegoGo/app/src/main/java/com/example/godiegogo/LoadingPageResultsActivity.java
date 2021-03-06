package com.example.godiegogo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingPageResultsActivity extends AppCompatActivity {
    public ArrayList<String> failed_songs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page_results);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle b = this.getIntent().getExtras();
        failed_songs=b.getStringArrayList("failed_songs");
        String transfer_type = b.getString("transfer_type");
        TextView results_header = (TextView)findViewById(R.id.results_header);
        ListView lv = (ListView) findViewById(R.id.songlist);

        if (failed_songs.size() > 0) {
            results_header.setText(transfer_type + " complete! The following songs could not be copied:");
        } else {
            results_header.setText(transfer_type + " complete! All songs were transferred!");
            lv.setVisibility(View.INVISIBLE);
        }

        int currentPlaylist = b.getInt("current_playlist");
        ArrayList<String> checkedPlaylists = b.getStringArrayList("checked_playlists");
        boolean morePlaylists = currentPlaylist < checkedPlaylists.size() - 1;


        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, failed_songs);
        lv.setAdapter(itemsAdapter);

        final Button button = findViewById(R.id.main_menu_button);
        if (morePlaylists) {
            if (transfer_type.equals("Syncing")) {
                button.setText("Sync next playlist");
            } else {
                button.setText("Transfer next playlist");
            }
        }
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (morePlaylists) {
                    b.putInt("current_playlist", currentPlaylist + 1);
                    b.putString("transfer_type", transfer_type);
                    Intent intent = new Intent(LoadingPageResultsActivity.this, LoadingPageActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);
                }
                finish();

            }
        });
    }
}
