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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AppleMusicUtils {

    // Construct a JSON structure for an empty Apple Music playlist
    public static JSONObject makeEmptyJSONPlaylist(String name, String description) throws JSONException, IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException();
        }

        JSONObject playlist = new JSONObject();
        JSONObject attributes = new JSONObject();
        JSONObject relationships = new JSONObject();

        attributes.put("name", name);
        if (description != null && !description.isEmpty()) {
            attributes.put("description", description);
        }

        playlist.put("attributes", attributes);
        playlist.put("relationships", relationships);

        return playlist;
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
