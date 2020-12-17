package com.trackerforce.splitmate.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class Event implements Serializable {

    private String _id;
    private String version;
    private String name;
    private String[] members;
    private String description;
    private Date date;
	private String location;
    private String organizer;
    private Item[] items;
    private User v_organizer;
    private User[] v_members;
    private String category;

    public float getTotalCost() {
        int unit;
        float total = 0;
        for (Item item : items) {
            if (item.getDetails() != null) {
                unit = Arrays.stream(item.getDetails())
                        .filter(val -> val.getType().equals("Unit"))
                        .map(val -> Integer.parseInt(val.getValue()))
                        .reduce(0, Integer::sum);

                total += Arrays.stream(item.getDetails())
                        .filter(val -> val.getType().equals("Cost"))
                        .map(val -> Float.parseFloat(val.getValue()))
                        .reduce(0f, Float::sum) * (unit == 0 ? 1 : unit) ;
            }
        }
        return total;
    }

    public int getVotingPolls() {
        return (int) Arrays.stream(items)
                .filter(item -> item.getPoll_name() != null && item.getPoll() != null && item.getPoll().length > 0)
                .count();
    }

    public int getPendingItems() {
        return (int) Arrays.stream(items)
                .filter(item -> item.getAssigned_to() == null || item.getAssigned_to().isEmpty())
                .count();
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getMembers() {
        return members;
    }

    public void setMembers(String[] members) {
        this.members = members;
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

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public User getV_organizer() {
        return v_organizer;
    }

    public void setV_organizer(User v_organizer) {
        this.v_organizer = v_organizer;
    }

    public User[] getV_members() {
        return v_members;
    }

    public void setV_members(User[] v_members) {
        setMembers(Arrays.stream(v_members).map(User::getId).toArray(String[]::new));
        this.v_members = v_members;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(_id, event._id) &&
                Objects.equals(category, event.category) &&
                Objects.equals(version, event.version) &&
                Arrays.equals(members, event.members) &&
                Arrays.equals(items, event.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, version, name, organizer);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + _id + '\'' +
                ", category='" + category + '\'' +
                ", version='" + version + '\'' +
                ", name='" + name + '\'' +
                ", members=" + Arrays.toString(members) +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", location='" + location + '\'' +
                ", organizer='" + organizer + '\'' +
                ", items=" + Arrays.toString(items) +
                ", v_organizer=" + v_organizer +
                ", v_members=" + Arrays.toString(v_members) +
                '}';
    }
}
