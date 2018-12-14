package rankstop.steeringit.com.rankstop.data.model.custom;

import java.util.List;

import rankstop.steeringit.com.rankstop.data.model.Comment;
import rankstop.steeringit.com.rankstop.data.model.Picture;

public class RSResponseItemData {
    private int pages, current;
    private List<Comment> comments;
    private List<Picture> pictures;

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }
}
