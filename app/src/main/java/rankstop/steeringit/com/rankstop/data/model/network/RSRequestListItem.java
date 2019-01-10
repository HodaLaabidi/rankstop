package rankstop.steeringit.com.rankstop.data.model.network;

public class RSRequestListItem {

    private int perPage, page;
    private String userId;

    public RSRequestListItem(int perPage, int page, String userId) {
        this.perPage = perPage;
        this.page = page;
        this.userId = userId;
    }

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
}
