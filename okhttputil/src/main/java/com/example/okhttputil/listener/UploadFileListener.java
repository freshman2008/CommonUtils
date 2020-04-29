package com.example.okhttputil.listener;

import java.io.File;

public interface UploadFileListener {
    /**
     * 上传成功
     */
    void onSuccess(String response);

    /**
     * 上传进度
     */
    void onProgress(long totalBytes, long remainingBytes, boolean done);

    /**
     * 上传异常信息
     */

    void onFailure(Exception e);
}
