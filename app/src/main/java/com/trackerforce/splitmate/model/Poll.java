package com.trackerforce.splitmate.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Poll implements Serializable {

    private String _id;
    private String value = "";
    private String[] votes;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String[] getVotes() {
        return votes;
    }

    public void setVotes(String[] votes) {
        this.votes = votes;
    }

    public void addVote(String vote) {
        if (votes == null || votes.length == 0) {
            votes = new String[1];
            votes[0] = vote;
        } else {
            List<String> votesList = new LinkedList<>(Arrays.asList(votes));
            votesList.add(vote);
            votes = votesList.toArray(new String[0]);
        }
    }

    public void removeVote(String vote) {
        if (votes != null && votes.length > 0) {
            List<String> votesList = new LinkedList<>(Arrays.asList(votes));
            votesList.remove(vote);
            votes = votesList.toArray(new String[0]);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poll poll = (Poll) o;
        return Objects.equals(_id, poll.getId());
    }

    @Override
    public String toString() {
        return "Poll{" +
                "_id='" + _id + '\'' +
                ", value='" + value + '\'' +
                ", votes=" + Arrays.toString(votes) +
                '}';
    }
}
