package com.example.okhttputil.builder;

import com.example.okhttputil.listener.RequestListener;
import com.example.okhttputil.request.BaseRequest;

public class BaseBuilder<T extends BaseBuilder, R extends BaseRequest> {
    public RequestListener listener;
    public String url;

    public BaseBuilder() {
    }

    public BaseBuilder(R request) {
        this.url = request.url;
        this.listener = request.listener;
    }

    public T url(String url) {
        if (url == null) throw new IllegalArgumentException("url == null");
        this.url = url;
        return (T)this;
    }

    public T listener(RequestListener listener) {
        this.listener = listener;
        return (T)this;
    }

    public R build() {
        return null;
    }
}
