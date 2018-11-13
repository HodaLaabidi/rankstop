package rankstop.steeringit.com.rankstop.data.model;

import java.io.Serializable;

public class Item implements Serializable {
    private String scoreItem;
    private Comment[] comments;
    private int bad, good, neutral;
    private ItemDetails itemDetails;
    private Picture[] pictures;
    private CriteriaNote[] tabCritereDetails;
    private Evaluation lastEvalUser;
    private int numberEval;
    private boolean isFollow;

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

    public CriteriaNote[] getTabCritereDetails() {
        return tabCritereDetails;
    }

    public void setTabCritereDetails(CriteriaNote[] tabCritereDetails) {
        this.tabCritereDetails = tabCritereDetails;
    }

    public Evaluation getLastEvalUser() {
        return lastEvalUser;
    }

    public void setLastEvalUser(Evaluation lastEvalUser) {
        this.lastEvalUser = lastEvalUser;
    }

    public int getNumberEval() {
        return numberEval;
    }

    public void setNumberEval(int numberEval) {
        this.numberEval = numberEval;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }
}
