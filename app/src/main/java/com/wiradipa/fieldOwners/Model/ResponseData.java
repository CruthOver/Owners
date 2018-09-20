package com.wiradipa.fieldOwners.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseData {

    @SerializedName("data")
    private List<FieldData> fieldData;

    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    public void setFieldData(List<FieldData> fieldTypes) {
        this.fieldData = fieldTypes;
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

    public List<FieldData> getFieldData() {
        return fieldData;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return
                "ResponseData{" +
                        "fieldData = '" + fieldData + '\'' +
                        ",error = '" + error + '\'' +
                        ",message = '" + message + '\'' +
                        "}";
    }
}
