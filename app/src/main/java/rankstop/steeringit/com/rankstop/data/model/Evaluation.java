package rankstop.steeringit.com.rankstop.data.model;

class Evaluation {
    private String date;
    private Comment[] comments;
    private Picture[] pictures;
    private CriteriaNote[] evalCriterias;

    public Evaluation(String date, Comment[] comments, Picture[] pictures, CriteriaNote[] evalCriterias) {
        this.date = date;
        this.comments = comments;
        this.pictures = pictures;
        this.evalCriterias = evalCriterias;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Comment[] getComments() {
        return comments;
    }

    public void setComments(Comment[] comments) {
        this.comments = comments;
    }

    public Picture[] getPictures() {
        return pictures;
    }

    public void setPictures(Picture[] pictures) {
        this.pictures = pictures;
    }

    public CriteriaNote[] getEvalCriterias() {
        return evalCriterias;
    }

    public void setEvalCriterias(CriteriaNote[] evalCriterias) {
        this.evalCriterias = evalCriterias;
    }
}
