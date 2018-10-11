package com.wiradipa.fieldOwners.Model;

import com.google.gson.annotations.SerializedName;

public class ListFacilities {

    @SerializedName("name")
    private String nama;

    @SerializedName("id")
    private int id;

    public ListFacilities(String nama) {
        this.nama = nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return
                "ListFacilities{" + "name = '" + nama
                        + '\'' + ", id = '" + id + '\'' + "}";
    }
}
