package rankstop.steeringit.com.rankstop.data.model.custom;

import java.io.Serializable;

public class RSRequestItemData implements Serializable {
    private String itemId, userId;
    private int perPage, page;

    public RSRequestItemData(String itemId, String userId, int perPage, int page) {
        this.itemId = itemId;
        this.userId = userId;
        this.perPage = perPage;
        this.page = page;
    }

    public RSRequestItemData() {
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
