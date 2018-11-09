package com.wiradipa.fieldOwners.Model;

import com.google.gson.annotations.SerializedName;

public class ListVenue {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public ListVenue(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ListVenue){
            ListVenue c = (ListVenue) obj;
            if(c.getName().equals(name) && c.getId()== id ) return true;
        }

        return false;
    }
}
