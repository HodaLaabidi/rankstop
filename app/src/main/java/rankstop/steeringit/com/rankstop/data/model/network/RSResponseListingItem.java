package rankstop.steeringit.com.rankstop.data.model.network;

import java.io.Serializable;
import java.util.List;

import rankstop.steeringit.com.rankstop.data.model.db.Item;

public class RSResponseListingItem implements Serializable {

    private List<Item> items;
    private int current;
    private int pages;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
