package com.steeringit.rankstop.data.model.network;

import java.io.Serializable;

import com.steeringit.rankstop.data.model.db.UserInfo;

public class RSLocalStorage implements Serializable {

    private String token;
    private UserInfo userInfo;

    public RSLocalStorage(String token, UserInfo userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }

    public RSLocalStorage() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
