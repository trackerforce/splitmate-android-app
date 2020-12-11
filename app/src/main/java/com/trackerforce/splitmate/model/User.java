package com.trackerforce.splitmate.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class User implements Serializable {

    private String _id;
    private String name;
    private String username;
    private String email;
    private String password;
    private String plan;
    private String[] events_pending;
    private String[] events_archived;
    private String eventId;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String[] getEvents_pending() {
        return events_pending;
    }

    public void setEvents_pending(String[] events_pending) {
        this.events_pending = events_pending;
    }

    public String[] getEvents_archived() {
        return events_archived;
    }

    public void setEvents_archived(String[] events_archived) {
        this.events_archived = events_archived;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(_id, user._id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", plan='" + plan + '\'' +
                ", events_pending=" + Arrays.toString(events_pending) +
                ", events_archived=" + Arrays.toString(events_archived) +
                '}';
    }
}
