package com.steeringit.rankstop.data.model.db;

import java.io.Serializable;

public class ItemSearchedByBarcode implements Serializable {

    String _id;
    String title;


    public ItemSearchedByBarcode(String _id, String title) {
        this._id = _id;
        this.title = title;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
