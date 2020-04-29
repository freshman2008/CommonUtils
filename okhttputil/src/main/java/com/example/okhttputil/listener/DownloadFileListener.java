package com.example.okhttputil.listener;

import java.io.File;

public interface DownloadFileListener {
    /**
     * 下载成功之后的文件
     */
    void onSuccess(File file);

    /**
     * 下载进度
     */
    void onProgress(int progress);

    /**
     * 下载异常信息
     */

    void onFailure(Exception e);
}
