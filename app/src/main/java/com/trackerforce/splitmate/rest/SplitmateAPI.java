package com.trackerforce.splitmate.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trackerforce.splitmate.rest.resources.APIResource;
import com.trackerforce.splitmate.rest.resources.EventResources;
import com.trackerforce.splitmate.rest.resources.UserResources;
import com.trackerforce.splitmate.utils.Config;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplitmateAPI {

    private static <T> T builder(Class<T> service) {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new AuthInterceptor()).build();
        return new Retrofit.Builder()
                .baseUrl(Config.getInstance().API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(createGsonInstance()))
                .build()
                .create(service);
    }

    public static Gson createGsonInstance() {
        return new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                .create();
    }

    public static APIResource api() {
        return builder(APIResource.class);
    }

    public static UserResources user() {
        return builder(UserResources.class);
    }

    public static EventResources event() {
        return builder((EventResources.class));
    }
}
