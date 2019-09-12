package com.steeringit.rankstop.data.model.network;

import java.io.Serializable;

public class RSRequestReportAbuse implements Serializable {

    private String userId, itemId, abuseId;

    public RSRequestReportAbuse(String userId, String itemId, String abuseId) {
        this.userId = userId;
        this.itemId = itemId;
        this.abuseId = abuseId;
    }

    public RSRequestReportAbuse() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getAbuseId() {
        return abuseId;
    }

    public void setAbuseId(String abuseId) {
        this.abuseId = abuseId;
    }
}
