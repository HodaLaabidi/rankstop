package com.steeringit.rankstop.data.model.db;

import java.io.Serializable;

public class RSPublicUserName implements Serializable {

    private String value, type;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
