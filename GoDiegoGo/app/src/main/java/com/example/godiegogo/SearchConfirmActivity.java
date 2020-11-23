package com.example.godiegogo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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


public class SearchConfirmActivity extends AppCompatActivity {
    public ArrayList<String> playlist_songs;
    public ArrayList<String> playlist_url;
    public String url;
    private String accessToken;
    private static final String TAG = "MyActivity";
    private Call mCall;
//    private Call mCall_name;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private final OkHttpClient mOkHttpClient_name = new OkHttpClient();
    private ArrayAdapter<String> itemsAdapter;
    private String playlistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_confirm);

        playlist_songs = new ArrayList<String>();
        playlist_url = new ArrayList<String>();


        Bundle b = this.getIntent().getExtras();
        url = b.getString("url");
        accessToken = b.getString("accessToken");
        if (url.contains("spotify")) {
            // do spotify stuff
            String playlistId = url.substring(url.lastIndexOf("/") + 1);
            final Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/playlists/" + playlistId + "/tracks?limit=10")
                    .addHeader("Authorization","Bearer " + accessToken)
                    .build();
//            final Request request_name = new Request.Builder()
//                    .url("https://api.spotify.com/v1/playlists/" + playlistId)
//                    .addHeader("Authorization","Bearer " + accessToken)
//                    .build();

            cancelCall();
            mCall = mOkHttpClient.newCall(request);
//            mCall_name = mOkHttpClient_name.newCall(request_name);
//
//            mCall_name.enqueue(new Callback() {
//                public void onFailure(Call call, IOException e) {
//                    Log.d("Failure", "onFailureMethodCalled");
//                }
//
//                public void onResponse(Call call, Response response) throws IOException {
//                    try {
//                        final JSONObject jsonObject = new JSONObject(response.body().string());
//
//                        JSONArray items = jsonObject.getJSONArray("items");
//                        System.out.println(items);
//                        for (int i = 0; i < items.length(); i++) {
//                            JSONObject p = items.getJSONObject(i);
//                            playlistName = p.getString("name");
//                        }
//                        Log.d("playList", playlistName);
//                        runOnUiThread(new Runnable() {
//
//                            public void run() {
//                                TextView title = (TextView) findViewById(R.id.transfer_header);
//                                title.setText("Playlist " + playlistName + " was found on Spotify. Choose which service you'd like to transfer it to: ");
//                            }
//                        });
//
//                    } catch (JSONException e) {
//                        System.out.println(e);
////                        setResponse("Failed to parse data: " + e);
//                    }
//                }
//            });

            mCall.enqueue(new Callback() {
                public void onFailure(Call call, IOException e) {
                    Log.d("Failure", "onFailureMethodCalled");
//                setResponse("Failed to fetch data: " + e);
                }

                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().string());

                        JSONArray items = jsonObject.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject p = items.getJSONObject(i).getJSONObject("track");
                            playlist_songs.add(p.getString("name"));
//                            JSONObject extURL = items.getJSONObject(i).getJSONObject("external_urls");
//                            playlist_url.add(extURL.getString("spotify"));

                        }
                        runOnUiThread(new Runnable() {

                            public void run() {
                                itemsAdapter.notifyDataSetChanged();
                            }
                        });
                        System.out.println(playlist_songs.size());
                        Log.d("PlayListNames", playlist_songs.toString());

                    } catch (JSONException e) {
//                        setResponse("Failed to parse data: " + e);
                    }
                }
            });

        } else if (url.contains("music.apple.com")) {
            // do apple music stuff
        } else {
            // not spotify or apple
        }

        ListView playlist_preview = (ListView) findViewById(R.id.playlist_preview);
        itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, playlist_songs);
        playlist_preview.setAdapter(itemsAdapter);

        final Button button = findViewById(R.id.search_cancel_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
//        if (mCall_name != null) {
//            mCall.cancel();
//        }
    }

}
