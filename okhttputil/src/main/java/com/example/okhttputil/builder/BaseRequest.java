package com.example.okhttputil.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Request;

public abstract class BaseRequest<T extends BaseRequest>{
    public String url;
    public Request request;
    public Map<String, String> headers;
    public Map<String, String> params; //表单参数

    public T url(String url) {
        this.url = url;
        return (T) this;
    }


    public T addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return (T) this;
    }

    public T addParam(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return (T) this;
    }

    public T build() {
        Request.Builder requestBuilder =  new Request.Builder();
        request = requestBuilder
                .get()
                .url(url)
                .build();
        return (T) this;
    }
}
