package com.example.okhttputil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public interface RequestListener {
    public void onFailure(Exception e);
    public void onResponse(String response);
}
