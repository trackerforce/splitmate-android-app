package com.trackerforce.splitmate.model;

import java.io.Serializable;
import java.util.Objects;

public class Item implements Serializable {

    private String _id;
    private String name;
    private ItemValue[] details;
    private Poll[] poll;
    private String poll_name;
    private String assigned_to;
    private String created_by;
    private User v_assigned_to;
    private User v_created_by;
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

    public ItemValue[] getDetails() {
        return details;
    }

    public void setDetails(ItemValue[] details) {
        this.details = details;
    }

    public Poll[] getPoll() {
        return poll;
    }

    public void setPoll(Poll[] poll) {
        this.poll = poll;
    }

    public String getPoll_name() {
        return poll_name;
    }

    public void setPoll_name(String poll_name) {
        this.poll_name = poll_name;
    }

    public String getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(String assigned_to) {
        this.assigned_to = assigned_to;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public User getV_assigned_to() {
        return v_assigned_to;
    }

    public void setV_assigned_to(User v_assigned_to) {
        this.v_assigned_to = v_assigned_to;
    }

    public User getV_created_by() {
        return v_created_by;
    }

    public void setV_created_by(User v_created_by) {
        this.v_created_by = v_created_by;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public boolean hasPoll() {
        return poll_name != null &&
                !poll_name.isEmpty() &&
                poll != null &&
                poll.length > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(_id, item._id);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", assigned_to='" + assigned_to + '\'' +
                ", created_by='" + created_by + '\'' +
                '}';
    }
}
