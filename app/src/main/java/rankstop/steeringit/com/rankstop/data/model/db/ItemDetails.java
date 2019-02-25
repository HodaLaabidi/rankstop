package rankstop.steeringit.com.rankstop.data.model.db;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class ItemDetails implements Serializable {

    private Object category;
    private String createdAt, lastDateView, updatedAt, phone;
    private String title, description, urlFacebook, urlInstagram, urlGooglePlus, urlLinkedIn, urlTwitter, _id;
    private Object creator, owner;
    private int evaluations, numberView;
    private List<Gallery> gallery;
    private Localisation location;

    public ItemDetails(Category category, String createdAt, String lastDateView, String updatedAt, String title, String description, String urlFacebook, String urlGooglePlus, String urlInstagram, String urlLinkedIn, String urlTwitter, String _id, Object creator, Object owner, int evaluations, int numberView) {
        this.category = category;
        this.createdAt = createdAt;
        this.lastDateView = lastDateView;
        this.updatedAt = updatedAt;
        this.title = title;
        this.description = description;
        this.urlFacebook = urlFacebook;
        this.urlGooglePlus = urlGooglePlus;
        this.urlTwitter = urlTwitter;
        this.urlLinkedIn = urlLinkedIn;
        this.urlInstagram = urlInstagram;
        this._id = _id;
        this.creator = creator;
        this.owner = owner;
        this.evaluations = evaluations;
        this.numberView = numberView;
    }

    public Object getCategory() {
        if (category instanceof String)
            return (String)category;
        else
            return new Gson().fromJson(new Gson().toJson(category), Category.class);
    }

    public void setCategory(Object category) {
        this.category = category;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastDateView() {
        return lastDateView;
    }

    public void setLastDateView(String lastDateView) {
        this.lastDateView = lastDateView;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlFacebook() {
        return urlFacebook;
    }

    public void setUrlFacebook(String urlFacebook) {
        this.urlFacebook = urlFacebook;
    }

    public String getUrlGooglePlus() {
        return urlGooglePlus;
    }

    public void setUrlGooglePlus(String urlGooglePlus) {
        this.urlGooglePlus = urlGooglePlus;
    }

    public String getUrlTwitter() {
        return urlTwitter;
    }

    public void setUrlTwitter(String urlTwitter) {
        this.urlTwitter = urlTwitter;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Object getCreator() {
        if (creator instanceof String)
            return (String)creator;
        else
            return new Gson().fromJson(new Gson().toJson(creator), User.class);
    }

    public void setCreator(Object creator) {
        if (creator instanceof String)
            this.creator = (String)creator;
        else
            this.creator = (User)creator;
    }

    public Object getOwner() {
        if (owner instanceof String)
            return (String)owner;
        else
            return new Gson().fromJson(new Gson().toJson(owner), User.class);
    }

    public void setOwner(Object owner) {
        if (owner instanceof String)
            this.owner = (String)owner;
        else
            this.owner = (User)owner;
    }

    public int getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(int evaluations) {
        this.evaluations = evaluations;
    }

    public int getNumberView() {
        return numberView;
    }

    public void setNumberView(int numberView) {
        this.numberView = numberView;
    }

    public List<Gallery> getGallery() {
        return gallery;
    }

    public void setGallery(List<Gallery> gallery) {
        this.gallery = gallery;
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

    public Localisation getLocation() {
        return location;
    }

    public void setLocation(Localisation location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
