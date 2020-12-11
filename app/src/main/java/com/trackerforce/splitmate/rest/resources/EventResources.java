package com.trackerforce.splitmate.rest.resources;

import com.trackerforce.splitmate.model.EmailsDTO;
import com.trackerforce.splitmate.model.Event;
import com.trackerforce.splitmate.model.EventDTO;
import com.trackerforce.splitmate.model.Item;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface EventResources {

    @POST("event/v1/invite_all/{eventid}")
    Call<Map<String, String>> inviteAll(@Path("eventid") String eventid, @Body EmailsDTO emails);

    @GET("event/v1/my_events/{category}")
    Call<Event[]> myEvents(@Path("category") String category);

    @GET("event/v1/{id}")
    Call<Event> getEventById(@Path("id") String id);

    @GET("event/v1/{eventId}/item/{id}")
    Call<Item> getEventItemById(@Path("eventId") String eventId, @Path("id") String id);

    @POST("event/v1/create")
    Call<Event> create(@Body Event event);

    @PATCH("event/v1/{id}")
    Call<Event> edit(@Path("id") String id, @Body EventDTO event);

    @PATCH("event/v1/{id}/add/item")
    Call<Event> addItem(@Path("id") String id, @Body Item item);

    @PATCH("event/v1/{id}/edit/item")
    Call<Event> editItem(@Path("id") String id, @Body Item item);

    @PATCH("event/v1/{id}/delete/item")
    Call<Event> deleteItem(@Path("id") String id, @Body Item item);

    @PATCH("event/v1/{id}/pick/item")
    Call<Event> pickupItem(@Path("id") String id, @Body Item item);

    @PATCH("event/v1/{id}/unpick/item")
    Call<Event> unpickItem(@Path("id") String id, @Body Item item);

    @DELETE("event/v1/{id}")
    Call<Map<String, String>> delete(@Path("id") String id);

    @POST("user/v1/event/{user}/remove")
    Call<Event> removeMember(@Path("user") String userId, @Query("eventid") String eventId);

    @PATCH("event/v1/transfer/{id}/{organizer}")
    Call<Event> transfer(@Path("id") String id, @Path("organizer") String organizer);

    @PATCH("poll/v1/{eventId}/{itemId}/{pollItemId}")
    Call<Item> pollVote(@Path("eventId") String eventId,
                        @Path("itemId") String itemId,
                        @Path("pollItemId") String pollItemId);
}
