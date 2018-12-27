package rankstop.steeringit.com.rankstop.data.model;

import java.io.Serializable;

import rankstop.steeringit.com.rankstop.R;
import rankstop.steeringit.com.rankstop.utils.RSDateParser;

public class Comment implements Serializable {

    private String date, _id, text, color, evaluations;
    private User userId;

    public Comment(String date, String _id, String text, String color, String evaluations, User userId) {
        this.date = date;
        this._id = _id;
        this.text = text;
        this.color = color;
        this.evaluations = evaluations;
        this.userId = userId;
    }

    public Comment() {
    }

    public String getDate() {
        return RSDateParser.convertToDateTimeFormat(date);
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
        switch (color) {
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

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public String getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(String evaluations) {
        this.evaluations = evaluations;
    }
}
