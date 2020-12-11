package com.trackerforce.splitmate.rest.resources;

import com.trackerforce.splitmate.model.Login;
import com.trackerforce.splitmate.model.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserResources {

    @POST("/user/v1/signup")
    Call<Login> signUp(@Body User user);

    @POST("/user/v1/login")
    Call<Login> login(@Body User user);

    @POST("/user/v1/logout")
    Call<Map<String, String>> logout();

    @GET("/user/v1/me")
    Call<User> getUser();

    @GET("/user/v1/find")
    Call<User> find(@Query("username") String username);

    @POST("/user/v1/event/join")
    Call<User> joinEvent(@Query("eventid") String eventId);

    @POST("/user/v1/event/dismiss")
    Call<User> dismissEvent(@Query("eventid") String eventId);

    @POST("/user/v1/event/leave")
    Call<User> leaveEvent(@Query("eventid") String eventId);

    @POST("/user/v1/event/{action}/archive")
    Call<User> archiveEvent(@Path("action") String action, @Query("eventid") String eventId);

    @DELETE("/user/v1/me")
    Call<Map<String, String>> deleteAccount();

}
