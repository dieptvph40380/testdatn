package fpl.md37.genz_fashion.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Favourite {


    @SerializedName("userId")
    private String userId;

    @SerializedName("products")
    private List<FavouriteItem> products;


    public Favourite(String userId, List<FavouriteItem> products) {
        this.userId = userId;
        this.products = products;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<FavouriteItem> getProducts() {
        return products;
    }

    public void setProducts(List<FavouriteItem> products) {
        this.products = products;
    }
}