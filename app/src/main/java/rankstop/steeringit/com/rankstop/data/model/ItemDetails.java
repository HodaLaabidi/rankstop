package rankstop.steeringit.com.rankstop.data.model;

public class ItemDetails {

    private Category category;
    private String createdAt, lastDateView, updatedAt;
    private String title, description, urlFacebook, urlGooglePlus, urlTwitter, _id;
    private Object creator, owner;
    private int evaluations, numberView;

    public ItemDetails(Category category, String createdAt, String lastDateView, String updatedAt, String title, String description, String urlFacebook, String urlGooglePlus, String urlTwitter, String _id, Object creator, Object owner, int evaluations, int numberView) {
        this.category = category;
        this.createdAt = createdAt;
        this.lastDateView = lastDateView;
        this.updatedAt = updatedAt;
        this.title = title;
        this.description = description;
        this.urlFacebook = urlFacebook;
        this.urlGooglePlus = urlGooglePlus;
        this.urlTwitter = urlTwitter;
        this._id = _id;
        this.creator = creator;
        this.owner = owner;
        this.evaluations = evaluations;
        this.numberView = numberView;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
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
            return (User) creator;
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
            return (User) owner;
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
}
