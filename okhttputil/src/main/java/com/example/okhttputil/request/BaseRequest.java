package com.example.okhttputil.request;

import com.example.okhttputil.builder.BaseBuilder;

import java.util.Map;

import okhttp3.Request;

public abstract class BaseRequest<T extends BaseRequest, B extends BaseBuilder, R> {
    protected String url;
    protected Request request;
    protected Map<String, String> headers;
    protected Map<String, String> params; //表单参数
    protected R listener;

    public BaseRequest() {
    }

    public BaseRequest(B builder) {
        this.url = builder.url;
        this.listener = (R) builder.listener;
    }

    public abstract void execute();
}
