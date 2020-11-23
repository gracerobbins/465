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
    public ArrayList<String> copied_songs;
    public ArrayList<String> failed_songs;
    public ArrayList<String> checked_playlists;
    public ArrayList<String> checked_playlist_ids;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String userId;
    private Call mCall;
    private ArrayAdapter<String> itemsAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);

        Bundle b = this.getIntent().getExtras();
        copied_songs = new ArrayList<String>();
        failed_songs = new ArrayList<String>();
        checked_playlists = b.getStringArrayList("checked_playlists");
        checked_playlist_ids = b.getStringArrayList("checked_playlist_ids");
        mAccessToken = b.getString("mAccessToken");
        userId = b.getString("userId");
        String transfer_type = b.getString("transfer_type");
        TextView transfer_header = (TextView)findViewById(R.id.transfer_header);
        transfer_header.setText(transfer_type + " Your Music:");
        ListView lv = (ListView) findViewById(R.id.songlist);
        itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, copied_songs);
        lv.setAdapter(itemsAdapter);
        Log.d("LPA: checkedPlaylists", checked_playlists.toString());
        Log.d("LPA: checkedPlaylistIds", checked_playlist_ids.toString());
        for (int i = 0; i < checked_playlists.size(); i++) {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            Log.d("for loop counter", String.valueOf(i));
            final Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/playlists/" + checked_playlist_ids.get(i) + "/tracks?market=US&limit=30")
                    .addHeader("Authorization","Bearer " + mAccessToken)
                    .build();

            cancelCall();
            mCall = mOkHttpClient.newCall(request);

            mCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("Call request", call.request().toString());
                    Log.d("Failure", "onFailureMethodCalled");
                    Log.d("Exception", e.toString());
//                setResponse("Failed to fetch data: " + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().string());

                        JSONArray items = jsonObject.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject p = items.getJSONObject(i).getJSONObject("track");
                            copied_songs.add(p.getString("name"));
                            if (i % 5 == 0) {
                                failed_songs.add(p.getString("name"));
                            }
                        }

                        Log.d("CopiedSongNames", copied_songs.toString());
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
        }




//        copied_songs.add("Song Name 1");
//        copied_songs.add("Song Name 2");
//        copied_songs.add("Song Name 3");
//        copied_songs.add("Song Name 1");
//        copied_songs.add("Song Name 2");
//        copied_songs.add("Song Name 3");
//        copied_songs.add("Song Name 1");
//        copied_songs.add("Song Name 2");
//        copied_songs.add("Song Name 3");
//        copied_songs.add("Song Name 1");
//        copied_songs.add("Song Name 2");
//        copied_songs.add("Song Name 3");
//
//        failed_songs = new ArrayList<String>();
//        failed_songs.add("Song Name 4");
//        failed_songs.add("Song Name 5");
//        failed_songs.add("Song Name 6");
//        failed_songs.add("Song Name 7");
//        failed_songs.add("Song Name 8");
//        failed_songs.add("Song Name 9");
//        failed_songs.add("Song Name 10");
//        failed_songs.add("Song Name 11");
//        failed_songs.add("Song Name 12");
//        failed_songs.add("Song Name 13");



        final Button button = findViewById(R.id.cancel_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // yourMethod();
                Bundle b = new Bundle();
                b.putStringArrayList("failed_songs", failed_songs);
                b.putString("transfer_type", transfer_type);
                Intent intent = new Intent(LoadingPageActivity.this, LoadingPageResultsActivity.class);
                intent.putExtras(b);

                //Intent intent = new Intent(this, LoadingPageResultsActivity.class);
                startActivity(intent);
                finish();
            }
        }, 10000);   //5 seconds
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
