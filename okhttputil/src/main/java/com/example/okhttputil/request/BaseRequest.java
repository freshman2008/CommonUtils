package com.example.okhttputil.request;

import com.example.okhttputil.OkHttpUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class BaseRequest<T> {
    protected String url;
    protected Request request;
    protected Map<String,String> params = new HashMap<String,String>(); //表单参数
    protected Map<String,String> headers = new HashMap<String,String>();

    public BaseRequest() {
    }

    public T params(String key, String value) {
        this.params.put(key, value);
        return (T)this;
    }

    public T params(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            this.params.put(key, value);
        }

        return (T)this;
    }

    public T headers(String key, String value) {
        this.headers.put(key, value);
        return (T)this;
    }

    protected RequestBody createRequestBody() {
        return null;
    }

    public Request.Builder getRequestBuilder() {
        Request.Builder builder = new Request.Builder();
        addHeaders(builder);

        return builder;
    }

    private void addHeaders(Request.Builder builder) {
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.addHeader(key, value);
        }
    }

    public abstract okhttp3.Request newRequest();

    public void execute(final Callback callback) {
        request = newRequest();
        Call call = OkHttpUtil.getInstance().getClient().newCall(request);
        call.enqueue(callback);
    }
}
