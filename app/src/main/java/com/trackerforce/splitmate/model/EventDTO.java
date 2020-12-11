package com.trackerforce.splitmate.model;

import java.util.Date;

public class EventDTO {

    private String name;
    private String description;
    private Date date;
    private String location;

    public EventDTO(Event event) {
        this.name = event.getName();
        this.description = event.getDescription();
        this.date = event.getDate();
        this.location = event.getLocation();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", location='" + location + '\'' +
                '}';
    }
}
