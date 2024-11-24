package fpl.md37.genz_fashion.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartData {
    @SerializedName("_id")
    private String id;
    @SerializedName("userId")
    private String userId;

    @SerializedName("products")
    private List<ProducItem> products;

    @SerializedName("voucher")
    private String voucher;

    @SerializedName("totalPrice")
    private double totalPrice;

    public CartData() {
    }

    public CartData(String id, String userId, List<ProducItem> products, String voucher, double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.products = products;
        this.voucher = voucher;
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ProducItem> getProducts() {
        return products;
    }

    public void setProducts(List<ProducItem> products) {
        this.products = products;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
