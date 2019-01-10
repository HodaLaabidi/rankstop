package rankstop.steeringit.com.rankstop.data.model.network;

import java.util.List;

import rankstop.steeringit.com.rankstop.data.model.db.Item;

public class RSItemIndex {
    private int index;
    private List<Item> items;

    public RSItemIndex(int index, List<Item> items) {
        this.index = index;
        this.items = items;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
