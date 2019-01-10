package rankstop.steeringit.com.rankstop.data.model.network;

import java.io.Serializable;
import java.util.List;

import rankstop.steeringit.com.rankstop.data.model.db.Category;
import rankstop.steeringit.com.rankstop.data.model.db.ItemDetails;

public class RSResponseSearch implements Serializable {

    private List<Category> category;
    private List<ItemDetails> item;

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public List<ItemDetails> getItem() {
        return item;
    }

    public void setItem(List<ItemDetails> item) {
        this.item = item;
    }
}
