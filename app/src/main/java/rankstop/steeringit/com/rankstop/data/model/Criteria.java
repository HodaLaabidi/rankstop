package rankstop.steeringit.com.rankstop.data.model;

public class Criteria {

    private String name, _id;

    public Criteria(String name, String _id) {
        this.name = name;
        this._id = _id;
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
}
