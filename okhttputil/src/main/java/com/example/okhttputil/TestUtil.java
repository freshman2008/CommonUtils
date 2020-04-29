package com.example.okhttputil;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TestUtil {
    OkHttpClient client = new OkHttpClient();
    private static volatile TestUtil instance;

    private TestUtil() {
    }

    public static TestUtil getInstance() {
        if (instance == null) {
            synchronized (OkHttpUtil.class) {
                if (instance == null) {
                    instance = new TestUtil();
                }
            }
        }
        return instance;
    }

    public String get(String url) throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null) {
            return response.body().string();
        }

        return null;
    }

    public void getAsync(String url) throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("TAG", "resp:" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.v("TAG", "resp:" + resp);
            }
        });
    }

    /**
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称，后面记得拼接后缀，否则手机没法识别文件类型
     * @param listener     下载监听
     */

    public void download(final String url, final String destFileDir, final String destFileName, final OnDownloadListener listener) {
        Request request = new Request.Builder()
                .url(url)
                .build();
//        try {
//            Response response = client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //异步请求
        client.newCall(request).enqueue(new Callback() {
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
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);

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

    public interface OnDownloadListener{

        /**
         * 下载成功之后的文件
         */
        void onDownloadSuccess(File file);

        /**
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载异常信息
         */

        void onDownloadFailed(Exception e);
    }


    public void uploadFile(String url, File file, File file1, File file2) {
        MultipartBody.Builder multiBuilder=new MultipartBody.Builder();

        RequestBody filebody = MultipartBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody filebody1 = MultipartBody.create(MediaType.parse("multipart/form-data"), file1);
        RequestBody filebody2 = MultipartBody.create(MediaType.parse("multipart/form-data"), file2);


        //参数以添加header方式将参数封装，否则上传参数为空
        // 设置请求体
//        multiBuilder.setType(MultipartBody.FORM);
//这里是 封装上传图片参数
        multiBuilder.addFormDataPart("file", file.getName(), filebody);
        multiBuilder.addFormDataPart("file", file1.getName(), filebody1);
        multiBuilder.addFormDataPart("file", file2.getName(), filebody2);
        // 封装请求参数,这里最重要
        HashMap<String, String> params = new HashMap<>();
        params.put("client","Android");
        params.put("uid","1061");
        params.put("token","1911173227afe098143caf4d315a436d");
        params.put("uuid","A000005566DA77");
//        参数以添加header方式将参数封装，否则上传参数为空
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                multiBuilder.addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
            }
        }
        RequestBody multiBody=multiBuilder.build();
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new   Request.Builder().url(url).post(multiBody).build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求失败的处理
                Log.v("TAG", "resp:" + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                Log.v("TAG", "resp:" + resp);
            }
        });
    }
}
