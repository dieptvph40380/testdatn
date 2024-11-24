package fpl.md37.genz_fashion.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SizeQuantity implements Serializable {
    @SerializedName("sizeId")
    private String sizeId;

    @SerializedName("quantity")
    private String quantity;

    public SizeQuantity() {
    }

    public SizeQuantity(String sizeId, String quantity) {
        this.sizeId = sizeId;
        this.quantity = quantity;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

