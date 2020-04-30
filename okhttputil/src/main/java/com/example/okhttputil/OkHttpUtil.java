package com.example.okhttputil;

import com.example.okhttputil.builder.DownloadFileRequest;
import com.example.okhttputil.builder.GetRequest;
import com.example.okhttputil.builder.UploadFileRequest;

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

    public OkHttpClient getClient() {
        return client;
    }


    public static GetRequest get() {
        return new GetRequest();
    }

    public static DownloadFileRequest download() {
        return new DownloadFileRequest();
    }

    public static UploadFileRequest upload() {
        return new UploadFileRequest();
    }
}
