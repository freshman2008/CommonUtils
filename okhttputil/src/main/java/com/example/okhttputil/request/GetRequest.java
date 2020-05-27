package com.example.okhttputil.request;

import android.util.Log;

import com.example.okhttputil.OkHttpUtil;
import com.example.okhttputil.builder.BaseBuilder;
import com.example.okhttputil.listener.RequestListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class GetRequest extends BaseRequest<GetRequest, GetRequest.Builder, RequestListener> {

    public GetRequest(GetRequest.Builder builder) {
        super(builder);
    }

    @Override
    public void execute() {
        Request.Builder requestBuilder = new Request.Builder();
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

    public static class Builder extends BaseBuilder<Builder, RequestListener> {
        public Builder() {
        }

        public GetRequest build() {
            if (url == null) throw new IllegalStateException("url == null");
            return new GetRequest(this);
        }
    }
}
