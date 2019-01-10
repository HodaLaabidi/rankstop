package rankstop.steeringit.com.rankstop.data.model.db;

import com.google.gson.Gson;

import java.io.Serializable;

public class CriteriaEval implements Serializable {

    private int note, coefficient;
    private Object criteria;
    private String criteriaName;

    public CriteriaEval(int note, int coefficient, Object criteria) {
        this.note = note;
        this.coefficient = coefficient;
        this.criteria = criteria;
    }

    public CriteriaEval(int note, int coefficient, Object criteria, String criteriaName) {
        this.note = note;
        this.coefficient = coefficient;
        this.criteria = criteria;
        this.criteriaName = criteriaName;
    }

    public String getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        this.criteriaName = criteriaName;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(int coefficient) {
        this.coefficient = coefficient;
    }

    public Object getCriteria() {
        if (criteria instanceof String) {
            return (String) criteria;
        }else {
            return new Gson().fromJson(new Gson().toJson(criteria), Criteria.class);
        }
    }

    public void setCriteria(Object criteria) {
        if (criteria instanceof String)
            this.criteria = (String)criteria;
        else
            this.criteria = (Criteria)criteria;
    }

}
