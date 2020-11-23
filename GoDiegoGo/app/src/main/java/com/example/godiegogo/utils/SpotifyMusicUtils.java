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

public class SpotifyMusicUtils {

    public static void getSpotifyMusicPlaylists(Context context, final VolleyResponseListener listener) throws JSONException, IllegalStateException {
        String userToken = ApplePreferences.with(context).getUserToken();

        if (userToken == null || userToken.isEmpty()) {
            throw new IllegalStateException();
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.spotify.com/v1/users/" + userId + "/playlists?limit=20";

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
//                params.put("Music-User-Token", ApplePreferences.with(context).getUserToken());
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
