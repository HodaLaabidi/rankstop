package rankstop.steeringit.com.rankstop.data.model;

import com.google.gson.annotations.SerializedName;

import rankstop.steeringit.com.rankstop.data.model.custom.SocialNetwork;

public class User {

    @SerializedName("_id")
    private String _id;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("address")
    private String address;
    @SerializedName("username")
    private String username;
    @SerializedName("fullname")
    private String fullname;
    @SerializedName("phone")
    private String phone;
    @SerializedName("photo")
    private String photo;
    @SerializedName("gender")
    private String gender;
    @SerializedName("pictureProfile")
    private String pictureProfile;
    @SerializedName("birthDate")
    private String birthDate;
    @SerializedName("facebook")
    private SocialNetwork facebook;
    @SerializedName("linkedin")
    private SocialNetwork linkedin;
    @SerializedName("google")
    private SocialNetwork google;

    public User() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPictureProfile() {
        return pictureProfile;
    }

    public void setPictureProfile(String pictureProfile) {
        this.pictureProfile = pictureProfile;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SocialNetwork getFacebook() {
        return facebook;
    }

    public void setFacebook(SocialNetwork facebook) {
        this.facebook = facebook;
    }

    public SocialNetwork getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(SocialNetwork linkedin) {
        this.linkedin = linkedin;
    }

    public SocialNetwork getGoogle() {
        return google;
    }

    public void setGoogle(SocialNetwork google) {
        this.google = google;
    }
}
