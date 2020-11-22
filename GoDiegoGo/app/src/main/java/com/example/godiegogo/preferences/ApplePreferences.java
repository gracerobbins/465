package com.example.godiegogo.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.godiegogo.R;

public class ApplePreferences implements com.apple.android.sdk.authentication.TokenProvider {

    private final Context context;
    private SharedPreferences preferences;
    private static volatile ApplePreferences instance;

    private static final String KEY_APPLE_MUSIC_USER_TOKEN = "apple-music-user-token";

    public static ApplePreferences with(Context context) {
        if (instance == null) {
            synchronized (ApplePreferences.class) {
                instance = new ApplePreferences(context);
            }
        }

        return instance;
    }

    public ApplePreferences(Context context) {
        this.context = context.getApplicationContext();
        preferences = context.getSharedPreferences(this.context.getString(R.string.preferences_file_name), Context.MODE_PRIVATE);
    }

    @Override
    public String getDeveloperToken() {
        return context.getString(R.string.jwt_token);
    }

    @Override
    public String getUserToken() {
        return preferences.getString(KEY_APPLE_MUSIC_USER_TOKEN, null);
    }

    public void setAppleMusicUserToken(String userToken) {
        preferences.edit().putString(KEY_APPLE_MUSIC_USER_TOKEN, userToken).apply();
    }
}
