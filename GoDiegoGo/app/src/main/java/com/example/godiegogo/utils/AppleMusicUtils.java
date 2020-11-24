package com.example.godiegogo.utils;

import android.content.Context;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.godiegogo.R;
import com.example.godiegogo.preferences.ApplePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AppleMusicUtils {

    // Construct a JSON structure for an empty Apple Music playlist
    public static JSONObject makeEmptyJSONPlaylist(String name, String description, JSONObject tracks) throws JSONException, IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }

        JSONObject playlist = new JSONObject();
        JSONObject attributes = new JSONObject();
        JSONObject relationships = new JSONObject();

        relationships.put("tracks", tracks);

        attributes.put("name", name);
        if (description != null && !description.isEmpty()) {
            attributes.put("description", description);
        }

        playlist.put("attributes", attributes);
        playlist.put("relationships", relationships);

        return playlist;
    }

    // Construct a list of song ID's to later pass in a PUT request
    public static JSONObject createSongList(ArrayList<String> songIds) throws JSONException {
        JSONObject songList = new JSONObject();
        JSONArray songDataList = new JSONArray();

        for (String id : songIds) {
            JSONObject songData = new JSONObject();
            songData.put("id", id);
            songData.put("type", "songs");
            songDataList.put(songData);
        }

        songList.put("data", songDataList);
        return songList;
    }

    // Generate the URL for a song search
    public static String generateSongSearchURL(String searchTerm) {
        String formattedSearchTerm = searchTerm.replace(' ', '+');
        return "https://api.music.apple.com/v1/catalog/us/search?term=" + formattedSearchTerm + "&types=songs";
    }

    // Make an API request to Apple Music API
    public static void makeApiRequest(Context context, int requestMethod, JSONObject jsonObjectForRequest, String url, final VolleyResponseListener listener) {
        Log.d("Api Request", "Method: " + requestMethod + " - Url: " + url);

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestMethod, url, jsonObjectForRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError("VolleyError: " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + context.getString(R.string.jwt_token));
                params.put("Music-User-Token", ApplePreferences.with(context).getUserToken());
                return params;
            }
        };
        queue.add(jsonObjectRequest);
    }


}
