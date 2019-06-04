package com.s.sdk.network;

/**
 * Created by Nhat Nguyen on 6/1/2016.
 */


import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;


@SuppressWarnings("unused")
public class SelfSigningClientBuilder {
    public static final int TIME_OUT = 30000;

    public static OkHttpClient getUnsafeOkHttpClient(final String token) {
        try {
          /*  if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 21) {
                return getNewHttpClient(token);
            }*/

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder builder = chain.request().newBuilder();
                    if (!TextUtils.isEmpty(token)) {
                        builder.addHeader("Authorization", "Bearer " + token);
                        //Log.v("Authorization", "Bearer " + token);
                    }
                    // String agent = Utils.convertObjTosjon(new Agent());
                    //builder.addHeader("Agent", agent);
                    //builder.addHeader("X-Api-Key", "5gfp6p8nhee404ki9z0m7lgubz3zjofba");
                    return chain.proceed(builder.build());
                }
            });

            builder.connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS);
            builder.readTimeout(TIME_OUT, TimeUnit.MILLISECONDS);
            builder.writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            //enableTls12OnPreLollipop(builder);
            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 21) {
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.1");
                sc.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_1)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
            }
        }

        return client;
    }


//    private static OkHttpClient getNewHttpClient(final String token) {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        OkHttpClient.Builder client = new OkHttpClient.Builder();
//        client.followRedirects(true);
//        client.followSslRedirects(true);
//        client.retryOnConnectionFailure(true);
//        client.cache(null);
//        client.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
//        client.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
//        client.readTimeout(TIME_OUT, TimeUnit.SECONDS);
//        client.addInterceptor(interceptor);
//
//        if (!TextUtils.isEmpty(token)) {
//            client.addInterceptor(new Interceptor() {
//                @Override
//                public Response intercept(Chain chain) throws IOException {
//                    Request request = chain.request();
//                    request = request.newBuilder()
//                            .addHeader("Authorization", "Bearer " + token)
//                            .build();
//                    Response response = chain.proceed(request);
//                    return response;
//                }
//            });
//        }
//
//
//        return enableTls12OnPreLollipop(client).build();
//    }


}