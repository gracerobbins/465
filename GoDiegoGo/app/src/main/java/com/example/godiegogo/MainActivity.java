package com.example.godiegogo;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.graphics.Color;
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public ArrayList<String> playlist_names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        playlist_names = new ArrayList<String>();
        playlist_names.add("Playlist Name 1");
        playlist_names.add("Playlist Name 2");
        playlist_names.add("Playlist Name 3");
        playlist_names.add("Playlist Name 4");


        ListView lv = (ListView) findViewById(R.id.playlist_selector);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playlist_names);
        lv.setAdapter(itemsAdapter);

        final Button transfer_button = findViewById(R.id.transfer_button);
        transfer_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoadingPageActivity.class);
                startActivity(intent);
            }
        });

        final Button sync_button = findViewById(R.id.sync_button);
        sync_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoadingPageActivity.class);
                startActivity(intent);
            }
        });

        final ImageButton swap_button = findViewById(R.id.swap_button);
        swap_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LinearLayout layout = findViewById(R.id.transfer_icon_list);
                ImageButton leftButton = (ImageButton)layout.getChildAt(0);
                layout.removeView(leftButton);
                ImageView arrow = (ImageView)layout.getChildAt(0);
                layout.removeView(arrow);
                ImageButton rightButton = (ImageButton)layout.getChildAt(0);
                layout.removeView(rightButton);

                layout.addView(rightButton);
                layout.addView(arrow);
                layout.addView(leftButton);
            }
        });
    }
}