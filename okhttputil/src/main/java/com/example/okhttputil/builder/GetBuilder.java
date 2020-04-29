package com.example.okhttputil.builder;

import android.util.Log;

import com.example.okhttputil.OkHttpUtil;
import com.example.okhttputil.listener.RequestListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GetBuilder extends DefaultBuilder<GetBuilder> {

    public void execute(final RequestListener listener) {
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

    public void execute() {
        execute((RequestListener) null);
    }
}
