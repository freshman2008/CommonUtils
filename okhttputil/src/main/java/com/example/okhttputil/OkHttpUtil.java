package com.example.okhttputil;

import com.example.okhttputil.request.DownloadFileRequest;
import com.example.okhttputil.request.GetRequest;
import com.example.okhttputil.request.PostFormRequest;
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
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return builder
                .sslSocketFactory(SSLParams.getSSLSocketFactory())
                .hostnameVerifier(SSLParams.getHostnameVerifier())
                .build();
//        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
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

    public static PostJsonRequest postJson() {
        return new PostJsonRequest();
    }

    public static PostFormRequest postForm() {
        return new PostFormRequest();
    }
}
