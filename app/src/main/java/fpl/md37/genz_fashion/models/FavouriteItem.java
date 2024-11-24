package fpl.md37.genz_fashion.models;

import com.google.gson.annotations.SerializedName;

public class FavouriteItem {
    @SerializedName("_id")
    private String id;
    @SerializedName("productId")
    private Product productId;

    public FavouriteItem(String id, Product productId) {
        this.id = id;
        this.productId = productId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProductId() {
        return productId;
    }

    public void setProductId(Product productId) {
        this.productId = productId;
    }
}
