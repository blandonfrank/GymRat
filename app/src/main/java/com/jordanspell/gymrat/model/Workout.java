package com.jordanspell.gymrat.model;

import java.util.Date;

/**
 * Created by jorda_000 on 2/13/2015.
 */
public class Workout{

    private int id;
    private Date date;
    private String rotation;

    public Workout() {

    }

    public Workout(String rotation) {
        super();
        this.rotation = rotation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRotation() {
        return rotation;
    }

    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    public String[] getExportableData() {
        return (Integer.toString(this.id) + "," + this.date.toString() + "," + this.rotation).split(",");
    }
}
