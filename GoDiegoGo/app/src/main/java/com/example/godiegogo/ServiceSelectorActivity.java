package com.example.godiegogo;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.graphics.Color;
import android.content.Intent;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.os.Bundle;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import android.content.res.ColorStateList;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.AdapterView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


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


    public static final String CLIENT_ID = "17c1c4ef3eda4cd0afa1abe42019cac7";
    public static final String REDIRECT_URI = "GoDiegoGo-login://callback";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int MAIN_ACTIVITY = 1;
    private String mAccessToken;
    private String userId;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Call mCall;

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
                    final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
                    AuthorizationClient.openLoginActivity(ServiceSelectorActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
//                    Bundle b = new Bundle();
//                    b.putString("service", "Spotify");
//                    Intent intent = new Intent(v.getContext(), LoginPromptActivity.class);
//                    intent.putExtras(b);
//                    startActivity(intent);
//                    finish();
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


    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-private"})
                .setCampaign("com.example.godiegogo")
                .build();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        if (response.getError() != null && !response.getError().isEmpty()) {
//            setResponse(response.getError());
            Log.d("Error", "Response Error");
        }
        Log.d("SSMyAccessToken", response.getAccessToken());
        Log.d("SSrequestCode", String.valueOf(requestCode));
        Log.d("SSresultCode", String.valueOf(resultCode));
        Log.d("SSIntentData", data.toString());
        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            mAccessToken = response.getAccessToken();
            Log.d("MyActivity", mAccessToken);
//            updateTokenView();
        }
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/")
                .addHeader("Authorization","Bearer " + mAccessToken)
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Failure", "onFailureMethodCalled");
//                setResponse("Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    userId = jsonObject.getString("id");
                    Log.d("userId", userId);

                    Intent intent = new Intent();
                    intent.putExtra("userId", userId);
                    intent.putExtra("mAccessToken", mAccessToken);
                    setResult(RESULT_OK, intent);
                    finish();
//                    JSONArray items = jsonObject.getJSONArray("items");
//                    for (int i = 0; i < items.length(); i++) {
//                        JSONObject p = items.getJSONObject(i);
//                        playlist_names.add(p.getString("name"));
//                    }

//                    Log.d("PlayListNames", playlist_names.toString());

//                    runOnUiThread(new Runnable() {
//
//                        public void run() {
//                            itemsAdapter.notifyDataSetChanged();
////                lv.invalidate();
//                        }
//                    });
//                    setResponse(jsonObject.toString(3));

//                    setResponse(playlist_names);
//                    user_id = jsonObject.getString("id");
//                    Log.d("ForMe", user_id);
                } catch (JSONException e) {
                    Log.d("Error", "Exception");
//                    setResponse("Failed to parse data: " + e);
                }
            }
        });

        Log.d("Got here", "got here");
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    public void onRequestTokenClicked(View view) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private Uri getRedirectUri() {
        return new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
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
