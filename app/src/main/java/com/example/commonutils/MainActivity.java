package com.example.commonutils;

import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.okhttputil.DownloadFileListener;
import com.example.okhttputil.OkHttpUtil;
import com.example.okhttputil.RequestListener;
import com.example.okhttputil.TestUtil;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void get(View view) {
        String url = "https://api.apiopen.top/getJoke?page=1&count=2&type=video";
//        try {
////            String resp = OkHttpUtil.getInstance().get(url);
////            Log.v("TAG", "resp:" + resp);
//            OkHttpUtil.getInstance().getAsync(url);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        OkHttpUtil
                .with(this)
                .type(1)
                .url(url)
                .build()
                .execute(new RequestListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.v("TAG", "resp:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.v("TAG", "response:" + response);
                    }
                });
    }

    public void download(View view) {
        Log.v("hello", "download");
        String url = "https://vdept.bdstatic.com/7748417a65596441716c764e37396972/703547424b584a71/0551caaddab8727b69c40faf1a1e2f6bff13f8a5141f07a691242102ee12b8b59bcbfa0184a1cbda0ab682759c438cd3.mp4?auth_key=1588059651-0-0-9eafa02da35aea9d71099821be46a8af";

        String destPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filename = "hello.mp4";
        File file = new File(destPath, filename);

        OkHttpUtil
                .with(this)
                .type(4)
                .url(url)
                .path(destPath)
                .filename(filename)
                .build()
                .execute(new DownloadFileListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        Log.v("hello", "file downloaded : " + file.getAbsolutePath());
                    }

                    @Override
                    public void onDownloading(int progress) {
                        Log.v("hello", "download progress: "+ progress);
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        Log.v("hello", "file download failed.");
                    }
                });

//        downloadFile();
    }

    private void downloadFile() {
//        String url = "https://vdept.bdstatic.com/7748417a65596441716c764e37396972/703547424b584a71/0551caaddab8727b69c40faf1a1e2f6bff13f8a5141f07a691242102ee12b8b59bcbfa0184a1cbda0ab682759c438cd3.mp4?auth_key=1588059651-0-0-9eafa02da35aea9d71099821be46a8af";
        String url = "https://vdept.bdstatic.com/50714d626a6c6246694d594c316e4671/4555764153774363/60c609e2e06527ed71bbe7ddedf82cc484dd4f249f71fb0ad3e0b4906b2aefd6719253b8b5ed5f74e6f9b8236980249d.mp4?auth_key=1588073184-0-0-a85ccda2ed6b52881c0ea03580558125";

        TestUtil.getInstance().download(url, Environment.getExternalStorageDirectory().getAbsolutePath(), "aaa.mp4",
                new TestUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        Log.v("hello", "file downloaded : " + file.getAbsolutePath());
                    }

                    @Override
                    public void onDownloading(int progress) {
//                        progressDialog.setProgress(progress);
                        Log.v("hello", "download progress: "+ progress);
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        //下载异常进行相关提示操作
                        Log.v("hello", "file download failed.");
                    }
                });
    }

    public void upload(View view) {
        String url = "http://192.168.8.135:8888/upload";
        String destPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filename = "hello.mp4";
        String filename1 = "111.txt";
        String filename2 = "wechat_devtools_1.02.2003250_x64.exe";
        File file = new File(destPath, filename);
        File file1 = new File(destPath, filename1);
        File file2 = new File(destPath, filename2);

//        TestUtil.getInstance().uploadFile(url, file, file1, file2);

        OkHttpUtil
                .with(this)
                .type(5)
                .url(url)
                .upload(file)
                .upload(file1)
                .upload(file2)
                .build()
                .execute(new RequestListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.v("TAG", "resp:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        Log.v("TAG", "response:" + response);
                    }
                });
    }
}
