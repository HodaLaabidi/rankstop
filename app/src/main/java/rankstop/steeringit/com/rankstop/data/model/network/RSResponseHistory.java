package com.steeringit.rankstop.data.model.network;

import java.io.Serializable;
import java.util.List;

import com.steeringit.rankstop.data.model.db.History;

public class RSResponseHistory implements Serializable {

    private List<History> stories;
    private int current;
    private int pages;

    public List<History> getStories() {
        return stories;
    }

    public void setStories(List<History> stories) {
        this.stories = stories;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
