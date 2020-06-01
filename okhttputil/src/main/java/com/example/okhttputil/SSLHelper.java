package com.example.okhttputil;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

import okhttp3.OkHttpClient;

public class SSLHelper {
    private static final String TAG = "SSLHelper";

    // 客户端证书密码
    private final static String CLIENT_CERT_PASSWORD = "123456";

    // CA证书文件字符串
    private static final String CACERT =
            "-----BEGIN CERTIFICATE-----\n" +
            "MIIEBTCCAu2gAwIBAgIUC3oRFF3DcXkXPZQ9EMo4pTRnyjEwDQYJKoZIhvcNAQEL\n" +
            "BQAwgZExCzAJBgNVBAYTAkNOMRAwDgYDVQQIDAdCZWlqaW5nMRAwDgYDVQQHDAdC\n" +
            "ZWlqaW5nMREwDwYDVQQKDAhteXJvb3RjYTETMBEGA1UECwwKbXlyb290Y2FvdTET\n" +
            "MBEGA1UEAwwKTXkgUm9vdCBDQTEhMB8GCSqGSIb3DQEJARYSbXlyb290Y2FAZ21h\n" +
            "aWwuY29tMB4XDTIwMDUyODE2MDEyOVoXDTIxMDUyODE2MDEyOVowgZExCzAJBgNV\n" +
            "BAYTAkNOMRAwDgYDVQQIDAdCZWlqaW5nMRAwDgYDVQQHDAdCZWlqaW5nMREwDwYD\n" +
            "VQQKDAhteXJvb3RjYTETMBEGA1UECwwKbXlyb290Y2FvdTETMBEGA1UEAwwKTXkg\n" +
            "Um9vdCBDQTEhMB8GCSqGSIb3DQEJARYSbXlyb290Y2FAZ21haWwuY29tMIIBIjAN\n" +
            "BgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvZd58QonHjzGFfyJMX1WOPq2Ezz5\n" +
            "QWFx7NfbQLPrnn3YD9QVJU3YON8jVfLcfBS8DpDWYqEVsvZ4csanX8cmeMyerMM2\n" +
            "R+9IfNYzA9bbUfKTB6ZxrOt13LKGxMqX3p3kWNsGa2dbNud2eBIPpGGdoJpoavGl\n" +
            "NQGX8ylSoKE9Mdl1LRqSQWkKJIujx28oxC0I9x+Qd2CR9sCzAdE7FO5RvQ3Nb5P+\n" +
            "l8ytmvsP1iIfRbnhmSPvAPEaA5/0gDCRqvZfj5xF181DAePZysAGmza0h0xwuHd4\n" +
            "2hoGC2fRrQ2ZZbjUelOybh6W4thpB4OZys8Df5KHGVXRTpIGOb9hpKw+NwIDAQAB\n" +
            "o1MwUTAdBgNVHQ4EFgQUpDBja6zuvANoqG95Zs4IImJ0WKYwHwYDVR0jBBgwFoAU\n" +
            "pDBja6zuvANoqG95Zs4IImJ0WKYwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0B\n" +
            "AQsFAAOCAQEAbKqG7y5zTWYgggsZFQKfHPx5eeuTdYXzRd1aqZXJ4iQmxntya4M3\n" +
            "fbMw6+yorRC2iGmKQruLABO6hoL4bdfPAKNidv7dW920BnPq0APE2Q1Rwz6AGYpO\n" +
            "4miyjUevabwFb4VItCJmqgI0DWYW3JybeoCTy4x1V0GwoTVnNJsNTHFkl+OuYGKx\n" +
            "3hScQ966Dl9Dl54I/+V/4xPHbzsbQXOHKhP36/nOjLxkstglCbU/lVtTne2e7i4u\n" +
            "/JkYnExDXMaDdcrDMGlWgp5I75ttbYCT305YYe/9IzJtiI37g93CdJlnhuaMkLF+\n" +
            "Wurw4xMRGk+KhoBaxDbwOq1vhVKqV1X1ZA==\n" +
            "-----END CERTIFICATE-----";

    /**
     * 获取这个SSLSocketFactory
     */
    public static SSLSocketFactory getSSLSocketFactory(Context context) {
        try {
            //初始化信任库
            /**
             * CA文件(用于验证服务器端证书)的提供方式有三种
             *  1.证书文件字符串
             *  2.crt证书文件
             *  3.bks文件
             *
             * 方式一
             */
//            InputStream is = new ByteArrayInputStream(CACERT.getBytes(Charset.forName("UTF-8")));
//            KeyStore trustKeyStore = setCertificates(is);

            /**
             * 方式二
             */
            InputStream is = context.getAssets().open("ca.crt");
            KeyStore trustKeyStore = setCertificates(is);

            /**
             * 方式三
             * Android平台CA证书必须是bks格式
             * crt转换bks格式命令：keytool -importcert -v -trustcacerts -alias "keystoreAlias" -file ca.crt -keystore cakeystore.bks -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "C:\Users\lixiulia\Downloads\bcprov-ext-jdk15on-159.jar" -storepass "123456"
             */
//            InputStream isCA = context.getAssets().open("cakeystore.bks");
//            KeyStore trustKeyStore = getKeyStore(isCA, "123456", "BKS");

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustKeyStore);


            //初始化密钥库
            /**
             * 客户端证书一般是提供p12格式文件
             * 如果提供的是客户端证书crt文件与key文件的话，需要进行转换：
             * $openssl pkcs12 -export -out client.p12 -inkey client.key -in client.crt
             */
            InputStream inputStream = context.getAssets().open("myclient.p12");
            KeyStore keyStore = getKeyStore(inputStream, CLIENT_CERT_PASSWORD, "PKCS12");
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()/*"SunX509"*/);
            keyManagerFactory.init(keyStore, CLIENT_CERT_PASSWORD.toCharArray()/*null*/);

            //初始化SSL上下文
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param inputStream
     * @param password
     * @param type Java平台默认识别jks格式的证书文件，但是android平台只识别bks格式的证书文件
     * @return
     * @throws Exception
     */
    private static KeyStore getKeyStore(InputStream inputStream, String password, String type) throws Exception {
        KeyStore keystore = KeyStore.getInstance(type);
        keystore.load(inputStream, password.toCharArray());
        return keystore;
    }

    /**
     * 把CA证书设置进去，生成KeyStore
     *
     * @param certificates CA证书文件
     * @return
     */
    public static KeyStore setCertificates(InputStream... certificates) {
        try  {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());//Android平台默认是"BKS"
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                if (certificate != null) {
                    certificate.close();
                }
            }
            return keyStore;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 信任所有证书
     *
     * @return
     */
    public static class UnSafeTrustManager implements X509TrustManager {
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
            Log.v("hello", "getAcceptedIssuers()");
            return new X509Certificate[]{};
        }
    }

    /**
     * 不进行域名验证
     *
     * @return
     */
    public static class  UnSafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            Log.v("hello", "UnSafeHostnameVerifier -> verify -> hostname: " + hostname);
            return true;
        }
    }

    public static HostnameVerifier getHostnameVerifier() {
        return new UnSafeHostnameVerifier();
        /*HostnameVerifier hostnameVerifier = new HostnameVerifier() {
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
        return hostnameVerifier;*/
    }
}
