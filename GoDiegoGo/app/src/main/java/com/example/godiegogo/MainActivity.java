package com.example.godiegogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.graphics.Color;
import android.content.Intent;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.os.Bundle;

import com.example.godiegogo.utils.AppleMusicUtils;
import com.example.godiegogo.utils.VolleyResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public ArrayList<String> playlist_names;
    public ArrayList<String> checked_playlists;
    private GridView grid_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checked_playlists = new ArrayList<String>();
        playlist_names = new ArrayList<String>();

        playlist_names.add("Playlist Name 1");
        playlist_names.add("Playlist Name 2");
        playlist_names.add("Playlist Name 3");
        playlist_names.add("Playlist Name 4");
        playlist_names.add("Playlist Name 5");
        playlist_names.add("Playlist Name 6");
        playlist_names.add("Playlist Name 7");
        playlist_names.add("Playlist Name 8");
        playlist_names.add("Playlist Name 9");
        playlist_names.add("Playlist Name 10");
        playlist_names.add("Playlist Name 11");
        playlist_names.add("Playlist Name 12");
        playlist_names.add("Playlist Name 13");

        grid_view = (GridView) findViewById(R.id.playlist_selector);
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, playlist_names);
        grid_view.setAdapter(itemsAdapter);
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

                int leftButtonID = leftButton.getId();
                if (leftButtonID == 2131231049) {
                    Log.d("Apple Music", "leftbuttonis apple music");
                    setAppleMusicToSelector();
                }
            }
        });

        final ImageButton spotify_button = findViewById(R.id.spotify_icon);
        spotify_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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

                        for (int i = 0; i < playlists.length(); i++) {
                            JSONObject playlist = playlists.getJSONObject(i);
                            JSONObject attributes = playlist.getJSONObject("attributes");
                            playlist_names.add(attributes.getString("name"));
                        }

                        ArrayAdapter<String> itemsAdapter =
                                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, playlist_names);
                        grid_view.setAdapter(itemsAdapter);

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