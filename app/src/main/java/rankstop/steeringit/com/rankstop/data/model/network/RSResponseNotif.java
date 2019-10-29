package rankstop.steeringit.com.rankstop.data.model.network;

import java.io.Serializable;
import java.util.List;

import rankstop.steeringit.com.rankstop.data.model.db.RSNotif;

public class RSResponseNotif implements Serializable {

    private int current, pages;
    private List<RSNotif> notification;

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

    public List<RSNotif> getNotification() {
        return notification;
    }

    public void setNotification(List<RSNotif> notification) {
        this.notification = notification;
    }
}
