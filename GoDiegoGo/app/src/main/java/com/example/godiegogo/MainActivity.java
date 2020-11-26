package com.example.godiegogo;

import androidx.appcompat.app.AppCompatActivity;


import android.net.Uri;

import android.content.res.ColorStateList;

import android.os.Debug;
import android.util.Log;
import android.util.Patterns;
import android.util.SparseBooleanArray;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.graphics.Color;
import android.content.Intent;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.os.Bundle;

import com.android.volley.Request;
import com.example.godiegogo.preferences.SpotifyPreferences;
import com.example.godiegogo.utils.SpotifyMusicUtils;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;
import android.content.res.ColorStateList;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.AdapterView;
import com.example.godiegogo.preferences.ApplePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import com.example.godiegogo.utils.AppleMusicUtils;
import com.example.godiegogo.utils.VolleyResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    public ArrayList<String> playlistNames;
    public ArrayList<String> playlistIds;
    public static final String CLIENT_ID = "17c1c4ef3eda4cd0afa1abe42019cac7";
    public static final String REDIRECT_URI = "GoDiegoGo-login://callback";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;
    public static final int MAIN_ACTIVITY = 1;
    public static final int SERVICE_SELECTOR_LEFT_ACTIVITY = 2;
    public static final int SERVICE_SELECTOR_RIGHT_ACTIVITY = 3;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessCode;
    private String userId;
    private Call mCall;
    private ArrayAdapter<String> itemsAdapter;
    public ArrayList<String> checked_playlists;
    private int button_one_id;
    private int button_two_id;

    public ArrayList<String> checked_playlist_ids;


    private GridView grid_view;

    public enum Service {
        APPLE_MUSIC,
        SPOTIFY,
        TIDAL,
        GOOGLE_PLAY
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checked_playlists = new ArrayList<String>();
        checked_playlist_ids = new ArrayList<String>();
        playlistNames = new ArrayList<String>();
        playlistIds = new ArrayList<String>();
        itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, playlistNames);

        // Temporary Measure until we figure out requesting refresh keys
        SpotifyPreferences.with(getApplicationContext()).setSpotifyUserToken(null);

        grid_view = (GridView) findViewById(R.id.playlist_selector);
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
                    int i = playlistNames.indexOf(checkedTextView.getText().toString());
                    checked_playlist_ids.add(playlistIds.get(i));

                } else {
                    int i = playlistNames.indexOf(checkedTextView.getText().toString());
                    checked_playlist_ids.remove(playlistIds.get(i));
                    checked_playlists.remove(checkedTextView.getText().toString());

                }
                updateSyncTransferButtons();
            }
        });

        if (mAccessToken != null) {

        }

        final EditText search_box = (EditText) findViewById(R.id.playlist_link);
        final ImageButton search_enter = findViewById(R.id.url_enter_button);
        search_enter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String urlString = search_box.getText().toString().trim();
                if (!urlString.startsWith("https://")) {
                    urlString = "https://" + urlString;
                }
                try {
                    URL url = new URL(urlString);
                    if (URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches()) {
                        Bundle b = new Bundle();
                        b.putString("url", urlString);
                        b.putString("accessToken", mAccessToken);
                        Intent intent = new Intent(v.getContext(), SearchConfirmActivity.class);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                } catch (MalformedURLException ignored) {
                    Log.d("MalformedURLException", "it hit this");
                }
            }
        });

        final Button transfer_button = findViewById(R.id.transfer_button);
        transfer_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = getSyncOrTransferBundle();
                b.putString("transfer_type", "Transferring");
                Intent intent = new Intent(v.getContext(), LoadingPageActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        final Button sync_button = findViewById(R.id.sync_button);
        sync_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = getSyncOrTransferBundle();
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

                updatePlaylistSelector();
                clearCheckboxes();
            }
        });

        final ImageButton button_one = findViewById(R.id.icon_one);
        button_one_id = button_one.getId();
        button_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                Intent intent = new Intent(v.getContext(), ServiceSelectorActivity.class);
                LinearLayout layout = findViewById(R.id.transfer_icon_list);
                int leftButtonId = layout.getChildAt(0).getId();
                if (leftButtonId == button_one_id) {
                    startActivityForResult(intent, SERVICE_SELECTOR_LEFT_ACTIVITY);
                } else {
                    startActivityForResult(intent, SERVICE_SELECTOR_RIGHT_ACTIVITY);
                }
            }
        });

        final ImageButton button_two = findViewById(R.id.icon_two);
        button_two_id = button_two.getId();
        button_two.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                Intent intent = new Intent(v.getContext(), ServiceSelectorActivity.class);
                LinearLayout layout = findViewById(R.id.transfer_icon_list);
                int leftButtonId = layout.getChildAt(0).getId();
                if (leftButtonId == button_two_id) {
                    startActivityForResult(intent, SERVICE_SELECTOR_LEFT_ACTIVITY);
                } else {
                    startActivityForResult(intent, SERVICE_SELECTOR_RIGHT_ACTIVITY);
                }
            }
        });
        updatePlaylistSelector();
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
        if (resultCode == RESULT_OK && (requestCode == SERVICE_SELECTOR_LEFT_ACTIVITY || requestCode == SERVICE_SELECTOR_RIGHT_ACTIVITY)) {
            LinearLayout layout = findViewById(R.id.transfer_icon_list);
            //update the button icon to match chosen service
            if (data.hasExtra("chosenService")) {
                ImageButton buttonToSwap;
                if (requestCode == SERVICE_SELECTOR_LEFT_ACTIVITY) {
                    buttonToSwap = (ImageButton)layout.getChildAt(0);
                    clearCheckboxes();
                } else {
                    buttonToSwap = (ImageButton) layout.getChildAt(2);
                }
                if (data.getStringExtra("chosenService").equals("Spotify")) {
                    buttonToSwap.setImageResource(R.drawable.spotify_icon_small);
                    buttonToSwap.setTag("spotify_button");
                } else if (data.getStringExtra("chosenService").equals("Apple Music")){
                    buttonToSwap.setImageResource(R.drawable.apple_music_logo);
                    buttonToSwap.setTag("apple_button");
                }
            }
            if (data.hasExtra("userId")) {
                userId = data.getStringExtra("userId");
                SpotifyPreferences.with(getApplicationContext()).setSpotifyUserId(userId);
            }
            if (data.hasExtra("mAccessToken")) {
                mAccessToken = data.getStringExtra("mAccessToken");
                SpotifyPreferences.with(getApplicationContext()).setSpotifyUserToken(mAccessToken);
            }
        }
        updatePlaylistSelector();
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

                        playlistIds.clear();
                        playlistNames.clear();
                        checked_playlist_ids.clear();
                        checked_playlists.clear();

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject p = items.getJSONObject(i);
                            playlistNames.add(p.getString("name"));
                            playlistIds.add(p.getString("id"));
                        }

                        Log.d("PlayListNames", playlistNames.toString());
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
        AppleMusicUtils.makeApiRequest(getApplicationContext(), Request.Method.GET, null, getString(R.string.apple_music_grab_playlists_url), new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.d("Apple Music Playlists", message);
            }

            @Override
            public void onResponse(Object response) {
                try {
                    Log.d("Apple Music", "We got response");
                    JSONObject jsonObject = (JSONObject) response;
                    JSONArray playlists = jsonObject.getJSONArray("data");

                    playlistNames.clear();
                    playlistIds.clear();
                    checked_playlist_ids.clear();
                    checked_playlists.clear();

                    for (int i = 0; i < playlists.length(); i++) {
                        JSONObject playlist = playlists.getJSONObject(i);
                        JSONObject attributes = playlist.getJSONObject("attributes");
                        playlistNames.add(attributes.getString("name"));
                        playlistIds.add(playlist.getString("id"));


                    }
                    runOnUiThread(new Runnable() {

                        public void run() {
                            itemsAdapter.notifyDataSetChanged();

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void updateSyncTransferButtons() {
        LinearLayout layout = findViewById(R.id.transfer_icon_list);
        ImageButton leftButton = (ImageButton)layout.getChildAt(0);
        ImageButton rightButton = (ImageButton)layout.getChildAt(2);
        Button transferButton = findViewById(R.id.transfer_button);
        Button syncButton = findViewById(R.id.sync_button);

        if (leftButton.getTag() != null && rightButton.getTag() != null && checked_playlists.size() > 0) {
            transferButton.setEnabled(true);
            syncButton.setEnabled(true);
        } else {
            transferButton.setEnabled(false);
            syncButton.setEnabled(false);
        }
    }

    private void clearCheckboxes() {
        Log.println(Log.DEBUG, "grace", "in clearCheckboxes() method");
        GridView grid_view = (GridView)findViewById(R.id.playlist_selector);
        for(int i = 0; i < grid_view.getChildCount(); i++){
            Log.println(Log.DEBUG, "grace", grid_view.getChildAt(i).toString()); // You should probably cast to your adapter's item type
            CheckedTextView checked_text = (CheckedTextView)grid_view.getChildAt(i);
            checked_text.setChecked(false);
        }
        checked_playlist_ids.clear();
        checked_playlists.clear();
        updateSyncTransferButtons();
    }

    private void updatePlaylistSelector() {
        LinearLayout layout = findViewById(R.id.transfer_icon_list);
        ImageButton leftButton = (ImageButton)layout.getChildAt(0);
        updateSyncTransferButtons();

        if (leftButton.getTag() != null && leftButton.getTag().equals("apple_button")) {
            Log.d("Playlist Selector", "Left button is Apple Music; adding playlists");
            setAppleMusicToSelector();
        } else if (leftButton.getTag() != null && leftButton.getTag().equals("spotify_button")) {
            Log.d("Playlist Selector", "Left button is Spotify; adding playlists.");
            setSpotifyMusicToSelector();
        }
    }

    private Bundle getSyncOrTransferBundle() {
        Bundle b = new Bundle();
        LinearLayout layout = findViewById(R.id.transfer_icon_list);
        ImageButton leftButton = (ImageButton)layout.getChildAt(0);

        b.putStringArrayList("checked_playlists", checked_playlists);
        b.putStringArrayList("checked_playlist_ids", checked_playlist_ids);
        Log.d("Transfer", "CheckedPlaylists: " + checked_playlists.toString());
        Log.d("Transfer", "CheckedPlaylistIds: " + checked_playlist_ids.toString());

        if (leftButton.getTag().equals("spotify_button")) {
            Log.d("Transfer", "Transferring Spotify Playlists");
            b.putString("mAccessToken", mAccessToken);
            b.putString("userId", userId);
            b.putSerializable("transferFrom", Service.SPOTIFY);
            b.putSerializable("transferTo", Service.APPLE_MUSIC);
        } else if (leftButton.getTag().equals("apple_button")) {
            Log.d("Transfer", "Transferring Apple Music Playlists");
            b.putSerializable("transferFrom", Service.APPLE_MUSIC);
            b.putSerializable("transferTo", Service.SPOTIFY);
        }
        return b;
    }

}