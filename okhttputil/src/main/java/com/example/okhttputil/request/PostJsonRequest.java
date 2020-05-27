package com.example.okhttputil.request;

import android.util.Log;

import com.example.okhttputil.OkHttpUtil;
import com.example.okhttputil.builder.BaseBuilder;
import com.example.okhttputil.listener.RequestListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostJsonRequest extends BaseRequest<PostJsonRequest, PostJsonRequest.Builder, RequestListener> {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String content;

    public PostJsonRequest(PostJsonRequest.Builder builder) {
        super(builder);
        this.content = builder.content;
    }

    @Override
    public void execute() {
        RequestBody formBody = RequestBody.create(JSON, content);
        request = new Request.Builder()
                .post(formBody)
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

    public static class Builder extends BaseBuilder<Builder, RequestListener> {
        private String content;

        public Builder() {
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public PostJsonRequest build() {
            if (url == null) throw new IllegalStateException("url == null");
            return new PostJsonRequest(this);
        }
    }
}
