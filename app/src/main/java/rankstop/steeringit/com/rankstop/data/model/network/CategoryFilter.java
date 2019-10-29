package rankstop.steeringit.com.rankstop.data.model.network;

import java.io.Serializable;
import java.util.List;

import rankstop.steeringit.com.rankstop.data.model.db.Category;

public class CategoryFilter implements Serializable {

    private Category category;
    private List<LocationFilter> location;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<LocationFilter> getLocation() {
        return location;
    }

    public void setLocation(List<LocationFilter> location) {
        this.location = location;
    }
}
