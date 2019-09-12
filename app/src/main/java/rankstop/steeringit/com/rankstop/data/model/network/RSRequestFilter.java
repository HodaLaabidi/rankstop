package com.steeringit.rankstop.data.model.network;

import java.io.Serializable;
import java.util.List;

public class RSRequestFilter implements Serializable {

    private String userId, catId, lang;
    private String[] codeCountry;
    private List<String> city;
    private int perPage, page;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String[] getCodeCountry() {
        return codeCountry;
    }

    public void setCodeCountry(String[] codeCountry) {
        this.codeCountry = codeCountry;
    }

    public List<String> getCity() {
        return city;
    }

    public void setCity(List<String> city) {
        this.city = city;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
