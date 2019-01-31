package rankstop.steeringit.com.rankstop.data.model.network;

import java.io.Serializable;
import java.util.List;

import rankstop.steeringit.com.rankstop.data.model.db.Comment;
import rankstop.steeringit.com.rankstop.data.model.db.Picture;

public class RSResponseItemData implements Serializable {
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
