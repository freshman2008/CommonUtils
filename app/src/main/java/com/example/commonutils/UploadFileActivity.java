package com.example.commonutils;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.okhttputil.OkHttpUtil;
import com.example.okhttputil.listener.UploadFileListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UploadFileActivity extends AppCompatActivity {
    private String filePath1 = null;
    private String filePath2 = null;
    private String filePath3 = null;
    private int flag = 0;

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;

    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        tv1 = findViewById(R.id.file1_tv);
        tv2 = findViewById(R.id.file2_tv);
        tv3 = findViewById(R.id.file3_tv);

        progressBar1 = findViewById(R.id.progress_1);
        progressBar2 = findViewById(R.id.progress_2);
        progressBar3 = findViewById(R.id.progress_3);
    }

    public void selectFile1(View view) {
        flag = 1;
        GetFilePathUtil.getFilePath(this);
    }

    public void selectFile2(View view) {
        flag = 2;
        GetFilePathUtil.getFilePath(this);
    }

    public void selectFile3(View view) {
        flag = 3;
        GetFilePathUtil.getFilePath(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        String path = GetFilePathUtil.onActivityResult(requestCode, 1, data);
        Log.v("hello", "selecct file path: " + path);
        if (flag == 1) {
            filePath1 = path;
            tv1.setText(filePath1);
        } else if (flag == 2) {
            filePath2 = path;
            tv2.setText(filePath2);
        } else if (flag == 3) {
            filePath3 = path;
            tv3.setText(filePath3);
        }
    }

    public void upload(View view) {
        String url = "http://192.168.8.75:8888/upload";
        File file = new File(filePath1);
        File file1 = new File(filePath2);
        File file2 = new File(filePath3);
        List<File> fileList = new ArrayList<>();
        fileList.add(file);
        fileList.add(file1);
        fileList.add(file2);
        for (File f : fileList) {
            /*OkHttpUtil
                    .upload()
                    .url(url)
                    .file(f)
                    .listener(new UploadFileListener() {
                        @Override
                        public void onSuccess(String response) {
                            Log.v("hello", "upload onSuccess: " + response);
                        }

                        @Override
                        public void onProgress(long totalBytes, long remainingBytes, boolean done) {
                            long progress = (totalBytes - remainingBytes) * 100 / totalBytes;
                            Log.v("hello", "upload progress: " + progress + "%");
                            if (f.equals(fileList.get(0))) {
                                progressBar1.setProgress((int)progress);
                            } else if (f.equals(fileList.get(1))) {
                                progressBar2.setProgress((int)progress);
                            } else if (f.equals(fileList.get(2))) {
                                progressBar3.setProgress((int)progress);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.v("hello", "upload onFailure: " + e.getMessage());
                        }
                    })
                    .build()
                    .execute();*/
        }

//        TestUtil.getInstance().uploadFile(url, file, file1, file2);

        try {
            OkHttpUtil
                    .upload()
                    .url(url)
                    .file(file)
                    .file(file1)
                    .file(file2)
                    .execute(new UploadFileListener() {
                        @Override
                        public void onSuccess(String response) {
                            Log.v("hello", "upload onSuccess: " + response);
                        }

                        @Override
                        public void onProgress(String filename, long totalBytes, long remainingBytes, boolean done) {
                            long progress = (totalBytes - remainingBytes) * 100 / totalBytes;
                            Log.v("hello", filename + "-> upload progress: " + progress + "%");
                            progressBar1.setProgress((int)progress);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.v("hello", "upload onFailure: " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
