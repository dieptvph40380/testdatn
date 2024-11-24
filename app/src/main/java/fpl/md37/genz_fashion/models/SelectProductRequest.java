package fpl.md37.genz_fashion.models;

import java.util.List;

public class SelectProductRequest {
    private String userId;
    private List<String> selectedProductIds;

    public SelectProductRequest(String userId, List<String> selectedProductIds) {
        this.userId = userId;
        this.selectedProductIds = selectedProductIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getSelectedProductIds() {
        return selectedProductIds;
    }

    public void setSelectedProductIds(List<String> selectedProductIds) {
        this.selectedProductIds = selectedProductIds;
    }
}

