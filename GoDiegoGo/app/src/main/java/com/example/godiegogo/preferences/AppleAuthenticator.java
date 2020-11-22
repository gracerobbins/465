package com.example.godiegogo.preferences;

import android.content.Context;
import android.content.Intent;

import com.apple.android.sdk.authentication.AuthenticationFactory;
import com.apple.android.sdk.authentication.AuthenticationManager;
import com.example.godiegogo.R;

import java.util.HashMap;

public class AppleAuthenticator {
    private AuthenticationManager authenticationManager;
    private static final int REQUESTCODE_APPLEMUSIC_AUTH = 3456;

    public Intent logIn(Context context) {

        if (authenticationManager == null) {
            authenticationManager = AuthenticationFactory.createAuthenticationManager(context);
        }

        Intent intent = authenticationManager.createIntentBuilder(context.getString(R.string.jwt_token))
                .setHideStartScreen(false)
                .setStartScreenMessage("Please log into Apple Music to access your library.")
                .build();

        return intent;
    }
}
