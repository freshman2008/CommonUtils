package com.example.okhttputil.listener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 外部回调处理器
 **/
public interface RequestListener {
    public void onFailure(Exception e);
    public void onResponse(Response response);
}
