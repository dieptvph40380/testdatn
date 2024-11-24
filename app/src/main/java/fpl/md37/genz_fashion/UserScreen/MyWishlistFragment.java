package fpl.md37.genz_fashion.UserScreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genz_fashion.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import fpl.md37.genz_fashion.adapter.AdapterCart;
import fpl.md37.genz_fashion.adapter.AdapterFavourite;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.handel.Item_Handel_delete;
import fpl.md37.genz_fashion.models.CartData;
import fpl.md37.genz_fashion.models.Favourite;
import fpl.md37.genz_fashion.models.FavouriteItem;
import fpl.md37.genz_fashion.models.ProducItem;
import fpl.md37.genz_fashion.models.RemoveFavouriteRequest;
import fpl.md37.genz_fashion.models.ResponseCart;
import fpl.md37.genz_fashion.models.ResponseFavourite;
import fpl.md37.genz_fashion.models.UpdateQuantityRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyWishlistFragment extends Fragment implements Item_Handel_delete {
    private RecyclerView recyclerView;
    private AdapterFavourite adapter;
    private HttpRequest httpRequest;
    private ImageView btn_back;
    private List<FavouriteItem> products;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_wishlist, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewWishlist);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new AdapterFavourite(getContext(),this);
        recyclerView.setAdapter(adapter);
        httpRequest = new HttpRequest();

        // Get current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("MyWishlistFragment", "Fetching favourites for user: " + userId);
            httpRequest.callApi().getFavourite(userId).enqueue(getFavourite); // API call to get favourites
        } else {
            Log.e("MyWishlistFragment", "No user logged in");
        }

        return view;
    }

    // Callback for fetching favourite items
    Callback<ResponseFavourite> getFavourite = new Callback<ResponseFavourite>() {
        @Override
        public void onResponse(Call<ResponseFavourite> call, Response<ResponseFavourite> response) {
            if (response.isSuccessful()) {
                // Handle the response
                if (response.body() != null) {
                    ResponseFavourite favouriteResponse = response.body();
                    List<FavouriteItem> favouriteItems = favouriteResponse.getData().getProducts();
                    if (favouriteItems != null && !favouriteItems.isEmpty()) {
                        products = favouriteItems;
                        adapter.setProducts(products); // Update the adapter with the new data
                        Log.d("MyWishlistFragment", "Favourite list fetched successfully. Items: " + favouriteItems.size());
                    } else {
                        Log.d("MyWishlistFragment", "No favourite items found.");
                        Toast.makeText(getContext(), "No items in your wishlist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("MyWishlistFragment", "Empty response body");
                    Toast.makeText(getContext(), "Failed to fetch favourites", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("MyWishlistFragment", "Failed response: " + response.code());
                Toast.makeText(getContext(), "Failed to fetch favourites, error: " + response.code(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ResponseFavourite> call, Throwable t) {
            // Log error message and show toast
            Log.e("MyWishlistFragment", "Network error: " + t.getMessage());
            Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void removeFromFavourite(String userId, String productId) {
        Log.d("MyWishlistFragment", "Removing product from favourites");
        Log.d("MyWishlistFragment", "User ID: " + userId);
        Log.d("MyWishlistFragment", "Product ID: " + productId);

        // Gửi yêu cầu API để xóa sản phẩm khỏi danh sách yêu thích
        RemoveFavouriteRequest request = new RemoveFavouriteRequest(userId, productId);
        httpRequest.callApi().removeFavourite(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    httpRequest.callApi().getFavourite(userId).enqueue(getFavourite);
                    Toast.makeText(getContext(), "Removed from favourites", Toast.LENGTH_SHORT).show();
                    Log.d("MyWishlistFragment", "Product removed successfully from wishlist");
                } else {
                    // Xử lý khi xóa thất bại
                    Log.e("MyWishlistFragment", "Error removing from favourites: " + response.code());
                    Toast.makeText(getContext(), "Failed to remove from favourites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Xử lý khi có lỗi trong quá trình kết nối với API
                Log.e("MyWishlistFragment", "Error: " + t.getMessage());
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
