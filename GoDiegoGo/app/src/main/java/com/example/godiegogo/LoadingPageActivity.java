package com.example.godiegogo;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.content.Intent;

import java.io.IOException;
import java.util.LinkedList;
import java.util.ArrayList;
import com.example.godiegogo.R;
import com.example.godiegogo.preferences.ApplePreferences;
import com.example.godiegogo.utils.AppleMusicUtils;
import com.example.godiegogo.utils.SpotifyMusicUtils;
import com.example.godiegogo.utils.VolleyResponseListener;

import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoadingPageActivity extends AppCompatActivity {
    public ArrayList<String> copiedSongs;
    public ArrayList<String> failedSongs;
    public ArrayList<String> checkedPlaylists;
    public ArrayList<String> checkedPlaylistIds;
    private ArrayList<String> newPlaylistIds;
    private ArrayList<String> songsToCopy;
    private ArrayList<String> songIdsToCopy;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private int amtFailed;
    private String mAccessToken;
    private String userId;
    private Call mCall;
    private ArrayAdapter<String> itemsAdapter;


    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);

        newPlaylistIds = new ArrayList<>();
        songsToCopy = new ArrayList<>();


        spotifyToApple();

        final Button button = findViewById(R.id.cancel_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        final Button transferButton = findViewById(R.id.confirm_transfer);
        transferButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    JSONObject tracksToAdd = AppleMusicUtils.createSongList(songIdsToCopy);
                    JSONObject playlistToAdd = AppleMusicUtils.makeEmptyJSONPlaylist(checkedPlaylists.get(0), null, tracksToAdd);
                    AppleMusicUtils.addNewPlaylist(getApplicationContext(), playlistToAdd, new VolleyResponseListener() {
                        @Override
                        public void onError(String message) {
                            Log.d("Adding Playlist", "playlist failed");
                        }

                        @Override
                        public void onResponse(Object response) {
                            Log.d("Adding Playlist", "Playlist Added");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Bundle b = new Bundle();
                b.putStringArrayList("failed_songs", failedSongs);
                b.putString("transfer_type", "Transfer");
                Intent intent = new Intent(LoadingPageActivity.this, LoadingPageResultsActivity.class);
                intent.putExtras(b);

                //Intent intent = new Intent(this, LoadingPageResultsActivity.class);
                startActivity(intent);
                finish();
            }


        });
        ListView lv = (ListView) findViewById(R.id.songlist);
        itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songsToCopy);
        lv.setAdapter(itemsAdapter);

    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    private void spotifyToApple() {
        Bundle b = this.getIntent().getExtras();
        copiedSongs = new ArrayList<>();
        failedSongs = new ArrayList<>();
        songIdsToCopy = new ArrayList<>();
        amtFailed = 0;
        // Get data from bundle
        checkedPlaylists = b.getStringArrayList("checked_playlists");
        checkedPlaylistIds = b.getStringArrayList("checked_playlist_ids");
        mAccessToken = b.getString("mAccessToken");
        userId = b.getString("userId");
        String transferType = b.getString("transfer_type");

        // Get views to modify UI
        TextView transferHeader = (TextView) findViewById(R.id.transfer_header);
        ListView transferredSongs = (ListView) findViewById(R.id.songlist);

        transferHeader.setText("Finding songs on: ");

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, copiedSongs);
        transferredSongs.setAdapter(itemsAdapter);

        ArrayList<String> songNames = new ArrayList<>();
        SpotifyMusicUtils.getPlaylistFromId(getApplicationContext(), checkedPlaylistIds.get(0), new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.e("Get Spotify Playlist", message);
            }

            @Override
            public void onResponse(Object response) {
                final JSONObject playlist = (JSONObject) response;
                String playlistName = null;

                Log.d("Adding Playlist", "got spotify playlist");
                try {
                    Log.d("Adding Playlist", playlist.toString(2));
                    JSONArray songs = playlist.getJSONArray("items");
                    for (int i = 0; i < songs.length(); i++) {
                        JSONObject song = songs.getJSONObject(i).getJSONObject("track");
                        songNames.add(song.getString("name"));
                    }

                    Log.d("Adding Playlist", "searching song names");

                    for (int i = 0; i < songNames.size(); i++) {
                        searchSongOnAppleMusic(songNames.get(i));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void searchSongOnAppleMusic(String songName) {
        AppleMusicUtils.searchForSong(getApplicationContext(), songName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.e("Search Song", message);
            }

            @Override
            public void onResponse(Object response) {
                JSONObject formattedResponse = (JSONObject) response;

                JSONObject song = null;
                JSONObject songAttributes = null;

                try {
                    song = formattedResponse.getJSONObject("results").getJSONObject("songs").getJSONArray("data").getJSONObject(0);
                    songAttributes = song.getJSONObject("attributes");

                    Log.d("song search", "Name: " + songAttributes.getString("name"));
                    Log.d("song search", "ID: " + song.getString("id"));
                    songIdsToCopy.add(song.getString("id"));
                    songsToCopy.add(songAttributes.getString("name"));

                    Log.d("song search", songsToCopy.toString());
                    runOnUiThread(new Runnable() {
                        public void run() {
                            itemsAdapter.notifyDataSetChanged();
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                    amtFailed++;
                }
            }
        });

    }
    
}
