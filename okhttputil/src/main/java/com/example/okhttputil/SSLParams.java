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

public class SSLParams {
    private static final String TAG = "SSLHelper";
//    private final static String CLIENT_PRI_KEY = "client.bks";
//    private final static String TRUSTSTORE_PUB_KEY = "ca.bks";
//    private final static String CLIENT_BKS_PASSWORD = "123456";
//    private final static String TRUSTSTORE_BKS_PASSWORD = "123456";
//    private final static String KEYSTORE_TYPE = "BKS";
//    private final static String PROTOCOL_TYPE = "TLS";
//    private final static String CERTIFICATE_STANDARD = "X509";

    // 客户端证书密码
    private final static String CLIENT_CERT_PASSWORD = "123456";

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

    private static final String CLIENTCERT =
            "-----BEGIN CERTIFICATE-----\n" +
            "MIIDrDCCApQCFGchI8pf3iNXrGM71Yfiw3FOJClgMA0GCSqGSIb3DQEBCwUAMIGR\n" +
            "MQswCQYDVQQGEwJDTjEQMA4GA1UECAwHQmVpamluZzEQMA4GA1UEBwwHQmVpamlu\n" +
            "ZzERMA8GA1UECgwIbXlyb290Y2ExEzARBgNVBAsMCm15cm9vdGNhb3UxEzARBgNV\n" +
            "BAMMCk15IFJvb3QgQ0ExITAfBgkqhkiG9w0BCQEWEm15cm9vdGNhQGdtYWlsLmNv\n" +
            "bTAeFw0yMDA1MjgxNjA2NTdaFw0zMDA1MjYxNjA2NTdaMIGSMQswCQYDVQQGEwJD\n" +
            "TjERMA8GA1UECAwIU2hhbmdoYWkxETAPBgNVBAcMCFNoYW5naGFpMREwDwYDVQQK\n" +
            "DAhteWNsaWVudDETMBEGA1UECwwKbXljbGllbnRvdTESMBAGA1UEAwwJTXkgQ2xp\n" +
            "ZW50MSEwHwYJKoZIhvcNAQkBFhJteWNsaWVudEBnbWFpbC5jb20wggEiMA0GCSqG\n" +
            "SIb3DQEBAQUAA4IBDwAwggEKAoIBAQCv6sMVF8sDljD4vIRrOLVnGhIjIdKtt+W0\n" +
            "RgP6QxM9ZJZrHAKc+nv6w+ssHxxBGwXp/8p5KxZ9VQybV8vZEPiP9Aca8WH9eQoJ\n" +
            "qWpaJ5y9kO6DJXUALLVgycqkfBTNpVjy/dlb4lzHmecnoDgOToPJEXc4SSQPn0K0\n" +
            "Mko1FRy8PLkW4zHbuaypVflYLYPL1ZfRN8qSbhEF20tpaw5yC/GiGGf5TBN+bhQM\n" +
            "hclnyOQIxMQij71fThOxTutK9J29LdRJ+0WJLzTBl+RoEVLikKh/4cy+rSOkJjQl\n" +
            "Hq5p7Wcub35MNwrMFHs+hlC82k0zkj4JqAXuPLBR3nq58am9Q5q1AgMBAAEwDQYJ\n" +
            "KoZIhvcNAQELBQADggEBAFfdrbqmaPiFhPjinrfn3P712Z8vO8FKhAPXV9ItQGEb\n" +
            "3yU93OUZB3MsO67GJbT8Bk5g9H6tkrHwtvDcm+5KKCSCyeKhAvPF1IcPN77aZ+LA\n" +
            "Fskfss84TckAeDvUXIzMC77VZAV4j0rYZU30NunvDnv35Od/8glUE/EKDbu3tKUp\n" +
            "2FQp0EGHtoToIGFRwdMVB2m64pKqeSD2eolW6wHPjJTDmTXFTt58vK3TqSrjfB0Q\n" +
            "lu0QoMSWN4myDV5pdFCaCUZkpQcVMpQet3kRaCGDznM0khwvZOSuTO5qy88tCFYf\n" +
            "zv8WVng+7kHl0xqqOmCT/mO8TJxZviD+VOrts4AT3RA=\n" +
            "-----END CERTIFICATE-----\n";


