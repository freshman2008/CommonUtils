package com.example.okhttputil.request;

import android.util.Log;

import com.example.okhttputil.listener.RequestListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class GetRequest extends BaseRequest<GetRequest> {

    public GetRequest() {
    }

    @Override
    public Request newRequest() {
        return getRequestBuilder()
                .get()
                .url(url)
                .build();
    }

    public GetRequest url(String url) {
        this.url = url;
        return this;
    }

    public void execute(final RequestListener listener) {
        execute(new Callback() {
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
                    listener.onResponse(response);
                }
            }
        });
    }
}
