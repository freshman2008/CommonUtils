package com.example.okhttputil;

import android.util.Log;

import com.example.okhttputil.listener.DownloadFileListener;
import com.example.okhttputil.listener.RequestListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadRequest {
    private String  url;
    public Request request;
    public Map<String, String> headers;
    public Map<String, String> params; //表单参数
    public DownloadFileListener listener;
    private File file;

    private DownloadRequest(Builder builder) {
        this.url = builder.url;
        this.listener = builder.listener;
        this.file = builder.file;
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
                // 下载失败监听回调
                listener.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                try {

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中更新进度条
                        listener.onProgress(progress);
                    }
                    fos.flush();
                    //下载完成
                    listener.onSuccess(file);
                } catch (Exception e) {
                    listener.onFailure(e);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    public static class Builder {
        public DownloadFileListener listener;
        private String url;
        private File file;

        public Builder() {
        }

        private Builder(DownloadRequest request) {
            this.url = request.url;
            this.listener = request.listener;
        }

        public Builder url(String url) {
            if (url == null) throw new IllegalArgumentException("url == null");
            this.url = url;
            return this;
        }

        public Builder listener(DownloadFileListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public DownloadRequest build() {
            if (url == null) throw new IllegalStateException("url == null");
            return new DownloadRequest(this);
        }
    }
}
