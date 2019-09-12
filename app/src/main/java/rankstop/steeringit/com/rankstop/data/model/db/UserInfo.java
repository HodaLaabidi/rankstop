package com.steeringit.rankstop.data.model.db;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private User user;
    private int countComments, countPictures, countEval;

    public UserInfo(User user) {
        this.user = user;
    }

    public UserInfo() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCountComments() {
        return countComments;
    }

    public void setCountComments(int countComments) {
        this.countComments = countComments;
    }

    public int getCountPictures() {
        return countPictures;
    }

    public void setCountPictures(int countPictures) {
        this.countPictures = countPictures;
    }

    public int getCountEval() {
        return countEval;
    }

    public void setCountEval(int countEval) {
        this.countEval = countEval;
    }
}
