package com.steeringit.rankstop.data.model.db;

import java.io.Serializable;

public class Criteria implements Serializable {

    private String _id;
    private Object name;

    public Criteria(Object name, String _id) {
        this.name = name;
        this._id = _id;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
