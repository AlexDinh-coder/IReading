package com.example.iread.apicaller;

import android.content.Context;


import com.example.iread.Interface.ImgurApi;
import com.example.iread.R;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;
    public static OkHttpClient okHttpClient;

    public static Retrofit getInstance(String baseUrl, Context context){
        try {
            if (instance == null) {
                // Load CAs from an InputStream
                CertificateFactory cf = CertificateFactory.getInstance("X.509");

                // Load the certificate from res/raw/my_server.crt
                InputStream caInput = context.getResources().openRawResource(R.raw.ireading_store_certificate);

                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                } finally {
                    caInput.close();
                }

                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                // Create an SSLContext that uses our TrustManager
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);

                // Build OkHttpClient with SSL configuration
                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
                        .build();

                // Set the OkHttpClient to the static variable

//                Gson gson = new GsonBuilder()
//                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
//                        .create();

                // Build Retrofit instance
                instance = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }

    public static OkHttpClient GetOkHttpClient(Context context) {
        try {
            if(okHttpClient == null) {
                // Load CAs from an InputStream
                CertificateFactory cf = CertificateFactory.getInstance("X.509");

                // Load the certificate from res/raw/my_server.crt
                InputStream caInput = context.getResources().openRawResource(R.raw.ireading_store_certificate);

                Certificate ca;
                try {
                    ca = cf.generateCertificate(caInput);
                    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
                } finally {
                    caInput.close();
                }

                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(keyStore);

                // Create an SSLContext that uses our TrustManager
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);

                // Build OkHttpClient with SSL configuration
                okHttpClient = new OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0])
                        .build();
                //return okHttpClient;
            }

        } catch (Exception e) {
            e.printStackTrace();
            okHttpClient = new OkHttpClient.Builder().build();

        }
       // return null;
        return okHttpClient;
    }

    public static class ImgurClient {
        private static Retrofit retrofit = null;

        public static ImgurApi getClient() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.imgur.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit.create(ImgurApi.class);
        }
    }




}