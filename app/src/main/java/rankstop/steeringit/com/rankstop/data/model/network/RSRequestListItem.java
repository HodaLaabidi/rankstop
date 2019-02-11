package rankstop.steeringit.com.rankstop.data.model.network;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class RSRequestListItem implements Serializable {

    private int perPage, page;
    private String userId, lang;

    public RSRequestListItem() {
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @NonNull
    @Override
    public String toString() {
        return "{\"perPage\":\""+perPage+"\", \"page\":\""+page+"\", \"userId\":\""+userId+"\"}";
    }
}
