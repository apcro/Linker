package com.alienpants.numberlink.libraries;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.alienpants.numberlink.application.NumberLink;
import com.alienpants.numberlink.responsemodels.BaseResponse;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utilities {

    private static final String TAG = Utilities.class.getSimpleName();

    public static void sendDeviceTokenToServer() {

        // send device token to server and store against current userId
        String userToken = NumberLink.getBackend().getUserToken();
        String deviceToken = NumberLink.getBackend().getSharedPreferences("deviceToken");

        if (userToken!= null) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<BaseResponse> call = apiService.updateDeviceToken(userToken, deviceToken, "android");
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    NumberLink.getBackend().setSharedPreferences("deviceTokenSaved", "yes");
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    // Log error here since request failed
                }
            });
        }
    }

    public static Point getDisplaySize(WindowManager windowManager) {
        try {
            if (Build.VERSION.SDK_INT > 16) {
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
            } else {
                return new Point(0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Point(0, 0);
        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void hideUI(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

//    public String mockDataReader(String fileName) {
//        try {
//            Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader()).getResource(fileName).toURI());
//            StringBuilder data = new StringBuilder();
//            Stream<String> lines = Files.lines(path);
//            lines.forEach((String line) -> data.append(line).append("\n"));
//            lines.close();
//
//            path = null;
//            return data.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
