package rankstop.steeringit.com.rankstop.data.model.network;

import java.io.Serializable;
import java.util.List;

public class RSResponseCategoryFilter implements Serializable {

    private List<CategoryFilter> categories;

    public List<CategoryFilter> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryFilter> categories) {
        this.categories = categories;
    }
}
