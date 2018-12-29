package com.alienpants.numberlink.libraries;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.alienpants.numberlink.application.NumberLink;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Currency;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Backend {

    private static final String USER_TOKEN_PREFERENCES_KEY = "userToken";
    private static final String DEVICE_TOKEN_PREFERENCES_KEY = "deviceToken";
    private static final String HAS_SEEN_TUTORIAL_PREFERENCES_KEY = "hasSeenTutorial";

    private static final String HASH_ALGORITHM = "HmacSHA256";
    private static String privateKey = "ven44p9wt8q3-lj4gb3q-2gbhw24p";
    private static String publicKey = "gl4n51shq45n-yghq234-89asit43";
    private static String salt = "gbrbalagagrvbl";



//    private static String baseUrl = "https://api.takemeaway.io/api/1.0/";
    private static String baseUrl = "http://dev.takemeaway/api/1.0/";


    private NumberLink application;

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String USER_NORMAL = "normal";
    public final String USER_TWITTER = "twitter";
    public final String USER_FACEBOOK = "facebook";

    public Backend(NumberLink application) {
        this.application = application;
    }

    public NumberLink getApplication() {
        return application;
    }

    public String getSharedPreferences(String key) {
        SharedPreferences preferences = getApplication().getSharedPreferences(NumberLink.preferencesFileName, Context.MODE_PRIVATE);
        if (preferences.contains(key)) {
            return preferences.getString(key, null);
        }
        return null;
    }

    public void setSharedPreferences(String key, String value) {
        SharedPreferences preferences = getApplication().getSharedPreferences(NumberLink.preferencesFileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void clearAllSharedPreferences() {
        SharedPreferences preferences = getApplication().getSharedPreferences(NumberLink.preferencesFileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.clear();

        // Preserve hasSeenTutorial, bound to device
        String hasSeenTutorial = "no";
        if (preferences.contains(HAS_SEEN_TUTORIAL_PREFERENCES_KEY)) {
            hasSeenTutorial = preferences.getString(HAS_SEEN_TUTORIAL_PREFERENCES_KEY, null);
        }
        editor.putString(HAS_SEEN_TUTORIAL_PREFERENCES_KEY, hasSeenTutorial);

        editor.apply();
    }

    public void Logout() {

        clearAllSharedPreferences();

    }

    public String getUserToken() {
        return getSharedPreferences(USER_TOKEN_PREFERENCES_KEY);
    }

    public void saveUserToken(String userToken) {
        if (userToken == "") {
            setSharedPreferences(USER_TOKEN_PREFERENCES_KEY, null);
        } else {
            setSharedPreferences(USER_TOKEN_PREFERENCES_KEY, userToken);
        }
    }

    public String getDeviceToken() {
        return getSharedPreferences(DEVICE_TOKEN_PREFERENCES_KEY);
    }

    public void saveDeviceToken(String deviceToken) {
        setSharedPreferences(DEVICE_TOKEN_PREFERENCES_KEY, deviceToken);
    }

    public boolean hasSeenTutorial() {
        String hasSeenTutorial = getSharedPreferences(HAS_SEEN_TUTORIAL_PREFERENCES_KEY);
        return hasSeenTutorial != null && hasSeenTutorial.equals("yes");
    }

    public void setTutorialSeen() {
        setSharedPreferences(HAS_SEEN_TUTORIAL_PREFERENCES_KEY, "yes");
    }

    public void setTutorialToSee() {
        setSharedPreferences(HAS_SEEN_TUTORIAL_PREFERENCES_KEY, "no");
    }

    public boolean isLoggedIn() {
        return true;
    }

    private String getTimestamp() {
        long timestamp = new Date().getTime();
        return String.valueOf(timestamp);
    }

    public String getAuthorisationHeaderValue() {
        String timestamp = getTimestamp();
        String hashSource = timestamp + publicKey + salt;
        String hash = null;
        try {
            hash = hashMac(hashSource, privateKey);
        } catch (SignatureException e) {
            Log.e("SignatureException", e.getMessage());
        }

        if (hash == null) {
            hash = "";
        }

        return "KeyAuth publicKey=" + publicKey + " hash=" + hash + " ts=" + timestamp;
    }

    static String hashMac(String text, String secretKey)
            throws SignatureException {
        try {
            Key sk = new SecretKeySpec(secretKey.getBytes(), HASH_ALGORITHM);
            Mac mac = Mac.getInstance(sk.getAlgorithm());
            mac.init(sk);
            final byte[] hmac = mac.doFinal(text.getBytes());
            return toHexString(hmac);
        } catch (NoSuchAlgorithmException e1) {
            // throw an exception or pick a different encryption method
            throw new SignatureException(
                    "error building signature, no such algorithm in device " + HASH_ALGORITHM);
        } catch (InvalidKeyException e) {
            throw new SignatureException("error building signature, invalid key " + HASH_ALGORITHM);
        }
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }

    static String getPrivateKey() {
        return privateKey;
    }

    static String getPublicKey() {
        return publicKey;
    }

    static String getSalt() {
        return salt;
    }

    static String getBaseUrl() {
        return baseUrl;
    }

    public String CurrencySymbol() {
        Currency currency = Currency.getInstance(Locale.getDefault());
        return currency.getSymbol();
    }

    public boolean isFirstTimeLaunch() {
        SharedPreferences preferences = getApplication().getSharedPreferences(NumberLink.preferencesFileName, Context.MODE_PRIVATE);
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        SharedPreferences preferences = getApplication().getSharedPreferences(NumberLink.preferencesFileName, Context.MODE_PRIVATE);
        Editor preferencesEditor = preferences.edit();
        preferencesEditor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        preferencesEditor.apply();
    }

}