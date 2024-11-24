package fpl.md37.genz_fashion.models;

public class UpdateQuantityRequest {
    private String userId;
    private String productId;
    private String sizeId;
    private String action;

    public UpdateQuantityRequest(String userId, String productId, String sizeId, String action) {
        this.userId = userId;
        this.productId = productId;
        this.sizeId = sizeId;
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSizeId() {
        return sizeId;
    }

    public void setSizeId(String sizeId) {
        this.sizeId = sizeId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
