package fpl.md37.genz_fashion.models;

import com.google.gson.annotations.SerializedName;

public class ProducItem {
    @SerializedName("_id")
    private String id;
    @SerializedName("productId")
    private Product productId;

    @SerializedName("sizeId")
    private Size  sizeId;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("isSelected")
    private boolean isSelected;

    public ProducItem(Product productId, Size  sizeId, int quantity, boolean isSelected) {
        this.productId = productId;
        this.sizeId = sizeId;
        this.quantity = quantity;
        this.isSelected = isSelected;
    }

    public ProducItem(String id, Product productId, Size sizeId, int quantity, boolean isSelected) {
        this.id = id;
        this.productId = productId;
        this.sizeId = sizeId;
        this.quantity = quantity;
        this.isSelected = isSelected;
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

    public Size  getSizeId() {
        return sizeId;
    }

    public void setSizeId(Size  sizeId) {
        this.sizeId = sizeId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
