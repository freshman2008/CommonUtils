package com.example.okhttputil;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    OkHttpClient getClient() {
        return client;
    }



    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static final class Builder {
        private Context context;
        private String url;
        private int requestType;
        private Request.Builder builder = new Request.Builder();
        private Request request;
        private Map<String, String> headers;
        private Map<String, String> params; //表单参数
        private String content;//json string
        private String downloadFilePath;
        private String downloadFileName;
        private List<File> uploadFile = new ArrayList<>();

        public Builder(Context context) {
            this.context = context;
        }

        private Builder() {
        }

        public Builder type(int type) {
            this.requestType = type;
            return this;
        }

        public Builder content(String data) {
            this.content = data;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder path(String path) {
            this.downloadFilePath = path;
            return this;
        }

        public Builder upload(File file) {
            this.uploadFile.add(file);
            return this;
        }

        public Builder filename(String filename) {
            this.downloadFileName = filename;
            return this;
        }

        public Builder addHeader(String key, String val) {
            if (this.headers == null) {
                headers = new LinkedHashMap<>();
            }
            headers.put(key, val);
            return this;
        }

        public Builder addParam(String key, String val) {
            if (this.params == null) {
                params = new LinkedHashMap<>();
            }
            params.put(key, val);
            return this;
        }

        public Builder build() {
            Request.Builder requestBuilder =  new Request.Builder();
            if (requestType == 1) {//get
                request = requestBuilder
                        .get()
                        .url(url)
                        .build();
            } else if (requestType == 2) {//post string -> 表单
                FormBody.Builder formBuilder = new FormBody.Builder();
                if (params != null) {
                    for (String key : params.keySet()) {
                        formBuilder.add(key, params.get(key));
                    }
                }
                RequestBody body = formBuilder.build();
                request = requestBuilder
                        .url(url)
                        .post(body)
                        .build();
            }  else if (requestType == 3) {//post json string
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, content);
                request = requestBuilder
                        .url(url)
                        .post(body)
                        .build();
            } else if (requestType == 4) {
                request = requestBuilder
                        .get()
                        .url(url)
                        .build();
            } else if (requestType == 5) {
                MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
                for (File file:uploadFile) {
                    multiBuilder.addFormDataPart("file", file.getName(), MultipartBody.create(MediaType.parse("multipart/form-data"), file));
                }
                RequestBody multiBody = multiBuilder.build();
                request= requestBuilder
                        .url(url)
                        .post(multiBody)
                        .build();
            }

            return this;
        }

        public void execute(final DownloadFileListener listener) {
            Call call = OkHttpUtil.getInstance().getClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // 下载失败监听回调
                    listener.onDownloadFailed(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;

                    //储存下载文件的目录
                    File dir = new File(downloadFilePath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, downloadFileName);

                    try {

                        is = response.body().byteStream();
                        long total = response.body().contentLength();
                        fos = new FileOutputStream(file);
                        long sum = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            sum += len;
                            int progress = (int) (sum * 1.0f / total * 100);
                            //下载中更新进度条
                            listener.onDownloading(progress);
                        }
                        fos.flush();
                        //下载完成
                        listener.onDownloadSuccess(file);
                    } catch (Exception e) {
                        listener.onDownloadFailed(e);
                    } finally {

                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                        }
                    }
                }
            });
        }

        public void execute(final RequestListener listener) {
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

        public void execute() {
            execute((RequestListener) null);
        }
    }


}
