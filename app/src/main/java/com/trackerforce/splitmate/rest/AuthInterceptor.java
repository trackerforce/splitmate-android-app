package com.trackerforce.splitmate.rest;

import android.util.Log;

import com.trackerforce.splitmate.BuildConfig;
import com.trackerforce.splitmate.utils.Config;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private static final String TAG = AuthInterceptor.class.getSimpleName();
    private static String DIGESTED_API_KEY;

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Config config = Config.getInstance();
        final String token = config.getToken() == null || config.getToken().isEmpty() ? "" : config.getToken();
        return chain.proceed(chain.request()
                .newBuilder()
                .addHeader("Authorization", String.format("Bearer %s", token))
                .addHeader("X-API-Key", DIGESTED_API_KEY)
                .build());
    }

    static {
        try {
            final String apiKey = BuildConfig.API_KEY;
            MessageDigest msd1 = MessageDigest.getInstance("MD5");
            msd1.update(apiKey.getBytes(),0, apiKey.length());
            DIGESTED_API_KEY = new BigInteger(1, msd1.digest()).toString(16).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.getMessage());
        }
    }

}
