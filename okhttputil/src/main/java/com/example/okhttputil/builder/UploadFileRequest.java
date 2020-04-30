package com.example.okhttputil.builder;

import android.util.Log;

import com.example.okhttputil.OkHttpUtil;
import com.example.okhttputil.listener.UploadFileListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class UploadFileRequest extends BaseRequest<UploadFileRequest> {
    private List<File> uploadFiles = new ArrayList<>();
    private UploadFileListener listener;

    public UploadFileRequest file(File file) {
        if (file != null && file.exists()) {
            this.uploadFiles.add(file);
        }
        return this;
    }

    public UploadFileRequest listener(UploadFileListener listener) {
        this.listener = listener;
        return this;
    }

    public UploadFileRequest build() {
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
        return this;
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

    public void execute() {
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
}
