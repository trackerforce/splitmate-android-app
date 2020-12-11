package com.trackerforce.splitmate.model.pusher;

import com.trackerforce.splitmate.pusher.PusherEvents;

public class PusherItemDTO extends PusherData {

    private String itemId;
    private String assigned_to;

    public PusherItemDTO(String eventId, PusherEvents pusherEvent) {
        super(eventId, pusherEvent.toString());
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(String assigned_to) {
        this.assigned_to = assigned_to;
    }

    public String getEventId() {
        return super.getChannel();
    }

}
