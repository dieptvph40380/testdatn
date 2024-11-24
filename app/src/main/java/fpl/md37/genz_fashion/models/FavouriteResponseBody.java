package fpl.md37.genz_fashion.models;

public class FavouriteResponseBody {
    private String userId;
    private String productId;

    public FavouriteResponseBody() {
    }

    public FavouriteResponseBody(String userId, String productId) {
        this.userId = userId;
        this.productId = productId;
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
}
