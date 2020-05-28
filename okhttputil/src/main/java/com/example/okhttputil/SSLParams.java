package com.example.okhttputil;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

import okhttp3.OkHttpClient;

public class SSLParams {
    //获取这个SSLSocketFactory
    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 信任所有证书
     *
     * @return
     */
    public static TrustManager[] getTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        Log.v("hello", "");
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        Log.v("hello", "");
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        return trustAllCerts;// new TrustManager[0];
    }

    /**
     * 不进行域名验证
     *
     * @return
     */
    public static HostnameVerifier getHostnameVerifier() {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                try {
                    String peerHost = session.getPeerHost(); //服务器返回的主机名
                    String str_new = "twd";//验证证书
                    X509Certificate[] peerCertificates = (X509Certificate[]) session.getPeerCertificates();
                    for (X509Certificate certificate : peerCertificates) {
                        X500Principal subjectX500Principal = certificate
                                .getSubjectX500Principal();
                        String name = subjectX500Principal.getName();
                        String[] split = name.split(",");
                        for (String str : split) {
                            if (str.startsWith("CN")) {//证书绑定的域名或者ip
                                if (str.contains(str_new)) {
                                    return true;
                                }
                            }
                        }
                    }
                } catch (SSLPeerUnverifiedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                return true;
            }
        };
        return hostnameVerifier;
    }

    private void hel() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
    }
}
