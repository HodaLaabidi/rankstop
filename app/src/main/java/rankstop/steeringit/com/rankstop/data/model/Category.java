package rankstop.steeringit.com.rankstop.data.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Arrays;

public class Category implements Serializable {

    private String name, _id;
    private boolean location;
    private Object criterias;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean isLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    public Object getCriterias() {

        if (criterias instanceof String[]) {
            return Arrays.asList((String[]) criterias);
        }else {
            Criteria[] criteriasArray = new Gson().fromJson(new Gson().toJson(criterias), Criteria[].class);
            return Arrays.asList(criteriasArray);
        }
    }

    public void setCriterias(Object[] criterias) {
        this.criterias = criterias;
    }
}
