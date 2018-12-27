package rankstop.steeringit.com.rankstop.data.model.custom;

import android.net.Uri;

import java.io.Serializable;
import java.util.List;

import rankstop.steeringit.com.rankstop.data.model.CriteriaEval;

public class RSAddReview implements Serializable {
    private String userId, categoryId, description, title, address, latitude, longitude, comment, itemId, evalId, country, governorate, city, phone;
    private List<CriteriaEval> evalCri;
    private List<Uri> files;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<CriteriaEval> getEvalCri() {
        return evalCri;
    }

    public void setEvalCri(List<CriteriaEval> evalCri) {
        this.evalCri = evalCri;
    }

    public List<Uri> getFiles() {
        return files;
    }

    public void setFiles(List<Uri> files) {
        this.files = files;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getEvalId() {
        return evalId;
    }

    public void setEvalId(String evalId) {
        this.evalId = evalId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


}
