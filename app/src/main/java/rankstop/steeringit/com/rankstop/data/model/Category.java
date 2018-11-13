package rankstop.steeringit.com.rankstop.data.model;

public class Category {

    private String name, _id;
    private String[] criterias;

    public Category(String name, String _id, String[] criterias) {
        this.name = name;
        this._id = _id;
        this.criterias = criterias;
    }

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

    public String[] getCriterias() {
        return criterias;
    }

    public void setCriterias(String[] criterias) {
        this.criterias = criterias;
    }
}
