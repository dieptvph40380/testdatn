package fpl.md37.genz_fashion.handel;

import fpl.md37.genz_fashion.models.Favourite;
import fpl.md37.genz_fashion.models.Product;


public interface Item_Handle_MyWishlist {
    public void addToFavourite(String userId, Product product);
}
