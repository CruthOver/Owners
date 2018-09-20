package com.wiradipa.fieldOwners.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FieldTariff {

    @SerializedName("start_day")
    @Expose
    private int start_day;

    @SerializedName("end_day")
    @Expose
    private int end_day;

    @SerializedName("start_hour")
    @Expose
    private int start_hour;

    @SerializedName("end_hour")
    @Expose
    private int end_hour;

    @SerializedName("tariff")
    @Expose
    private String tariff;

    public FieldTariff(int start_day, int end_day, int start_hour, int end_hour, String tariff) {
        this.start_day = start_day;
        this.end_day = end_day;
        this.start_hour = start_hour;
        this.end_hour = end_hour;
        this.tariff = tariff;
    }

    public FieldTariff() {
    }

    public void setStartDay(int start_day) {
        this.start_day = start_day;
    }

    public void setEndDay(int end_day) {
        this.end_day = end_day;
    }

    public void setStartHour(int start_hour) {
        this.start_hour = start_hour;
    }

    public void setEndHour(int end_hour) {
        this.end_hour = end_hour;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public int getStartDay() {
        return start_day;
    }

    public int getEndDay() {
        return end_day;
    }

    public int getStartHour() {
        return start_hour;
    }

    public int getEndHour() {
        return end_hour;
    }

    public String getTariff() {
        return tariff;
    }
}
