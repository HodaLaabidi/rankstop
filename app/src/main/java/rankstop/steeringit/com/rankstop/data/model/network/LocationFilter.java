package rankstop.steeringit.com.rankstop.data.model.network;

import java.io.Serializable;

import rankstop.steeringit.com.rankstop.data.model.db.Country;

public class LocationFilter implements Serializable {

    private Country country;
    private String[] cities;

    public LocationFilter(Country country) {
        this.country = country;
    }

    public LocationFilter() {
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String[] getCities() {
        return cities;
    }

    public void setCities(String[] cities) {
        this.cities = cities;
    }
}
