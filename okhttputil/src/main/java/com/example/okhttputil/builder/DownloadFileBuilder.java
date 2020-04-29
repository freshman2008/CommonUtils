package com.example.okhttputil.builder;

import com.example.okhttputil.listener.DownloadFileListener;
import com.example.okhttputil.OkHttpUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DownloadFileBuilder extends DefaultBuilder<DownloadFileBuilder>  {
    private File downloadFile;

    public DownloadFileBuilder file(File file) {
        this.downloadFile = file;
        return this;
    }

    public void execute(final DownloadFileListener listener) {
        Call call = OkHttpUtil.getInstance().getClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败监听回调
                listener.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                try {

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(downloadFile);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中更新进度条
                        listener.onProgress(progress);
                    }
                    fos.flush();
                    //下载完成
                    listener.onSuccess(downloadFile);
                } catch (Exception e) {
                    listener.onFailure(e);
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


    public void execute() {
        execute((DownloadFileListener) null);
    }
}
