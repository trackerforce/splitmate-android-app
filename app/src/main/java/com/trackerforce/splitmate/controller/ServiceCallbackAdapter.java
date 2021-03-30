package com.trackerforce.splitmate.controller;

import android.content.Context;

import com.trackerforce.splitmate.utils.AppUtils;

public abstract class ServiceCallbackAdapter<T> implements ServiceCallback<T> {

    public final Context context;

    public ServiceCallbackAdapter(Context context) {
        this.context = context;
    }

    public abstract void onSuccessAdapter(T data);

    @Override
    public void onSuccess(T data) {
        onSuccessAdapter(data);
    }

    @Override
    public void onError(String error) {
        AppUtils.showMessage(context, error);
    }

}
