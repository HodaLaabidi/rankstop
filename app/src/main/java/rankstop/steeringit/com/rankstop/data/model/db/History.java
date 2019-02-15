package rankstop.steeringit.com.rankstop.data.model.db;

import java.io.Serializable;

import rankstop.steeringit.com.rankstop.utils.RSDateParser;

public class History implements Serializable {
    private String date, _id, subject, message, user, time;
    private ItemDetails item;

    public String getDate() {
        if (date != null)
            return date;
        return null;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        if (date != null)
            return RSDateParser.convertToTimeFormat(date);
        return null;
    }

    public void setTime(String date) {
        this.date = date;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ItemDetails getItem() {
        return item;
    }

    public void setItem(ItemDetails item) {
        this.item = item;
    }
}
