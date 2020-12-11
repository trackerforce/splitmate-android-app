package com.trackerforce.splitmate.model;

public class ErrorResponse {

    private String error;
    private String document;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "error='" + error + '\'' +
                ", document='" + document + '\'' +
                '}';
    }
}
