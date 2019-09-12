package com.steeringit.rankstop.data.model.db;

import java.io.Serializable;

import com.steeringit.rankstop.R;

public class Picture implements Serializable {

    private String date, _id, url, color, urlPicture;
    private User userId;

    public Picture(String date, String _id, String url, String color, User userId, String urlPicture) {
        this.date = date;
        this._id = _id;
        this.url = url;
        this.color = color;
        this.userId = userId;
        this.urlPicture = urlPicture;
    }

    public Picture() {
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getColor() {
        switch(color){
            case "green":
                return R.color.colorGreenPie;
            case "red":
                return R.color.colorRedPie;
            case "yellow":
                return R.color.colorOrangePie;
        }
        return R.color.colorGray;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public User getUser() {
        return userId;
    }

    public void setUser(User userId) {
        this.userId = userId;
    }

    public String getPictureEval() {
        return urlPicture;
    }

    public void setPictureEval(String urlPicture) {
        this.urlPicture = urlPicture;
    }
}
