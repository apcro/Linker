package com.alienpants.numberlink.libraries;


import android.util.Log;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String TAG = ApiClient.class.getSimpleName();

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            Log.d(TAG, "Calling: " + Backend.getBaseUrl());

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(interceptor);

            httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();

                        String authHeader = getAuthorisationHeaderValue();

                        Request request = original.newBuilder()
                              .header("Authorisation", authHeader)
                              .method(original.method(), original.body())
                              .build();

                        Response response = chain.proceed(request);
                        if (response.code() != 200) {
                            // Magic is here ( Handle the error as your way )

//                            int status = response.body().getStatus();
//                            if (status == 0) {
//                                // logged out
//                                mBackend.Logout();
//                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
//                                builder.setMessage("Your login token has expired, so we have logged you out. Please log in again.");
//                                mAlertDialog = builder.create();
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (!isFinishing()) {
//                                            mAlertDialog.show();
//                                        }
//                                    }
//                                });
//
//                            }


                            return response;
                        }
                        return response;
                    }
                }
            );

            OkHttpClient client = httpClient.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Backend.getBaseUrl())
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static String getTimestamp() {
        long timestamp = new Date().getTime();
        return String.valueOf(timestamp);
    }

    private static String getAuthorisationHeaderValue() {
        String timestamp = getTimestamp();
        String hashSource = timestamp + Backend.getPublicKey() + Backend.getSalt();
        String hash = null;
        try {
            hash = Backend.hashMac(hashSource, Backend.getPrivateKey());
        } catch (SignatureException e) {
            Log.e("SignatureException", e.getMessage());
        }

        if (hash == null) {
            hash = "";
        }

        return "KeyAuth publicKey=" + Backend.getPublicKey() + " hash=" + hash + " ts=" + timestamp;
    }

}