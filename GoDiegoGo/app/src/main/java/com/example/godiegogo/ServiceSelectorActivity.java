package com.example.godiegogo;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import com.apple.android.sdk.authentication.AuthenticationFactory;
import com.apple.android.sdk.authentication.AuthenticationManager;
import com.apple.android.sdk.authentication.TokenResult;
import com.example.godiegogo.preferences.AppleAuthenticator;
import com.example.godiegogo.preferences.ApplePreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.annotation.Native;
import java.util.ArrayList;

public class ServiceSelectorActivity extends AppCompatActivity {

    private AuthenticationManager appleAuthenticationManager;
    private static final int REQUESTCODE_APPLEMUSIC_AUTH = 3456;

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

        String appleToken = ApplePreferences.with(getApplicationContext()).getUserToken();
        if (appleToken == null || appleToken.isEmpty()) {
            Button button = findViewById(R.id.Apple_signin);
            button.setText("Sign In");
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                   if (appleAuthenticationManager == null) {
                       appleAuthenticationManager = AuthenticationFactory.createAuthenticationManager(getApplicationContext());
                   }
                   Intent intent = appleAuthenticationManager.createIntentBuilder(getString(R.string.jwt_token))
                           .setHideStartScreen(false)
                           .setStartScreenMessage("Please log in to access your library")
                           .build();
                   startActivityForResult(intent, REQUESTCODE_APPLEMUSIC_AUTH);

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
            Log.d("Apple Music", "Already Signed in");
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

    // This method is used to handle the results from any activity that was called from this one.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUESTCODE_APPLEMUSIC_AUTH) {
            TokenResult tokenResult = appleAuthenticationManager.handleTokenResult(data);

            if (!tokenResult.isError()) {
                String appleMusicUserToken = tokenResult.getMusicUserToken();
                ApplePreferences.with(getApplicationContext()).setAppleMusicUserToken(appleMusicUserToken);
                Log.d("Apple Music", "User Token: " + appleMusicUserToken);
            } else {
                Log.e("Apple Music", "Error getting token: " + tokenResult.getError());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
