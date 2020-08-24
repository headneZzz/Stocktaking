package ru.gosarcho.stocktaking.model;

import java.io.Serializable;
import java.util.Date;

public class Action implements Serializable {
    private String action;
    private String actionee;
    private Date actionDate;

    public Action(String action, String actionee, Date actionDate) {
        this.action = action;
        this.actionee = actionee;
        this.actionDate = actionDate;
    }

    public Action() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionee() {
        return actionee;
    }

    public void setActionee(String actionee) {
        this.actionee = actionee;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }
}
