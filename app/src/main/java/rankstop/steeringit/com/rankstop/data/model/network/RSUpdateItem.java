package rankstop.steeringit.com.rankstop.data.model.network;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RSUpdateItem implements Serializable {

    private String itemId, urlFacebook, urlTwitter, urlGooglePlus, urlInstagram, urlLinkedIn;
    private List<Uri> gallery;
    private ArrayList<String> picDelete;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUrlFacebook() {
        return urlFacebook;
    }

    public void setUrlFacebook(String urlFacebook) {
        this.urlFacebook = urlFacebook;
    }

    public String getUrlTwitter() {
        return urlTwitter;
    }

    public void setUrlTwitter(String urlTwitter) {
        this.urlTwitter = urlTwitter;
    }

    public String getUrlGooglePlus() {
        return urlGooglePlus;
    }

    public void setUrlGooglePlus(String urlGooglePlus) {
        this.urlGooglePlus = urlGooglePlus;
    }

    public String getUrlInstagram() {
        return urlInstagram;
    }

    public void setUrlInstagram(String urlInstagram) {
        this.urlInstagram = urlInstagram;
    }

    public String getUrlLinkedIn() {
        return urlLinkedIn;
    }

    public void setUrlLinkedIn(String urlLinkedIn) {
        this.urlLinkedIn = urlLinkedIn;
    }

    public List<Uri> getGallery() {
        return gallery;
    }

    public void setGallery(List<Uri> gallery) {
        this.gallery = gallery;
    }

    public ArrayList<String> getPicDelete() {
        return picDelete;
    }

    public void setPicDelete(ArrayList<String> picDelete) {
        this.picDelete = picDelete;
    }
}
