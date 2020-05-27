package com.example.okhttputil.request;

import com.example.okhttputil.OkHttpUtil;
import com.example.okhttputil.listener.DownloadFileListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

class DownloadRunnable implements Runnable {
    private long start;
    private long end;
    private String url;
    private DownloadFileListener downloadFileListener;
    private File file;

    public DownloadRunnable(long start, long end, String url, File file, DownloadFileListener downloadFileListener) {
        this.start = start;
        this.end = end;
        this.url = url;
        this.file = file;
        this.downloadFileListener = downloadFileListener;
    }

    @Override
    public void run() {
        Response response = syncRequestByRange(url, start, end);
        if ((response == null || !response.isSuccessful()) && downloadFileListener != null) {
            downloadFileListener.onFailure(new Exception("download failed."));
            return;
        }

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(start);

            int len;
//            long sum = 0;
            byte[] buf  = new byte[1024*500];
            InputStream is = response.body().byteStream();
            while ((len = is.read(buf)) != -1) {
                randomAccessFile.write(buf, 0, len);
//                sum += len;
//                int progress = (int) (sum * 1.0f / total * 100);
//                //下载中更新进度条
//                listener.onProgress(progress);
            }
            downloadFileListener.onSuccess(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Response syncRequestByRange(String url, long start, long end) {
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder
                .url(url)
                .addHeader("Range", "bytes=" + start + "-" + end)
                .build();
        try {
            return OkHttpUtil.getInstance().getClient().newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
