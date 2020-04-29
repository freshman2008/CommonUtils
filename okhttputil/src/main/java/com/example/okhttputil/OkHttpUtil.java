package com.example.okhttputil;

import com.example.okhttputil.builder.DefaultBuilder;
import com.example.okhttputil.builder.DownloadFileBuilder;
import com.example.okhttputil.builder.GetBuilder;
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

    public OkHttpClient getClient() {
        return client;
    }


    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static DownloadFileBuilder download() {
        return new DownloadFileBuilder();
    }

    public static UploadFileBuilder upload() {
        return new UploadFileBuilder();
    }
}
