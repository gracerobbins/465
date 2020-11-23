package com.example.godiegogo.utils;

// Source: https://stackoverflow.com/questions/33535435/how-to-create-a-proper-volley-listener-for-cross-class-volley-method-calling

public interface VolleyResponseListener {

    void onError(String message);

    void onResponse(Object response);
}


