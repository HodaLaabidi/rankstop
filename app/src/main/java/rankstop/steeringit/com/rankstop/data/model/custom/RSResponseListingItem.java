package rankstop.steeringit.com.rankstop.data.model.custom;

import java.util.List;

import rankstop.steeringit.com.rankstop.data.model.Item;

public class RSResponseListingItem {

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
