package rankstop.steeringit.com.rankstop.data.model.custom;

import java.io.Serializable;

public class RSRequestItemByCategory implements Serializable {
    private String q, catId, userId;
    private int perPage, page;

    public RSRequestItemByCategory(String q, String catId, String userId, int perPage, int page) {
        this.q = q;
        this.catId = catId;
        this.userId = userId;
        this.perPage = perPage;
        this.page = page;
    }

    public RSRequestItemByCategory() {
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
}
