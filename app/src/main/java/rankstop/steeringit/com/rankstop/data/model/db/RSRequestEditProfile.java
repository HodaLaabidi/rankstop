package com.steeringit.rankstop.data.model.db;

import android.net.Uri;

import java.io.Serializable;

public class RSRequestEditProfile implements Serializable {

    private String lastName, firstName, gender, phone, birthDate, username, nameToUse, oldPassword, newPassword, userId;
    private Uri file;
    private String countryCode,countryName,city;
    private RSAddress location;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public RSAddress getLocation() {
        return location;
    }

    public void setLocation(String city, String countryCode, String countryName) {
        this.location = new RSAddress(city, countryCode, countryName);
    }

    public String getNameToUse() {
        return nameToUse;
    }

    public void setNameToUse(String nameToUse) {
        this.nameToUse = nameToUse;
    }

    public Uri getFile() {
        return file;
    }

    public void setFile(Uri file) {
        this.file = file;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