    private static final String CLIENTKEY =
            "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEogIBAAKCAQEAr+rDFRfLA5Yw+LyEazi1ZxoSIyHSrbfltEYD+kMTPWSWaxwC\n" +
            "nPp7+sPrLB8cQRsF6f/KeSsWfVUMm1fL2RD4j/QHGvFh/XkKCalqWiecvZDugyV1\n" +
            "ACy1YMnKpHwUzaVY8v3ZW+Jcx5nnJ6A4Dk6DyRF3OEkkD59CtDJKNRUcvDy5FuMx\n" +
            "27msqVX5WC2Dy9WX0TfKkm4RBdtLaWsOcgvxohhn+UwTfm4UDIXJZ8jkCMTEIo+9\n" +
            "X04TsU7rSvSdvS3USftFiS80wZfkaBFS4pCof+HMvq0jpCY0JR6uae1nLm9+TDcK\n" +
            "zBR7PoZQvNpNM5I+CagF7jywUd56ufGpvUOatQIDAQABAoIBAA0wCznck8KMEtXC\n" +
            "xCaJlMfK44swsOuG+rhd+1RajOmwTbpv2h5MhNjSsSGYn3SeAv58x3/34/K3Wn+W\n" +
            "wFhgdlHMWHADonXvCfqZcbiaeZyYbdj1COVfdKVx2zgjeSfFenqU3yONP6lS90o4\n" +
            "L5ua9TQwlABrM0HcjZKWpot+Lq5Juz5tZ0AQ4jQKQCHgg5CExDhHpavSdAfJihMi\n" +
            "V++eS1YSJWzZ7sEAtweTkkVRC2LSvhZpPbD5N5kaItnUimx3OF4LzVzZbC2jMT+X\n" +
            "DuTIQS4CmlKpDL0h6HmRiJMshhb2fY4461CHvpF4VVdH38hmJoIS+7l9GLpLHzmT\n" +
            "SD8EFOECgYEA2aSLj64C8Uax90NigpICf3sTsZgGud2OyBMShn+Y6rcrhVU3iFfJ\n" +
            "1z5uee+mptMoHLWYLoO9u5ZwJmXalNNguJ5GfaVBKzHEbuLTGOt3QYjTDVenXfTR\n" +
            "MVkO5TsoBL3F0TGWYF2d1cpYgqi/LSJF+bTfUGI4cj+oaFPDuy6dfikCgYEAzuuq\n" +
            "PUaPy1V8c0APWIC5eONAtOiuUUG/1Q66v5lVvtXMCEMXU2ZyoVsPc0W67VY5V+cj\n" +
            "1+ZoI+Cg+RuIYivxb2RqWelO2Z7dkt4sVCGO9PEg97dMqycyHrOSM4wxcKPNIkE2\n" +
            "wc3QySK9yRiuHWVZdGn3QDryq9br8v6d+HEjsa0CgYA5q8Znfj69XHRQBwUVgEc1\n" +
            "LJHdX2F44uKw1Yz1It6MY5keraQXdtu/M4XmvcA1OILKiLwYp0Qhbw2svxSalyBs\n" +
            "C1S2epG4NmnM0EILumlRbkYupOluli7Qmr69s105VGgUtMUaJ/Ro8ENf7AMsnIZa\n" +
            "UrlR/ZTjgUbIzllxQjrtcQKBgHVUKdbO8PBCC+wp0cnES+bhLSqnmVhX6Nd9n6TO\n" +
            "5FDuV+ADcvGGcM9PnAYn7uNu9dsDJE7ixgA5+ezmkMHh2ufnz2omIeDB9S+3LPmT\n" +
            "gcUgCSqNVDrkYcYI2ojCILKXHtUil1LzSkUSnvytMuVWN5AMGffgtQN9PZMujxJ1\n" +
            "zOCNAoGAFu4XPV/UQ3Ltsq10TrQBIDmSL2ktYJQOh7EQbrP/H9PoyTI5Ko2L2JfE\n" +
            "ESLtNsV8iOHGoLAXS6YibmQVX/5Rq5Xu2OFYC/29ZH8efgEVyhwni2PF+eZHlFx2\n" +
            "ov6dW2WTCEcPwaDs/7GvpENAMTBP3SB7i8jfDXI4IUqyrhDEupc=\n" +
            "-----END RSA PRIVATE KEY-----";

