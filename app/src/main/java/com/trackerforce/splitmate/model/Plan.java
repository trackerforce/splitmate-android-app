package com.trackerforce.splitmate.model;

import java.io.Serializable;

public class Plan implements Serializable {

    private String _id;
    private String name;
    private boolean enable_ads;
    private boolean enable_invite_email;
    private int max_events;
    private int max_items;
    private int max_poll_items;
    private int max_members;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable_ads() {
        return enable_ads;
    }

    public void setEnable_ads(boolean enable_ads) {
        this.enable_ads = enable_ads;
    }

    public boolean isEnable_invite_email() {
        return enable_invite_email;
    }

    public void setEnable_invite_email(boolean enable_invite_email) {
        this.enable_invite_email = enable_invite_email;
    }

    public int getMax_events() {
        return max_events;
    }

    public void setMax_events(int max_events) {
        this.max_events = max_events;
    }

    public int getMax_items() {
        return max_items;
    }

    public void setMax_items(int max_items) {
        this.max_items = max_items;
    }

    public int getMax_poll_items() {
        return max_poll_items;
    }

    public void setMax_poll_items(int max_poll_items) {
        this.max_poll_items = max_poll_items;
    }

    public int getMax_members() {
        return max_members;
    }

    public void setMax_members(int max_members) {
        this.max_members = max_members;
    }
}
