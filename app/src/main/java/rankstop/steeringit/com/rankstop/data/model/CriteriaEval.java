package rankstop.steeringit.com.rankstop.data.model;

public class CriteriaEval {

    private int note, coefficient;
    private Object criteria;

    public CriteriaEval(int note, int coefficient, Object criteria) {
        this.note = note;
        this.coefficient = coefficient;
        this.criteria = criteria;
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
        if (criteria instanceof String)
            return (String)criteria;
        else
            return (Criteria) criteria;
    }

    public void setCriteria(Object criteria) {
        if (criteria instanceof String)
            this.criteria = (String)criteria;
        else
            this.criteria = (Criteria)criteria;
    }

}
