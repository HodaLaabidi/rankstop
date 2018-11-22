package rankstop.steeringit.com.rankstop.data.model;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable {
    private String scoreItem;
    private List<Comment> comments;
    private int bad, good, neutral;
    private ItemDetails itemDetails;
    private List<Picture> pictures;
    private List<CriteriaNote> tabCritereDetails;
    private Evaluation lastEvalUser;
    private Evaluation myEval;
    private int numberEval;
    private boolean isFollow;

    public Evaluation getMyEval() {
        return myEval;
    }

    public void setMyEval(Evaluation myEval) {
        this.myEval = myEval;
    }

    public String getScoreItem() {
        return scoreItem;
    }

    public void setScoreItem(String scoreItem) {
        this.scoreItem = scoreItem;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<CriteriaNote> getTabCritereDetails() {
        return tabCritereDetails;
    }

    public void setTabCritereDetails(List<CriteriaNote> tabCritereDetails) {
        this.tabCritereDetails = tabCritereDetails;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }
}
