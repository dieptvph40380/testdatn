package fpl.md37.genz_fashion.models;

import com.google.gson.annotations.SerializedName;

public class Size {
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;


    public Size(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Size(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
