package rankstop.steeringit.com.rankstop.data.model;

public class Comment {

    private String date, _id, text, color, userId;

    public Comment(String date, String _id, String text, String color, String userId) {
        this.date = date;
        this._id = _id;
        this.text = text;
        this.color = color;
        this.userId = userId;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String authorId) {
        this.userId = authorId;
    }
}
