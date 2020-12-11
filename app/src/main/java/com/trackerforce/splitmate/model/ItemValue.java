package com.trackerforce.splitmate.model;

import java.io.Serializable;
import java.util.Objects;

public class ItemValue implements Serializable {

    private String type = "";
    private String value = "";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemValue itemValue = (ItemValue) o;
        return Objects.equals(type, itemValue.getType()) &&
                Objects.equals(value, itemValue.getValue());
    }

    @Override
    public String toString() {
        return "ItemValue{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
