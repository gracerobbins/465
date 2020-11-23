package com.example.godiegogo.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.godiegogo.R;

public class SpotifyPreferences implements com.apple.android.sdk.authentication.TokenProvider {

    private final Context context;
    private SharedPreferences preferences;
    private static volatile SpotifyPreferences instance;
    private static final String KEY_SPOTIFY_MUSIC_USER_TOKEN = "spotify-user-token";
    private static final String KEY_SPOTIFY_USER_ID = "spotify-user-id";

    public static SpotifyPreferences with(Context context) {
        if (instance == null) {
            synchronized (SpotifyPreferences.class) {
                instance = new SpotifyPreferences(context);
            }
        }

        return instance;
    }

    public SpotifyPreferences(Context context) {
        this.context = context.getApplicationContext();
        preferences = context.getSharedPreferences(this.context.getString(R.string.preferences_file_name), Context.MODE_PRIVATE);
    }

    @Override
    public String getDeveloperToken() {
        return context.getString(R.string.spotify_client_id);
    }

    @Override
    public String getUserToken() {
        return preferences.getString(KEY_SPOTIFY_MUSIC_USER_TOKEN, null);
    }

    public String getUserID() {
        return preferences.getString(KEY_SPOTIFY_USER_ID, null);
    }

    public void setSpotifyUserToken(String userToken) {
        preferences.edit().putString(KEY_SPOTIFY_MUSIC_USER_TOKEN, userToken).apply();
    }

    public void setSpotifyUserId(String userId) {
        preferences.edit().putString(KEY_SPOTIFY_USER_ID, userId).apply();
    }
}
