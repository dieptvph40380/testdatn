package fpl.md37.genz_fashion.handel;

import fpl.md37.genz_fashion.models.ProducItem;

public interface Item_Handel_check {
    void onProductChecked(ProducItem product, boolean isChecked);
    void updateQuantity(String userId, String productId, String sizeId, String action);
    void removeCart(String userId, String productId);
}
