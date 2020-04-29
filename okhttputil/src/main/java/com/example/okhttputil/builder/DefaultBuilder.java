package com.example.okhttputil.builder;

import android.util.Log;

import com.example.okhttputil.OkHttpUtil;
import com.example.okhttputil.listener.RequestListener;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public abstract class DefaultBuilder<T extends DefaultBuilder>{
    public String url;
    public Request request;
    public Map<String, String> headers;
    public Map<String, String> params; //表单参数

    public T url(String url) {
        this.url = url;
        return (T) this;
    }


    public T addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return (T) this;
    }

    public T addParam(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return (T) this;
    }

    public T build() {
        Request.Builder requestBuilder =  new Request.Builder();
        request = requestBuilder
                .get()
                .url(url)
                .build();
        return (T) this;
    }
}
