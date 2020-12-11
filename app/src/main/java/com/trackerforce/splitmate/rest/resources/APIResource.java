package com.trackerforce.splitmate.rest.resources;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIResource {

    @GET("/check")
    Call<Map<String, String>> check();
}
