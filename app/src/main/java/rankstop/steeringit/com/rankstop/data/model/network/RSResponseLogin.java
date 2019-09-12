package com.steeringit.rankstop.data.model.network;

import java.io.Serializable;

public class RSResponseLogin implements Serializable {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
