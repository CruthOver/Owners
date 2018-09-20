package com.wiradipa.fieldOwners.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseListFacilities {

    @SerializedName("data")
    private List<ListFacilities> listFacilities;

    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    public void setFieldTypes(List<ListFacilities> listFacilities) {
        this.listFacilities = listFacilities;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public boolean isError(){
        return error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ListFacilities> getListFacilities() {
        return listFacilities;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return
                "ResponseListFacilities{" +
                        "listFacilities = '" + listFacilities + '\'' +
                        ",error = '" + error + '\'' +
                        ",message = '" + message + '\'' +
                        "}";
    }
}
