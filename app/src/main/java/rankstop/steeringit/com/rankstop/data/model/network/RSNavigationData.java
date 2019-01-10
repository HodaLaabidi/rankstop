package rankstop.steeringit.com.rankstop.data.model.network;

import java.io.Serializable;

public class RSNavigationData implements Serializable {

    private String from, action, message, itemId, userId, categoryId, section;

    public RSNavigationData(String from, String action) {
        this.from = from;
        this.action = action;
    }

    public RSNavigationData() {
    }

    public RSNavigationData(String from, String action, String message, String itemId, String userId, String categoryId) {
        this.from = from;
        this.action = action;
        this.message = message;
        this.itemId = itemId;
        this.userId = userId;
        this.categoryId = categoryId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}
