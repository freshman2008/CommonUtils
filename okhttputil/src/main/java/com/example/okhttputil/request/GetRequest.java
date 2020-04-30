package com.example.okhttputil;

import android.util.Log;

import com.example.okhttputil.listener.RequestListener;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class HelloRequest {
    private String  url;
    public Request request;
    public Map<String, String> headers;
    public Map<String, String> params; //表单参数
    public RequestListener listener;

    private HelloRequest(Builder builder) {
        this.url = builder.url;
        this.listener = builder.listener;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public void execute() {
        Request.Builder requestBuilder =  new Request.Builder();
        request = requestBuilder
                .get()
                .url(url)
                .build();

        Call call = OkHttpUtil.getInstance().getClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.onFailure(e);
                }
                Log.v("TAG", "resp:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.v("TAG", "resp:" + resp);
                if (listener != null) {
                    listener.onResponse(resp);
                }
            }
        });
    }

    public static class Builder {
        public RequestListener listener;
        private String url;

        public Builder() {
        }

        private Builder(HelloRequest request) {
            this.url = request.url;
            this.listener = request.listener;
        }

        public Builder url(String url) {
            if (url == null) throw new IllegalArgumentException("url == null");
            this.url = url;
            return this;
        }

        public Builder listener(RequestListener listener) {
            this.listener = listener;
            return this;
        }

        public HelloRequest build() {
            if (url == null) throw new IllegalStateException("url == null");
            return new HelloRequest(this);
        }
    }
}
