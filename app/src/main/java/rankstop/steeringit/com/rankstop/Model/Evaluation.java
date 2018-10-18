package rankstop.steeringit.com.rankstop.Model;

class Evaluation {
    private String date;
    private Comment[] comments;
    private Picture[] pictures;
    private Criteria[] evalCriterias;

    public Evaluation(String date, Comment[] comments, Picture[] pictures, Criteria[] evalCriterias) {
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

    public Criteria[] getEvalCriterias() {
        return evalCriterias;
    }

    public void setEvalCriterias(Criteria[] evalCriterias) {
        this.evalCriterias = evalCriterias;
    }
}