    /*public static SSLSocketFactory getSSLCertifcation(Context context) {
        SSLSocketFactory sslSocketFactory = null;
        try {
            // 服务器端需要验证的客户端证书，其实就是客户端的keystore
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            // 客户端信任的服务器端证书
            KeyStore trustStore = KeyStore.getInstance(KEYSTORE_TYPE);

            //读取证书
            InputStream ksIn = context.getAssets().open(CLIENT_PRI_KEY);
            InputStream tsIn = context.getAssets().open(TRUSTSTORE_PUB_KEY);

            //加载证书
            keyStore.load(ksIn, CLIENT_BKS_PASSWORD.toCharArray());
            trustStore.load(tsIn, TRUSTSTORE_BKS_PASSWORD.toCharArray());
            ksIn.close();
            tsIn.close();


            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(CERTIFICATE_STANDARD);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(CERTIFICATE_STANDARD);
            trustManagerFactory.init(trustStore);
            keyManagerFactory.init(keyStore, CLIENT_BKS_PASSWORD.toCharArray());

            //初始化SSLContext
            SSLContext sslContext = SSLContext.getInstance(PROTOCOL_TYPE);
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());

            sslSocketFactory = sslContext.getSocketFactory();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }*/


    //获取这个SSLSocketFactory
    public static SSLSocketFactory getSSLSocketFactory(Context context) {
        try {
            //初始化信任库
/*
            ByteArrayInputStream clientIns = new ByteArrayInputStream(CLIENTCERT.getBytes(Charset.forName("UTF-8")));
            CertificateFactory clientCF = CertificateFactory.getInstance("X.509");
            X509Certificate clientCert = (X509Certificate) clientCF.generateCertificate(clientIns);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("clientCert", clientCert);
            keyStore.setKeyEntry("clientkey", null, null);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());//创建KeyManagerFactory对象
            keyManagerFactory.init(keyStore, CLIENT_CERT_PASSWORD.toCharArray());//加载1中的keyStore和server的密钥对密码keyStorePass来初始化
*/
            InputStream is = new ByteArrayInputStream(CACERT.getBytes(Charset.forName("UTF-8")));
            /**
             * 方式一
             */
            KeyStore trustKeyStore = getTrustKeyStore(is);

            /**
             * 方式二
             */

//            KeyStore trustKeyStore = getKeyStore(is, "123456", "BKS");

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustKeyStore);


            //初始化密钥库
            /*
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            InputStream inputStream = context.getAssets().open("myclient.p12");
            keystore.load(inputStream, CLIENT_CERT_PASSWORD.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, null);
            inputStream.close();
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

    private static KeyStore getTrustKeyStore(InputStream is) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate trustCert = (X509Certificate) cf.generateCertificate(is);
        keyStore.setCertificateEntry("caCert", trustCert);

        return keyStore;
    }

    private static KeyStore getTrustKeyStore2(Context context) throws Exception {
        // 信任库路径，使用keytool生成的库文件
        InputStream is = context.getAssets().open("ca.truststore");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(is, "123456".toCharArray());
        is.close();

        return keyStore;
    }

    private static KeyStore getKeyStore(InputStream inputStream, String password, String type) throws Exception {
        KeyStore keystore = KeyStore.getInstance(type);
        keystore.load(inputStream, password.toCharArray());
        return keystore;
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
