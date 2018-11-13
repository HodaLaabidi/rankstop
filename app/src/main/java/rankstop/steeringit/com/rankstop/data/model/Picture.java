package rankstop.steeringit.com.rankstop.data.model;

public class Picture {

    private String date, _id, url, color, userId, pictureEval;

    public Picture(String date, String _id, String url, String color, String userId, String pictureEval) {
        this.date = date;
        this._id = _id;
        this.url = url;
        this.color = color;
        this.userId = userId;
        this.pictureEval = pictureEval;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPictureEval() {
        return pictureEval;
    }

    public void setPictureEval(String pictureEval) {
        this.pictureEval = pictureEval;
    }
}
