package com.steeringit.rankstop.data.model.db;

import java.io.Serializable;

public class Abuse implements Serializable {

    private String _id, name;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
