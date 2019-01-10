package rankstop.steeringit.com.rankstop.data.model.db;

import java.io.Serializable;

public class RSAddress implements Serializable {

    private String city;
    private Country country;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
