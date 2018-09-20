package com.wiradipa.fieldOwners.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FasilitasValue {

    @SerializedName("facility_id")
    @Expose
    private int id;

    public FasilitasValue(int id) {
        this.id = id;
    }

    public void setImage(int id) {
        this.id = id;
    }

    public int getImage() {
        return id;
    }

    @Override
    public String toString() {
        return "FasilitasValue{" + "id = '" + id
                + '\'' + "}";
    }
}
