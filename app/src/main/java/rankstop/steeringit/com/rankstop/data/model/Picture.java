package rankstop.steeringit.com.rankstop.data.model;

import java.io.Serializable;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.utils.RSDateParser;

public class Picture implements Serializable {

    private String date, _id, url, color, pictureEval;
    private User user;

    public Picture(String date, String _id, String url, String color, User user, String pictureEval) {
        this.date = date;
        this._id = _id;
        this.url = url;
        this.color = color;
        this.user = user;
        this.pictureEval = pictureEval;
    }

    public Picture() {
    }

    public String getDate() {
        return RSDateParser.convertToDateFormat(date);
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
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPictureEval() {
        return pictureEval;
    }

    public void setPictureEval(String pictureEval) {
        this.pictureEval = pictureEval;
    }
}
