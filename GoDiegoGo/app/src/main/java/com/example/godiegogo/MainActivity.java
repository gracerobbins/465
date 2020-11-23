package com.example.godiegogo;

import androidx.appcompat.app.AppCompatActivity;
<<<<<<< Updated upstream
import android.widget.Button;
import android.view.View;
import android.graphics.Color;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

=======

import android.content.res.ColorStateList;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.graphics.Color;
import android.content.Intent;
import android.widget.CheckedTextView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

//import com.spotify.sdk.android.auth.AuthenticationClient;
//import com.spotify.sdk.android.auth.AuthenticationRequest;
//import com.spotify.sdk.android.auth.AuthenticationResponse;
//import com.spotify.sdk.android.player.Config;
//import com.spotify.sdk.android.player.ConnectionStateCallback;
//import com.spotify.sdk.android.player.Error;
//import com.spotify.sdk.android.player.Player;
//import com.spotify.sdk.android.player.PlayerEvent;
//import com.spotify.sdk.android.player.Spotify;
//import com.spotify.sdk.android.player.SpotifyPlayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public ArrayList<String> playlist_names;
//<<<<<<< HEAD
    public static final String CLIENT_ID = "17c1c4ef3eda4cd0afa1abe42019cac7";
    public static final String REDIRECT_URI = "GoDiegoGo-login://callback";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessCode;
    private Call mCall;
    private ArrayAdapter<String> itemsAdapter;
//=======
    public ArrayList<String> checked_playlists;

//>>>>>>> 1b601ada58f12d7c61f9d08786db284a9628513c
>>>>>>> Stashed changes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< Updated upstream

=======
//<<<<<<< HEAD
        playlist_names = new ArrayList<String>();
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
        itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playlist_names);
        GridView grid_view = (GridView) findViewById(R.id.playlist_selector);
        grid_view.setAdapter(itemsAdapter);
//        playlist_names.add("Playlist Name 1");
//        playlist_names.add("Playlist Name 2");
//        playlist_names.add("Playlist Name 3");
//        playlist_names.add("Playlist Name 4");

//        ArrayAdapter<String> itemsAdapter =
//                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playlist_names);

//=======
//        checked_playlists = new ArrayList<String>();
//        GridView grid_view = (GridView) findViewById(R.id.playlist_selector);
//        ArrayAdapter<String> itemsAdapter =
//                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, playlist_names);
//        grid_view.setAdapter(itemsAdapter);
        grid_view.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                CheckedTextView checkedTextView = ((CheckedTextView)view);
                int[][] states = new int[][] {new int[] { android.R.attr.state_enabled} };//enabled state
                int[] colors = new int[] { Color.BLACK };
                ColorStateList color_states = new ColorStateList(states, colors);
                checkedTextView.setCheckMarkTintList(color_states);
                checkedTextView.setChecked(!checkedTextView.isChecked());
                if (checkedTextView.isChecked() && !checked_playlists.contains(checkedTextView.getText().toString())) {
                    checked_playlists.add(checkedTextView.getText().toString());
                } else {
                    checked_playlists.remove(checkedTextView.getText().toString());
                }
            }
        });

        final Button transfer_button = findViewById(R.id.transfer_button);
        transfer_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putStringArrayList("checked_playlists", checked_playlists);
                b.putString("transfer_type", "Transferring");
                Intent intent = new Intent(v.getContext(), LoadingPageActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }

        });

        final Button sync_button = findViewById(R.id.sync_button);
        sync_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putStringArrayList("checked_playlists", checked_playlists);
                b.putString("transfer_type", "Syncing");
                Intent intent = new Intent(v.getContext(), LoadingPageActivity.class);
                intent.putExtras(b);
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
>>>>>>> Stashed changes

        final Button button = findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
<<<<<<< Updated upstream
                button.setBackgroundColor(Color.BLUE);
            }
        });
    }
=======
                Bundle b = new Bundle();
                Intent intent = new Intent(v.getContext(), ServiceSelectorActivity.class);
                startActivity(intent);
            }
        });


        final ImageButton apple_button = findViewById(R.id.apple_icon);
        apple_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                Intent intent = new Intent(v.getContext(), ServiceSelectorActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onRequestCodeClicked(View view) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(this, AUTH_CODE_REQUEST_CODE, request);
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"playlist-read-private"})
                .setCampaign("com.example.godiegogo")
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        Log.d("MyResponse", response.toString());
        if (response.getError() != null && !response.getError().isEmpty()) {
//            setResponse(response.getError());
            Log.d("Error", "Response Error");
        }
        Log.d("MyAccessToken", response.getAccessToken());
        Log.d("requestCode", String.valueOf(requestCode));
        Log.d("resultCode", String.valueOf(resultCode));
        Log.d("IntentData", data.toString());
        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            mAccessToken = response.getAccessToken();
            Log.d("MyActivity", mAccessToken);
//            updateTokenView();
        }
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/users/" + "advai" + "/playlists")
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

                    JSONArray items = jsonObject.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject p = items.getJSONObject(i);
                        playlist_names.add(p.getString("name"));
                    }

                    Log.d("PlayListNames", playlist_names.toString());
                    runOnUiThread(new Runnable() {

                        public void run() {
                            itemsAdapter.notifyDataSetChanged();
//                lv.invalidate();
                        }
                    });
//                    setResponse(jsonObject.toString(3));

//                    setResponse(playlist_names);
//                    user_id = jsonObject.getString("id");
//                    Log.d("ForMe", user_id);
                } catch (JSONException e) {
//                    setResponse("Failed to parse data: " + e);
                }
            }
        });

        Log.d("Got here", "got here");
//        runOnUiThread(() -> {
//            ListView lv = (ListView) findViewById(R.id.playlist_selector);
//            itemsAdapter =
//                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playlist_names);
//            lv.setAdapter(itemsAdapter);
//            lv.invalidate();
//        });


    }
    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
    private Uri getRedirectUri() {
        return new Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build();
//=======

//>>>>>>> 1b601ada58f12d7c61f9d08786db284a9628513c
    }
>>>>>>> Stashed changes
}