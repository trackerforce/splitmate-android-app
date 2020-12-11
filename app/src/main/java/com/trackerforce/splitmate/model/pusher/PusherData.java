package com.trackerforce.splitmate.model.pusher;

public class PusherData {

    private final String action;
    private final String channel;

    public PusherData(String channel, String action) {
        this.channel = channel;
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public String getChannel() {
        return channel;
    }
}
