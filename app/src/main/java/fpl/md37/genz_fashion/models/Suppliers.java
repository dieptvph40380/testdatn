package fpl.md37.genz_fashion.models;

import com.google.gson.annotations.SerializedName;

public class Suppliers {
    @SerializedName("_id")
    private String id;
    private String name;
    private String phone;
    private String email;
    private String description;
    @SerializedName("image")
    private String image;

    public Suppliers() {
    }

    public Suppliers(String id, String name, String phone, String email, String description, String image) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.description = description;
        this.image = image;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
