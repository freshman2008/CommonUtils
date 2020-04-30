package com.example.okhttputil;

import com.example.okhttputil.builder.DownloadFileBuilder;
import com.example.okhttputil.builder.UploadFileBuilder;

import okhttp3.OkHttpClient;

public class OkHttpUtil {
    OkHttpClient client = new OkHttpClient();
    private static volatile OkHttpUtil instance;

    private OkHttpUtil() {
    }

    public static OkHttpUtil getInstance() {
        if (instance == null) {
            synchronized (OkHttpUtil.class) {
                if (instance == null) {
                    instance = new OkHttpUtil();
                }
            }
        }
        return instance;
    }

    public static OkHttpUtil initClient(OkHttpClient client) {
        getInstance().setClient(client);
        return getInstance();
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public static HelloRequest.Builder get() {
//        return new GetRequest();
        return new HelloRequest.Builder();
    }

    public static DownloadRequest.Builder download() {
//        return new DownloadFileBuilder();
        return new DownloadRequest.Builder();
    }

    public static UploadRequest.Builder upload() {
//        return new UploadFileBuilder();
        return new UploadRequest.Builder();
    }
}
