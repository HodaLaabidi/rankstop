package com.steeringit.rankstop.data.model.db;

import java.io.Serializable;

public class RSNotif implements Serializable {

    private String date, _id, text, userId;
    private boolean visibility;
    private ItemDetails item;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public ItemDetails getItem() {
        return item;
    }

    public void setItem(ItemDetails item) {
        this.item = item;
    }
}
