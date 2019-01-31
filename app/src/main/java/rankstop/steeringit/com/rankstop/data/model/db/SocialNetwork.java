package rankstop.steeringit.com.rankstop.data.model.db;

import java.io.Serializable;

public class SocialNetwork implements Serializable {

    private String userid, name;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
