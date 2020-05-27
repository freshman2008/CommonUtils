package com.example.okhttputil;

import com.example.okhttputil.request.DownloadFileRequest;
import com.example.okhttputil.request.GetRequest;
import com.example.okhttputil.request.PostJsonRequest;
import com.example.okhttputil.request.UploadFileRequest;

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


    public static GetRequest.Builder get() {
        return new GetRequest.Builder();
    }

    public static DownloadFileRequest.Builder download() {
        return new DownloadFileRequest.Builder();
    }

    public static UploadFileRequest.Builder upload() {
        return new UploadFileRequest.Builder();
    }

    public static PostJsonRequest.Builder post() {
        return new PostJsonRequest.Builder();
    }
}
