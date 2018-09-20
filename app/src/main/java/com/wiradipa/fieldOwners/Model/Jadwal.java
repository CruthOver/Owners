package com.wiradipa.fieldOwners.Model;

public class Jadwal {
    public String startHour,endHours, date;
    public int status;

    public Jadwal() { }

    public Jadwal(String startHour, String endHours, String date, int status) {
        this.startHour = startHour;
        this.endHours = endHours;
        this.date = date;
        this.status = status;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHours() {
        return endHours;
    }

    public void setEndHours(String endHours) {
        this.endHours = endHours;
    }

    public String getStrip() {
        return date;
    }

    public void setStrip(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
