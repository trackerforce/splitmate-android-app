package com.trackerforce.splitmate.model.pusher;

import com.trackerforce.splitmate.pusher.PusherEvents;

public class PusherPollItemDTO extends PusherItemDTO {

    private String pollItemId;
    private String voter;

    public PusherPollItemDTO(String eventId, String itemId, PusherEvents pusherEvent) {
        super(eventId, pusherEvent);
        setItemId(itemId);
    }

    public String getPollItemId() {
        return pollItemId;
    }

    public void setPollItemId(String pollItemId) {
        this.pollItemId = pollItemId;
    }

    public String getVoter() {
        return voter;
    }

    public void setVoter(String voter) {
        this.voter = voter;
    }
}
