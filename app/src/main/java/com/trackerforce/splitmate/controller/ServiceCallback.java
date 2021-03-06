package com.trackerforce.splitmate.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.UiThread;

import com.trackerforce.splitmate.LoginActivity;
import com.trackerforce.splitmate.utils.AppUtils;

import retrofit2.Response;

/**
 * It defines an asynchronous callback for service level objects
 */
public interface ServiceCallback<T> {

    void onSuccess(T data);

    void onError(String error);

    @UiThread
    default void onSuccessResponse(Context context, T data) {
        ((Activity) context).runOnUiThread(() -> onSuccess(data));
    }

    @UiThread
    default void onErrorResponse(Context context, String error) {
        ((Activity) context).runOnUiThread(() -> onError(error));
    }

    default void onError(String error, Object obj) {}

    default void errorHandler(Context context, Response<?> response) {
        if (response.code() == 401) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            this.onError("Unauthorized access");
        } else if (response.code() == 422) {
            assert response.errorBody() != null;
            this.onErrorResponse(context, AppUtils.getJsonError(response.errorBody(), "msg"));
        } else {
            assert response.errorBody() != null;
            this.onErrorResponse(context, AppUtils.getJsonValue(response.errorBody(), "error"));
        }
    }

    default void responseHandler(Context context, int expectedStatus, Response<T> response,
                                     ServiceCallback<T> callback) {
        if (response.code() == expectedStatus) {
            assert response.body() != null;
            callback.onSuccessResponse(context, response.body());
        } else {
            callback.errorHandler(context, response);
        }
    }

}
