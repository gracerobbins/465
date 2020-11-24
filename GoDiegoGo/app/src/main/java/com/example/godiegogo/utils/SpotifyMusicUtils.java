package com.example.godiegogo.utils;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.godiegogo.R;
import com.example.godiegogo.preferences.ApplePreferences;
import com.example.godiegogo.preferences.SpotifyPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SpotifyMusicUtils {

    public static void getSpotifyMusicPlaylists(Context context, final VolleyResponseListener listener) throws JSONException, IllegalStateException {

        RequestQueue queue = Volley.newRequestQueue(context);
        String userId = SpotifyPreferences.with(context).getUserID();
        String mAccessToken = SpotifyPreferences.with(context).getUserToken();
        String url = "https://api.spotify.com/v1/users/" + userId + "/playlists";

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
                params.put("Authorization", "Bearer " + mAccessToken);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    public static void getPlaylistFromId(Context context, String playlistID, final VolleyResponseListener listener) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String userId = SpotifyPreferences.with(context).getUserID();
        String mAccesstoken = SpotifyPreferences.with(context).getUserToken();
        String url = "https://api.spotify.com/v1/playlists/" + playlistID + "/tracks?market=US&limit=30";

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
                params.put("Authorization", "Bearer " + mAccesstoken);
                return params;
            }
        };

        queue.add(jsonObjectRequest);

    }
}
