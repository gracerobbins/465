package com.example.godiegogo;

import android.graphics.Color;
import android.os.Build;
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
import android.widget.ProgressBar;
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
    private ProgressBar progressBar;


    protected void onCreate(Bundle savedInstanceState) {

        // Grab and set up views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        progressBar = (ProgressBar) findViewById(R.id.determinateBar);
        progressBar.setProgress(0);

        // Initialize class variables
        copiedSongs = new ArrayList<>();
        newPlaylistIds = new ArrayList<>();
        songsToCopy = new ArrayList<>();
        failedSongs = new ArrayList<>();
        songIdsToCopy = new ArrayList<>();

        amtFailed = 0;

        // Set listener for cancel button
        final Button button = findViewById(R.id.cancel_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // Set listener for confirm transfer button
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

        // Transfer songs from Spotify to Apple Music
        spotifyToApple();


        // Set up listview to update songs
        ListView lv = (ListView) findViewById(R.id.songlist);
        itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, copiedSongs);
        lv.setAdapter(itemsAdapter);

    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    private void spotifyToApple() {

        // Get data from bundle
        Bundle b = this.getIntent().getExtras();
        checkedPlaylists = b.getStringArrayList("checked_playlists");
        checkedPlaylistIds = b.getStringArrayList("checked_playlist_ids");
        mAccessToken = b.getString("mAccessToken");
        userId = b.getString("userId");
        String transferType = b.getString("transfer_type");

        // Get views to modify UI
        TextView transferHeader = (TextView) findViewById(R.id.transfer_header);
        transferHeader.setText("Finding songs on: ");


        // Grab playlist from Spotify
        ArrayList<String> songNames = new ArrayList<>();
        SpotifyMusicUtils.getPlaylistFromId(getApplicationContext(), checkedPlaylistIds.get(0), new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.e("Get Spotify Playlist", message);
            }

            // Handle a good response from the Spotify API
            @Override
            public void onResponse(Object response) {
                final JSONObject playlist = (JSONObject) response;
                String playlistName = null;

                // Populate list of songs to search for
                Log.d("Adding Playlist", "got spotify playlist");
                try {
                    Log.d("Adding Playlist", playlist.toString(2));
                    JSONArray songs = playlist.getJSONArray("items");
                    // Combine song name and artist name for improved search across services
                    for (int i = 0; i < songs.length(); i++) {
                        JSONObject song = songs.getJSONObject(i).getJSONObject("track");
                        JSONObject artist = song.getJSONArray("artists").getJSONObject(0);
                        songNames.add(song.getString("name") + " " + artist.getString("name"));
                    }

                    songsToCopy = songNames;

                    // Make async call to search for song on Apple Music
                    Log.d("Adding Playlist", "Searching song names");
                    for (int i = 0; i < songNames.size(); i++) {
                        searchSongOnAppleMusic(songNames.get(i));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // Given a song name, search for it on Apple Music and add the ID to songIdsToCopy[]
    private void searchSongOnAppleMusic(String songName) {
        AppleMusicUtils.searchForSong(getApplicationContext(), songName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.e("Search Song", message);
            }

            // Handle correct response returned
            @Override
            public void onResponse(Object response) {
                // Format response and set up variables
                JSONObject formattedResponse = (JSONObject) response;
                JSONArray songList = null;
                JSONObject song = null;
                JSONObject songAttributes = null;

                try {
                    // Get the song and attributes
                    songList = formattedResponse.getJSONObject("results").getJSONObject("songs").getJSONArray("data");

                    // If there are no search results, add the song name to the missing songs list
                    if (songList.length() == 0) {
                        failedSongs.add(songName);
                        amtFailed++;
                    } else {
                        song = songList.getJSONObject(0);
                        songAttributes = song.getJSONObject("attributes");

                        // Confirm song in logs, and add the ID and name to respective lists
                        Log.d("song search", "Name: " + songAttributes.getString("name"));
                        Log.d("song search", "Artist: " + songAttributes.getString("artistName"));
                        Log.d("song search", "ID: " + song.getString("id"));
                        songIdsToCopy.add(song.getString("id"));
                        copiedSongs.add(songAttributes.getString("name"));
                    }

                    // Update UI to reflect the songs found as well as the progress bar to show the overall progress
                    runOnUiThread(new Runnable() {
                        public void run() {
                            itemsAdapter.notifyDataSetChanged();
                            double amtCopied = copiedSongs.size();
                            double amtToCopy = songsToCopy.size();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                progressBar.setProgress( (int) ((amtFailed + amtCopied) / amtToCopy * 100), true);
                            } else {
                                progressBar.setProgress((int) (amtCopied / amtToCopy * 100));
                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                    failedSongs.add(songName);
                    amtFailed++;

                    runOnUiThread(new Runnable() {
                        public void run() {
                            itemsAdapter.notifyDataSetChanged();
                            double amtCopied = copiedSongs.size();
                            double amtToCopy = songsToCopy.size();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                progressBar.setProgress( (int) ((amtFailed + amtCopied) / amtToCopy * 100), true);
                            } else {
                                progressBar.setProgress((int) (amtCopied / amtToCopy * 100));
                            }
                        }
                    });
                }
            }
        });

    }

}
