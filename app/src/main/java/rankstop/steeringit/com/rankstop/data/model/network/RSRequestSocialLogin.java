package rankstop.steeringit.com.rankstop.data.model.network;

import java.io.Serializable;

import rankstop.steeringit.com.rankstop.data.model.db.RSAddress;

public class RSRequestSocialLogin implements Serializable {

    private String photoUrl;
    private String email;
    private String provider;
    private String lastName;
    private String firstName;
    private String id;
    private String gender;
    private String birthday;
    private String fcmRegistrationToken;
    private RSAddress location;
    private String language ;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFcmRegistrationToken() {
        return fcmRegistrationToken;
    }

    public void setFcmRegistrationToken(String fcmRegistrationToken) {
        this.fcmRegistrationToken = fcmRegistrationToken;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public RSAddress getLocation() {
        return location;
    }

    public void setLocation(RSAddress location) {
        this.location = location;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String toString() {
        return "id = " + this.id + "photoUrl = " + this.photoUrl + "email = " + this.email + "provider = " + this.provider + "lastName = " + this.lastName + "firstName = " + this.firstName + "gender = " + this.gender + "birthday = " + this.birthday +
               "getCity = " + this.location.getCity() + "getCountry().getCountryCode()= " + this.location.getCountry().getCountryCode() + "getLocation().getCountry().getCountryName() = " + this.getLocation().getCountry().getCountryName() + "getLocation().getCountry().get_id() = " + this.getLocation().getCountry().get_id();
    }
}
