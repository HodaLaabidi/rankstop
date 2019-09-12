package com.steeringit.rankstop.data.model.db;

import java.io.Serializable;
import java.util.List;

public class Evaluation implements Serializable {
    private String date;
    private List<CriteriaEval> evalCriterias;
    private String _id, userId, itemId;
    private double noteEval;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<CriteriaEval> getEvalCriterias() {
        return evalCriterias;
    }

    public void setEvalCriterias(List<CriteriaEval> evalCriterias) {
        this.evalCriterias = evalCriterias;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public double getNoteEval() {
        return noteEval;
    }

    public void setNoteEval(double noteEval) {
        this.noteEval = noteEval;
    }
}
