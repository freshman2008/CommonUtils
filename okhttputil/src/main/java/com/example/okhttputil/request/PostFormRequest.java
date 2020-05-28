package com.example.okhttputil.request;

import android.util.Log;

import com.example.okhttputil.listener.RequestListener;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostFormRequest extends BaseRequest<PostFormRequest> {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public PostFormRequest() {
    }

    /**
     * //        HashMap<String,String> paramsMap=new HashMap<>();
     * //        paramsMap.put("name","哈哈");
     * //        paramsMap.put("client","Android");
     * //        paramsMap.put("id","3243598");
     * @return
     */
    @Override
    protected RequestBody createRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : this.params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.add(key, value); //追加表单信息
        }
        RequestBody formBody = builder.build();
        return formBody;
    }

    @Override
    public Request newRequest() {
        return getRequestBuilder()
                .post(createRequestBody())
                .url(url)
                .build();
    }

    public PostFormRequest url(String url) {
        this.url = url;
        return this;
    }


    public void execute(final RequestListener listener) {
        execute(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.onFailure(e);
                }
                Log.v("TAG", "resp:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.v("TAG", "resp:" + resp);
                if (listener != null) {
                    listener.onResponse(response);
                }
            }
        });
    }
}
/*public class PostFormRequest extends BaseRequest<PostFormRequest, PostFormRequest.Builder, RequestListener> {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private HashMap<String, String> content;

    public PostFormRequest(PostFormRequest.Builder builder) {
        super(builder);
        this.content = builder.content;
    }

    @Override
    public void execute() {
        FormBody.Builder builder = new FormBody.Builder();
//        HashMap<String,String> paramsMap=new HashMap<>();
//        paramsMap.put("name","哈哈");
//        paramsMap.put("client","Android");
//        paramsMap.put("id","3243598");
        for (String key : content.keySet()) {
            builder.add(key, content.get(key));//追加表单信息
        }
        RequestBody formBody  = builder.build();
        request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();
        Call call = OkHttpUtil.getInstance().getClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.onFailure(e);
                }
                Log.v("TAG", "resp:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.v("TAG", "resp:" + resp);
                if (listener != null) {
                    listener.onResponse(resp);
                }
            }
        });
    }

    public static class Builder extends BaseBuilder<Builder, RequestListener> {
        private HashMap<String, String> content = new HashMap<>();

        public Builder() {
        }

        public Builder with(HashMap<String, String> content) {
            this.content = content;
            return this;
        }

        public PostFormRequest build() {
            if (url == null) throw new IllegalStateException("url == null");
            return new PostFormRequest(this);
        }
    }
}*/
