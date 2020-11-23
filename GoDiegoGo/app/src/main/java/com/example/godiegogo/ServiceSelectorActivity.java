package com.example.godiegogo;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ServiceSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_selector);

        //Spotify
        String spotifyToken = "";
//        spotifyToken = AppPreferences.with(getApplicationContext().getUserToken());
        if (spotifyToken == null || spotifyToken.isEmpty()) {
            Button button = findViewById(R.id.Spotify_signin);
            button.setText("Sign In");
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("service", "Spotify");
                    Intent intent = new Intent(v.getContext(), LoginPromptActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            // handle SELECT function to return service back to main menu
        }
        //Apple Music
        String appleToken = null;
//        spotifyToken = AppPreferences.with(getApplicationContext().getUserToken());
        if (appleToken == null || appleToken.isEmpty()) {
            Button button = findViewById(R.id.Apple_signin);
            button.setText("Sign In");
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("service", "Apple");
                    Intent intent = new Intent(v.getContext(), LoginPromptActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            // handle SELECT function to return service back to main menu
        }
        //Tidal
        String tidalToken = null;
//        spotifyToken = AppPreferences.with(getApplicationContext().getUserToken());
        if (tidalToken == null || tidalToken.isEmpty()) {
            Button button = findViewById(R.id.Tidal_signin);
            button.setText("Sign In");
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("service", "Tidal");
                    Intent intent = new Intent(v.getContext(), LoginPromptActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            // handle SELECT function to return service back to main menu
        }
        //Google Play
        String googleToken = null;
//        spotifyToken = AppPreferences.with(getApplicationContext().getUserToken());
        if (googleToken == null || googleToken.isEmpty()) {
            Button button = findViewById(R.id.Google_signin);
            button.setText("Sign In");
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("service", "Google");
                    Intent intent = new Intent(v.getContext(), LoginPromptActivity.class);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            // handle SELECT function to return service back to main menu
        }

        final Button button = findViewById(R.id.finish_service_selection);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }
}
