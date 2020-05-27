package com.example.okhttputil.request;

import com.example.okhttputil.listener.DownloadFileListener;

import java.io.File;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadManager {
    private static final int MAX_THREAD = 2;
    private static DownloadManager manager = null;
    private ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(MAX_THREAD, MAX_THREAD, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
        private AtomicInteger id = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "download thread #" + id.getAndIncrement());
            return thread;
        }
    });

    private DownloadManager() {}

    public static DownloadManager getInstance() {
        if (manager == null) {
            synchronized (DownloadManager.class) {
                if (manager == null) {
                    manager = new DownloadManager();
                }
            }
        }
        return manager;
    }

    public void download(String url, File file, long contentLength, DownloadFileListener downloadFileListener) {

        long threadDownloadLength = contentLength / 2;//TODO:不是很准确，长度如果不是2的整数倍，需需要考虑榆树
        for (int i = 0; i<MAX_THREAD; i++) {
            long start = i*threadDownloadLength;
            long end = 0;
            if (i == MAX_THREAD-1) {
                end = contentLength-1;
            } else {
                end = (i+1)*threadDownloadLength - 1;
            }
            mThreadPool.execute(new DownloadRunnable(start, end, url, file, downloadFileListener));
        }
    }

}
