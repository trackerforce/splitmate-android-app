package com.trackerforce.splitmate.model.pusher;

import com.google.gson.Gson;

public class PusherData {

    private final String action;
    private final String channel;

    public PusherData(String channel, String action) {
        this.channel = channel;
        this.action = action;
    }

    public static <T> T getDTO(Class<T> dtoType, Object... args) {
        Gson gson = new Gson();
        return gson.fromJson(args[0].toString(), dtoType);
    }

    public String getAction() {
        return action;
    }

    public String getChannel() {
        return channel;
    }
}
