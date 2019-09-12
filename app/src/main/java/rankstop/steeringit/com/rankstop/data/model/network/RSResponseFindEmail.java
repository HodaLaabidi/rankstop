package com.steeringit.rankstop.data.model.network;

import java.io.Serializable;

public class RSResponseFindEmail implements Serializable {
    private boolean connectSocialMedia;

    public RSResponseFindEmail(boolean connectSocialMedia) {
        this.connectSocialMedia = connectSocialMedia;
    }

    public RSResponseFindEmail() {
    }

    public boolean isConnectSocialMedia() {
        return connectSocialMedia;
    }

    public void setConnectSocialMedia(boolean connectSocialMedia) {
        this.connectSocialMedia = connectSocialMedia;
    }
}
