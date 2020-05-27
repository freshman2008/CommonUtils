package com.example.okhttputil.builder;

public abstract class BaseBuilder<T extends BaseBuilder, R> {
    public String url;
    public R listener;

    public T url(String url) {
        this.url = url;
        return (T) this;
    }

    public T listener(R listener) {
        this.listener = listener;
        return (T) this;
    }
}
