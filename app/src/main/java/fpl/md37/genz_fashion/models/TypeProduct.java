package fpl.md37.genz_fashion.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TypeProduct {
    @SerializedName("_id")
    private String id;
    private String name;
    @SerializedName("image")
    private String image;
    @SerializedName("id_size")
    private List<Size> sizes;

    public TypeProduct(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public TypeProduct(String id, String name, String image, List<Size> sizes) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.sizes = sizes;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public void setSizes(List<Size> sizes) {
        this.sizes = sizes;
    }
}
