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

    // Add a list of JSON formatted songs to a playlist
    public static void addSongsToPlaylist(Context context, JSONObject songs, String playlistId, final VolleyResponseListener listener) {
        String url = "https://api.music.apple.com/v1/me/library/playlists/" + playlistId + "/tracks";

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, songs, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onResponse(error);
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

    // Search for a song with the Apple Music API
    public static void searchForSong(Context context, String songName, final VolleyResponseListener listener) {
        String formattedSong = songName.replace(' ', '+');
        Log.d("song search", "Formatted Song: " + formattedSong);
        String url = "https://api.music.apple.com/v1/catalog/us/search?term=" + formattedSong + "&types=songs";
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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

    public static JSONObject synchronousSearchForSong(Context context, String songName) {
        String formattedSong = songName.replace(' ', '+');
        Log.d("song search", "Formatted Song: " + formattedSong);
        String url = "https://api.music.apple.com/v1/catalog/us/search?term=" + formattedSong + "&types=songs";
        RequestQueue queue = Volley.newRequestQueue(context);
        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), future, future) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + context.getString(R.string.jwt_token));
                params.put("Music-User-Token", ApplePreferences.with(context).getUserToken());
                return params;
            }
        };

        queue.add(jsonObjectRequest);
        try {

            JSONObject response = future.get(5, TimeUnit.SECONDS);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void addNewPlaylist(Context context, JSONObject playlist, final VolleyResponseListener listener) throws JSONException {
        String url = "https://api.music.apple.com/v1/me/library/playlists";

        RequestQueue queue = Volley.newRequestQueue(context);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, playlist, new Response.Listener<JSONObject>()
        {
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

    public static void getAppleMusicPlaylists(Context context, final VolleyResponseListener listener) throws JSONException, IllegalStateException {
        String userToken = ApplePreferences.with(context).getUserToken();

        if (userToken == null || userToken.isEmpty()) {
            throw new IllegalStateException();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = "https://api.music.apple.com/v1/me/library/playlists?limit=100";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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

        queue.add(stringRequest);
    }


}
