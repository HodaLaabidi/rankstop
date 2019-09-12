package com.steeringit.rankstop.data.model.db;

import java.io.Serializable;

public class CriteriaNote implements Serializable {
    private String idCritere, nameCritere, crit_good, crit_neutral, crit_bad;

    public CriteriaNote(String idCritere, String nameCritere, String crit_good, String crit_neutral, String crit_bad) {
        this.idCritere = idCritere;
        this.nameCritere = nameCritere;
        this.crit_good = crit_good;
        this.crit_neutral = crit_neutral;
        this.crit_bad = crit_bad;
    }

    public String getIdCritere() {
        return idCritere;
    }

    public void setIdCritere(String idCritere) {
        this.idCritere = idCritere;
    }

    public String getNameCritere() {
        return nameCritere;
    }

    public void setNameCritere(String nameCritere) {
        this.nameCritere = nameCritere;
    }

    public String getCrit_good() {
        return crit_good;
    }

    public void setCrit_good(String crit_good) {
        this.crit_good = crit_good;
    }

    public String getCrit_neutral() {
        return crit_neutral;
    }

    public void setCrit_neutral(String crit_neutral) {
        this.crit_neutral = crit_neutral;
    }

    public String getCrit_bad() {
        return crit_bad;
    }

    public void setCrit_bad(String crit_bad) {
        this.crit_bad = crit_bad;
    }
}
