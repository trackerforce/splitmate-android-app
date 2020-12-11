package com.trackerforce.splitmate.model;

import java.util.Arrays;

public class EmailsDTO {

    private String[] emails;

    public String[] getEmails() {
        return emails;
    }

    public void setEmails(String[] emails) {
        this.emails = emails;
    }

    @Override
    public String toString() {
        return "EmailsDTO{" +
                "emails=" + Arrays.toString(emails) +
                '}';
    }
}
