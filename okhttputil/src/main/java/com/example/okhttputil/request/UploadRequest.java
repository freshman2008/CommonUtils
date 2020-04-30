package com.example.okhttputil;

import android.util.Log;

import com.example.okhttputil.builder.UploadFileBuilder;
import com.example.okhttputil.listener.DownloadFileListener;
import com.example.okhttputil.listener.UploadFileListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class UploadRequest {
    private String  url;
    public Request request;
    public Map<String, String> headers;
    public Map<String, String> params; //表单参数
    public UploadFileListener listener;
    private List<File> uploadFiles = new ArrayList<>();

    private UploadRequest(Builder builder) {
        this.url = builder.url;
        this.listener = builder.listener;
        this.uploadFiles = builder.uploadFiles;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public void execute() {
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
        for (File file : uploadFiles) {
//                    multiBuilder.addFormDataPart("file", file.getName(), MultipartBody.create(MediaType.parse("multipart/form-data"), file));
            RequestBody requestBody = createCustomRequestBody(MultipartBody.FORM, file, this.listener);
            multiBuilder.addFormDataPart("file", file.getName(), requestBody);
        }
        RequestBody multiBody = multiBuilder.build();

        Request.Builder requestBuilder =  new Request.Builder();
        request= requestBuilder
                .url(url)
                .post(multiBody)
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
                    listener.onSuccess(resp);
                }
            }
        });
    }

    public RequestBody createCustomRequestBody(final MediaType contentType, final File file, final UploadFileListener listener) {
        final int SEGMENT_SIZE = 2048;
        return new RequestBody() {
            @Override public MediaType contentType() {
                Log.v("hello", "contentType:" + contentType);
                return contentType;
            }

            @Override public long contentLength() {
                Log.v("hello", "file length:" + file.length());
                return file.length();
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                Log.v("hello", "writeTo");
                Source source;
                try {
                    source = Okio.source(file);
                    //sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long total = contentLength();
                    Long remaining = total;
                    for (long readCount; (readCount = source.read(buf, SEGMENT_SIZE)) != -1; ) {
                        sink.write(buf, readCount);
                        if (listener != null) {
                            listener.onProgress(total, remaining -= readCount, remaining == 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static class Builder {
        private String url;
        private List<File> uploadFiles = new ArrayList<>();
        private UploadFileListener listener;

        public Builder() {
        }

        private Builder(UploadRequest request) {
            this.url = request.url;
            this.listener = request.listener;
        }

        public Builder url(String url) {
            if (url == null) throw new IllegalArgumentException("url == null");
            this.url = url;
            return this;
        }

        public Builder listener(UploadFileListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder file(File file) {
            if (file != null && file.exists()) {
                this.uploadFiles.add(file);
            }
            return this;
        }

        public UploadRequest build() {
            if (url == null) throw new IllegalStateException("url == null");
            return new UploadRequest(this);
        }
    }
}
