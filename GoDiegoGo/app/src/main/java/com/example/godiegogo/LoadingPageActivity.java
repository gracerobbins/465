package com.example.godiegogo;

import android.app.Service;
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
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.example.godiegogo.R;
import com.example.godiegogo.preferences.ApplePreferences;
import com.example.godiegogo.preferences.SpotifyPreferences;
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
import org.json.JSONStringer;
import org.w3c.dom.Text;

import okhttp3.Call;
import okhttp3.OkHttpClient;

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
    private Runnable updateProgressBar;
    private MainActivity.Service transferFrom;
    private MainActivity.Service transferTo;
    private int currentPlaylist;


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

        // Get info from Bundle
        Bundle b = this.getIntent().getExtras();
        transferFrom = (MainActivity.Service) b.getSerializable("transferFrom");
        transferTo = (MainActivity.Service) b.getSerializable("transferTo");
        checkedPlaylists = b.getStringArrayList("checked_playlists");
        checkedPlaylistIds = b.getStringArrayList("checked_playlist_ids");
        mAccessToken = b.getString("mAccessToken");
        userId = b.getString("userId");
        currentPlaylist = b.getInt("current_playlist");

        Log.d("checkedPlaylists", checkedPlaylists.toString());
        Log.d("current playlist", checkedPlaylists.get(currentPlaylist));


        // Set service icons
        ImageButton leftService = findViewById(R.id.leftService);
        ImageButton rightService = findViewById(R.id.rightService);
        if (transferFrom == MainActivity.Service.APPLE_MUSIC) {
            leftService.setImageDrawable(getDrawable(R.drawable.apple_music_logo));
        } else if (transferFrom == MainActivity.Service.SPOTIFY) {
            leftService.setImageDrawable(getDrawable(R.drawable.spotify_icon_small));
        }

        if (transferTo == MainActivity.Service.APPLE_MUSIC) {
            rightService.setImageDrawable(getDrawable(R.drawable.apple_music_logo));
        } else if (transferTo == MainActivity.Service.SPOTIFY) {
            rightService.setImageDrawable(getDrawable(R.drawable.spotify_icon_small));
        }

        // Set listener for cancel button
        final Button button = findViewById(R.id.cancel_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        //If user is syncing, set the transfer button text accordingly
        final Button transferButton = findViewById(R.id.confirm_transfer);
        String transferType = b.getString("transfer_type");

        // Set listener for confirm transfer button
        transferButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (transferTo == MainActivity.Service.APPLE_MUSIC) {
                    addPlaylistToAppleMusic();
                } else if (transferTo == MainActivity.Service.SPOTIFY) {
                    Log.d("Transfer", "Transferring to Spotify");
                    addPlaylistToSpotify();
                }
                Bundle b = new Bundle();
                b.putStringArrayList("failed_songs", failedSongs);
                b.putString("transfer_type", transferType);
                b.putInt("current_playlist", currentPlaylist);
                b.putStringArrayList("checked_playlists", checkedPlaylists);
                b.putStringArrayList("checked_playlist_ids", checkedPlaylistIds);
                b.putString("transfer_type", transferType);
                b.putString("mAccessToken", mAccessToken);
                b.putString("userId", userId);
                b.putSerializable("transferTo", transferTo);
                b.putSerializable("transferFrom", transferFrom);
                Intent intent = new Intent(LoadingPageActivity.this, LoadingPageResultsActivity.class);
                intent.putExtras(b);

                //Intent intent = new Intent(this, LoadingPageResultsActivity.class);
                startActivity(intent);
                finish();
            }


        });

        // Check which service we are transferring from and to
        if (transferFrom == MainActivity.Service.SPOTIFY && transferTo == MainActivity.Service.APPLE_MUSIC) {
            spotifyToApple();
        } else if (transferFrom == MainActivity.Service.APPLE_MUSIC && transferTo == MainActivity.Service.SPOTIFY) {
            appleToSpotify();
        }


        // Set up listview to update songs
        ListView lv = (ListView) findViewById(R.id.songlist);
        itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, copiedSongs);
        lv.setAdapter(itemsAdapter);

        updateProgressBar = new Runnable() {
            @Override
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
        };

    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    private void spotifyToApple() {
        // Get views to modify UI
        TextView transferHeader = (TextView) findViewById(R.id.transfer_header);
        transferHeader.setText("Finding songs on Apple Music: ");


        // Grab playlist from Spotify
        ArrayList<String> songNames = new ArrayList<>();
        SpotifyMusicUtils.getPlaylistFromId(getApplicationContext(), checkedPlaylistIds.get(currentPlaylist), new VolleyResponseListener() {
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

    private void appleToSpotify() {

        // Get views to modify UI
        TextView transferHeader = (TextView) findViewById(R.id.transfer_header);
        transferHeader.setText("Finding songs on Spotify: ");

        // Grab Playlist from Apple Music
        ArrayList<String> songNames = new ArrayList<>();

        String url = "https://api.music.apple.com/v1/me/library/playlists/" + checkedPlaylistIds.get(currentPlaylist) + "/tracks";
        AppleMusicUtils.makeApiRequest(getApplicationContext(), Request.Method.GET, null, url, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.e("Get Apple Playlist", message);
            }

            @Override
            public void onResponse(Object response) {
                final JSONObject playlist = (JSONObject) response;

                // Populate list of songs to search for
                try {
                    Log.d("Adding Playlist", playlist.toString(2));
                    JSONArray data = playlist.getJSONArray("data");

                    // Combine song name and artist name for improved search across services
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject song = data.getJSONObject(i);
                        JSONObject songAttributes = song.getJSONObject("attributes");

                        String songName = songAttributes.getString("name");
                        String artistName = songAttributes.getString("artistName");
                        songNames.add(songName + " " + artistName);
                    }

                    songsToCopy = songNames;

                    //Make async call to search for song on Spotify
                    Log.d("Adding Playlist", "Searching song names");
                    for (int i = 0; i < songNames.size(); i++) {
                        searchSongOnSpotify(songNames.get(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Given a song name, search for it on Apple Music and add the ID to songIdsToCopy[]
    private void searchSongOnAppleMusic(String songName) {
        String url = AppleMusicUtils.generateSongSearchURL(songName);
        AppleMusicUtils.makeApiRequest(getApplicationContext(), Request.Method.GET, null, url, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.e("Search Song", message);
                failedSongs.add(songName);
                amtFailed++;
                runOnUiThread(updateProgressBar);
            }

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
                        Log.d("song search", "ID: " + song.getString("id"));
                        songIdsToCopy.add(song.getString("id"));
                        copiedSongs.add(songAttributes.getString("name"));
                    }

                    // Update UI to reflect the songs found as well as the progress bar to show the overall progress
                    runOnUiThread(updateProgressBar);


                } catch (Exception e) {
                    e.printStackTrace();
                    failedSongs.add(songName);
                    amtFailed++;

                    runOnUiThread(updateProgressBar);
                }
            }
        });

    }

    private void searchSongOnSpotify(String query) {
        String formattedQuery = query.replace(' ', '+');
        String url = "https://api.spotify.com/v1/search?q=" + formattedQuery + "&type=track&market=us";

        SpotifyMusicUtils.makeApiRequest(getApplicationContext(), Request.Method.GET, null, url, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Log.e("Search Song", message);
                failedSongs.add(query);
                amtFailed++;
                runOnUiThread(updateProgressBar);
            }

            @Override
            public void onResponse(Object response) {
                // Formate response and set up variables
                JSONObject formattedResponse = (JSONObject) response;
                JSONArray items = null;
                JSONObject song = null;

                try {
                    // Get the song and attributes
                    items = formattedResponse.getJSONObject("tracks").getJSONArray("items");

                    // If there's no search results, add the song name to the missing songs list
                    if (items.length() == 0) {
                        failedSongs.add(query);
                        amtFailed++;
                    } else {
                        song = items.getJSONObject(0);
                        String uri = song.getString("uri");
                        String name = song.getString("name");

                        // Confirm song in logs, and add the ID and name to respective lists
                        Log.d("song search", "Name: " + name);
                        Log.d("song search", "Uri: " + uri);
                        songIdsToCopy.add(uri);
                        copiedSongs.add(name);
                    }

                    // Update UI to reflect the songs found as well as the progress bar to show the overall process
                    runOnUiThread(updateProgressBar);

                } catch (Exception e) {
                    e.printStackTrace();
                    failedSongs.add(query);
                    amtFailed++;
                    runOnUiThread(updateProgressBar);
                }

            }
        });
    }

    private void addPlaylistToAppleMusic() {
        try {
            JSONObject tracksToAdd = AppleMusicUtils.createSongList(songIdsToCopy);
            JSONObject playlistToAdd = AppleMusicUtils.makeEmptyJSONPlaylist(checkedPlaylists.get(currentPlaylist), null, tracksToAdd);
            AppleMusicUtils.makeApiRequest(getApplicationContext(), Request.Method.POST, playlistToAdd, getString(R.string.apple_music_create_playlist_url), new VolleyResponseListener() {
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
    }

    private void addPlaylistToSpotify() {
        try {
            JSONObject newPlaylist = SpotifyMusicUtils.makeEmptyJSONPlaylist(checkedPlaylists.get(currentPlaylist), null);
            Log.d("Adding Playlist", newPlaylist.toString(3));
            String url = "https://api.spotify.com/v1/users/" + SpotifyPreferences.with(getApplicationContext()).getUserID() + "/playlists";
            SpotifyMusicUtils.makeApiRequest(getApplicationContext(), Request.Method.POST, newPlaylist, url, new VolleyResponseListener() {

                @Override
                public void onError(String message) {
                    Log.e("Adding Playlist", message);
                }

                @Override
                public void onResponse(Object response) {
                    Log.d("Adding Playlist", "New playlist added");
                    Log.d("Adding Playlist", "Songs to add: " + songIdsToCopy.toString());
                    JSONObject newPlaylistJson = (JSONObject) response;
                    JSONArray jsonSongIds = new JSONArray();
                    for (String id : songIdsToCopy) {
                        jsonSongIds.put(id);
                    }

                    String songURL = null;

                    try {
                        JSONObject apiRequestJson = new JSONObject();
                        apiRequestJson.put("uris", jsonSongIds);
                        songURL = "https://api.spotify.com/v1/playlists/" + newPlaylistJson.getString("id") + "/tracks";
                        SpotifyMusicUtils.makeApiRequest(getApplicationContext(), Request.Method.POST, apiRequestJson, songURL, new VolleyResponseListener() {
                            @Override
                            public void onError(String message) {
                                Log.e("Adding Playlist", message);
                            }

                            @Override
                            public void onResponse(Object response) {
                                Log.d("Adding Playlist", "Playlist added?");
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
