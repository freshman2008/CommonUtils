package com.example.okhttputil.request;

import android.util.Log;

import com.example.okhttputil.listener.DownloadFileListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadFileRequest extends BaseRequest<DownloadFileRequest> {
    protected File file;
    private long start=0;
    @Override
    public Request newRequest() {
        if (file!=null && file.exists()) {
            start = file.length();
        }
        return getRequestBuilder()
                .get()
                .addHeader("Range", "bytes=" + start + "-")
                .url(url)
                .build();
    }

    public DownloadFileRequest url(String url) {
        this.url = url;
        return this;
    }

    public DownloadFileRequest file(File file) {
        this.file = file;
        return this;
    }

    public void execute(final DownloadFileListener listener) {
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
//                String resp = response.body().string();
//                Log.v("TAG", "resp:" + resp);
                if (listener != null) {
//                    listener.onResponse(resp);
                    saveFile(file, response.body().contentLength(), response.body().byteStream(), listener);
                }
            }
        });
    }

    private void saveFile(File file, long contentLength, InputStream is, DownloadFileListener listener) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(start);

            int len;
            long sum = start;
            byte[] buf  = new byte[1024/*1024*500*/];
            while ((len = is.read(buf)) != -1) {
                randomAccessFile.write(buf, 0, len);
                sum += len;
                int progress = (int) (sum * 1.0f / contentLength * 100);
                //下载中更新进度条
                listener.onProgress(progress);
            }
            listener.onSuccess(file);
            randomAccessFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        //储存下载文件的目录
//        if(this.file == null) {
//            String destPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//            File file = new File(destPath, fileName);
//        }
    }
}
/*
public class DownloadFileRequest extends BaseRequest<DownloadFileRequest, DownloadFileRequest.Builder, DownloadFileListener> {
    private File file;

    public DownloadFileRequest(DownloadFileRequest.Builder builder) {
        super(builder);
        this.file = builder.file;
    }

    @Override
    public void execute() {
        final Request.Builder requestBuilder = new Request.Builder();
        request = requestBuilder
                .get()
                .url(url)
                .build();
        Call call = OkHttpUtil.getInstance().getClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败监听回调
                listener.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                saveFile(response);
                if (!response.isSuccessful() && listener != null) {
                    listener.onFailure(new Exception("failed."));
                    return;
                }
                long contentLength = response.body().contentLength();
                InputStream inputStream = response.body().byteStream();
                byte[] buf = new byte[2048];
                int read = inputStream.read(buf);
                if (contentLength == -1) {
                    listener.onFailure(new Exception("failed."));
                    return;
                }
                DownloadManager.getInstance().download(url, file, contentLength, listener);
            }
        });
    }

    private void saveFile(Response response) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;

//                //储存下载文件的目录
//                File dir = new File(downloadFilePath);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//                File file = new File(dir, downloadFileName);

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
                listener.onProgress(progress);
            }
            fos.flush();
            //下载完成
            listener.onSuccess(file);
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

    public static class Builder extends BaseBuilder<Builder, DownloadFileListener> {
        private File file;

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public Builder() {
        }

        public DownloadFileRequest build() {
            if (url == null) throw new IllegalStateException("url == null");
            return new DownloadFileRequest(this);
        }
    }
}*/
