package rankstop.steeringit.com.rankstop.data.model;

import java.io.Serializable;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.utils.RSDateParser;

public class Comment implements Serializable {

    private String date, _id, text, color;
    private User user;

    public Comment(String date, String _id, String text, String color, User user) {
        this.date = date;
        this._id = _id;
        this.text = text;
        this.color = color;
        this.user = user;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
