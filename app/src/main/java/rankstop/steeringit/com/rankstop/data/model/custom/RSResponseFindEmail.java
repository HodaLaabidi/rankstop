package rankstop.steeringit.com.rankstop.data.model.custom;

public class RSResponseFindEmail {
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
