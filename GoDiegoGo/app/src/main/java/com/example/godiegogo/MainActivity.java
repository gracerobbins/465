package com.example.godiegogo;

import androidx.appcompat.app.AppCompatActivity;


import android.net.Uri;

import android.content.res.ColorStateList;

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

import com.example.godiegogo.preferences.SpotifyPreferences;
import com.example.godiegogo.utils.SpotifyMusicUtils;
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

import com.example.godiegogo.utils.AppleMusicUtils;
import com.example.godiegogo.utils.VolleyResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    public ArrayList<String> playlist_names;
    public ArrayList<String> playlist_ids;
    public static final String CLIENT_ID = "17c1c4ef3eda4cd0afa1abe42019cac7";
    public static final String REDIRECT_URI = "GoDiegoGo-login://callback";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;
    public static final int MAIN_ACTIVITY = 1;
    public static final int SERVICE_SELECTOR_ACTIVITY = 2;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessCode;
    private String userId;
    private Call mCall;
    private ArrayAdapter<String> itemsAdapter;
    public ArrayList<String> checked_playlists;

    public ArrayList<String> checked_playlist_ids;


    public ArrayList<String> appleMusicPlaylistIds;
    private GridView grid_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checked_playlists = new ArrayList<String>();
        checked_playlist_ids = new ArrayList<String>();
        playlist_names = new ArrayList<String>();

        playlist_ids = new ArrayList<String>();
        itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, playlist_names);

        GridView grid_view = (GridView) findViewById(R.id.playlist_selector);

        appleMusicPlaylistIds = new ArrayList<>();

        grid_view = (GridView) findViewById(R.id.playlist_selector);
//        itemsAdapter =
//                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, playlist_names);

        grid_view.setAdapter(itemsAdapter);
        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                    int i = playlist_names.indexOf(checkedTextView.getText().toString());
                    checked_playlist_ids.add(playlist_ids.get(i));

                } else {
                    int i = playlist_names.indexOf(checkedTextView.getText().toString());
                    checked_playlist_ids.remove(playlist_ids.get(i));
                    checked_playlists.remove(checkedTextView.getText().toString());

                }
            }
        });

        if (mAccessToken != null) {

        }
        final Button transfer_button = findViewById(R.id.transfer_button);
        transfer_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putStringArrayList("checked_playlists", checked_playlists);
                b.putStringArrayList("checked_playlist_ids", checked_playlist_ids);
                Log.d("CheckedPlaylists", checked_playlists.toString());
                Log.d("CheckedPlaylistIds", checked_playlist_ids.toString());
                b.putString("transfer_type", "Transferring");
                b.putString("mAccessToken", mAccessToken);
                b.putString("userId", userId);
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

                int leftButtonID = leftButton.getId();
                if (leftButtonID == 2131231049) {
                    Log.d("Apple Music", "Left button is apple music");
                    setAppleMusicToSelector();
                }
            }
        });

        final ImageButton spotify_button = findViewById(R.id.spotify_icon);
        spotify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                Intent intent = new Intent(v.getContext(), ServiceSelectorActivity.class);
                startActivityForResult(intent, SERVICE_SELECTOR_ACTIVITY);
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
        if (resultCode == RESULT_OK && requestCode == SERVICE_SELECTOR_ACTIVITY) {
            if (data.hasExtra("userId")) {
                userId = data.getStringExtra("userId");
                SpotifyPreferences.with(getApplicationContext()).setSpotifyUserId(userId);
            }
            if (data.hasExtra("mAccessToken")) {
                mAccessToken = data.getStringExtra("mAccessToken");
                SpotifyPreferences.with(getApplicationContext()).setSpotifyUserToken(mAccessToken);
            }
            if (mAccessToken != null && userId != null) {
                final Request request = new Request.Builder()
                        .url("https://api.spotify.com/v1/users/" + userId + "/playlists")
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
                                playlist_ids.add(p.getString("id"));
                            }

                            Log.d("PlayListNames", playlist_names.toString());
                            runOnUiThread(new Runnable() {

                                public void run() {
                                    itemsAdapter.notifyDataSetChanged();

                                }
                            });



                        } catch (JSONException e) {

                        }
                    }
                });
        }

//        Log.d("MainMyAccessToken", response.getAccessToken());
//        Log.d("MainrequestCode", String.valueOf(requestCode));
//        Log.d("MainresultCode", String.valueOf(resultCode));
//        Log.d("MainIntentData", data.toString());
//        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
//            mAccessToken = response.getAccessToken();
//            Log.d("MainMyActivity", mAccessToken);
////            updateTokenView();
//        }
        }

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
    }
    private void setSpotifyMusicToSelector() {
        try {
            SpotifyMusicUtils.getSpotifyMusicPlaylists(getApplicationContext(), new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Log.e("Spotify Music", message);
                }

                @Override
                public void onResponse(Object response) {
                    try {
                        final JSONObject jsonObject = new JSONObject((String) response);
                        Log.d("JSON Object", jsonObject.toString());
                        JSONArray items = jsonObject.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject p = items.getJSONObject(i);
                            playlist_names.add(p.getString("name"));
                            playlist_ids.add(p.getString("id"));
                        }

                        Log.d("PlayListNames", playlist_names.toString());
                        runOnUiThread(new Runnable() {

                            public void run() {
                                itemsAdapter.notifyDataSetChanged();

                            }
                        });



                    } catch (JSONException e) {

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setAppleMusicToSelector() {
        try {
            AppleMusicUtils.getAppleMusicPlaylists(getApplicationContext(), new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Log.e("Apple Music", message);
                }

                @Override
                public void onResponse(Object response) {
                    try {
                        Log.d("Apple Music", "We got response");
                        JSONObject jsonObject = new JSONObject((String) response);
                        JSONArray playlists = jsonObject.getJSONArray("data");

                        playlist_names.clear();
                        appleMusicPlaylistIds.clear();

                        for (int i = 0; i < playlists.length(); i++) {
                            JSONObject playlist = playlists.getJSONObject(i);
                            JSONObject attributes = playlist.getJSONObject("attributes");
                            if (attributes.getBoolean("canEdit")) {
                                playlist_names.add(attributes.getString("name"));
                                appleMusicPlaylistIds.add(playlist.getString("id"));
                            }

                        }
                        runOnUiThread(new Runnable() {

                            public void run() {
                                itemsAdapter.notifyDataSetChanged();

                            }
                        });
//                        ArrayAdapter<String> itemsAdapter =
//                                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, playlist_names);
//                        grid_view.setAdapter(itemsAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}