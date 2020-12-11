package com.trackerforce.splitmate.model.pusher;

import com.trackerforce.splitmate.pusher.PusherEvents;

public class PusherMemberDTO extends PusherData {

    private String memberId;

    public PusherMemberDTO(String eventId, PusherEvents pusherEvent) {
        super(eventId, pusherEvent.toString());
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
