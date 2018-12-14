package rankstop.steeringit.com.rankstop.data.model;

import java.io.Serializable;

public class Localisation implements Serializable {

    private String address;
    private float longitude, latitude;

    public Localisation(String address, float longitude, float latitude) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
}
