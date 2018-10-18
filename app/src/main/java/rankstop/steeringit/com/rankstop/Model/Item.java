package rankstop.steeringit.com.rankstop.Model;

import java.io.Serializable;

public class Item implements Serializable {
    private String scoreItem;
    private Comment[] comments;
    private int bad, good, neutral;
    private ItemDetails itemDetails;
    private Picture[] pictures;
    private Criteria[] tabCritereDetails;
    private Evaluation lastEvalUser;

    public Item() {
    }

    public Item(String scoreItem, Comment[] comments, int bad, int good, int neutral, ItemDetails itemDetails, Picture[] pictures, Criteria[] tabCritereDetails, Evaluation lastEvalUser) {
        this.scoreItem = scoreItem;
        this.comments = comments;
        this.bad = bad;
        this.good = good;
        this.neutral = neutral;
        this.itemDetails = itemDetails;
        this.pictures = pictures;
        this.tabCritereDetails = tabCritereDetails;
        this.lastEvalUser = lastEvalUser;
    }

    public String getScoreItem() {
        return scoreItem;
    }

    public void setScoreItem(String scoreItem) {
        this.scoreItem = scoreItem;
    }

    public Comment[] getComments() {
        return comments;
    }

    public void setComments(Comment[] comments) {
        this.comments = comments;
    }

    public int getBad() {
        return bad;
    }

    public void setBad(int bad) {
        this.bad = bad;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getNeutral() {
        return neutral;
    }

    public void setNeutral(int neutral) {
        this.neutral = neutral;
    }

    public ItemDetails getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(ItemDetails itemDetails) {
        this.itemDetails = itemDetails;
    }

    public Picture[] getPictures() {
        return pictures;
    }

    public void setPictures(Picture[] pictures) {
        this.pictures = pictures;
    }

    public Criteria[] getTabCritereDetails() {
        return tabCritereDetails;
    }

    public void setTabCritereDetails(Criteria[] tabCritereDetails) {
        this.tabCritereDetails = tabCritereDetails;
    }

    public Evaluation getLastEvalUser() {
        return lastEvalUser;
    }

    public void setLastEvalUser(Evaluation lastEvalUser) {
        this.lastEvalUser = lastEvalUser;
    }
}
