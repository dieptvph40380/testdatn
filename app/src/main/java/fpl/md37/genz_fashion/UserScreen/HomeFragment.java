package fpl.md37.genz_fashion.UserScreen;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import fpl.md37.genz_fashion.adapter.AdapterTypeProduct;
import fpl.md37.genz_fashion.adapter.AdapterTypeProductUser;
import fpl.md37.genz_fashion.handel.Item_Handel_click;
import fpl.md37.genz_fashion.handel.Item_Handle_MyWishlist;
import fpl.md37.genz_fashion.models.FavouriteResponseBody;
import fpl.md37.genz_fashion.models.Response;
import fpl.md37.genz_fashion.models.TypeProduct;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.genz_fashion.R;
import com.example.genz_fashion.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fpl.md37.genz_fashion.adapter.AdapterProductUser;
import fpl.md37.genz_fashion.adapter.CategoryAdapter;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.models.Product;

public class HomeFragment extends Fragment implements Item_Handel_click, Item_Handle_MyWishlist {

    private ArrayList<TypeProduct> typeProducts;
    private FragmentHomeBinding binding;
    private CountDownTimer countDownTimer;
    private HttpRequest httpRequest;
    private ArrayList<Product> productList = new ArrayList<>();  // Empty list initialization
    private RecyclerView rcv, rcv2;
    private AdapterTypeProductUser adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcv2 = binding.rvTypes;
        rcv = binding.rvItems;
        httpRequest = new HttpRequest();

        fetchProducts();
        fetchTypeProducts();

        // Initialize image slider
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner3, ScaleTypes.FIT));
        binding.slide.setImageList(slideModels, ScaleTypes.FIT);

        startCountdownTimer(2 * 60 * 60 * 1000); // 2 hours
    }

    private void setupRecyclerView(ArrayList<Product> products) {
        if (products == null) {
            products = new ArrayList<>();  // Initialize empty list if null
        }
        AdapterProductUser adapter = new AdapterProductUser(getContext(), products, this);
        rcv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rcv.setAdapter(adapter);
    }

    private void setupRecyclerView2(ArrayList<TypeProduct> ds) {
        adapter = new AdapterTypeProductUser(getContext(), ds, this);
        rcv2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rcv2.setAdapter(adapter);
    }

    private void filterProductsByType(String typeId) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        if (productList != null) {
            for (Product product : productList) {
                if (Objects.equals(product.getTypeProductId(), typeId)) {
                    filteredProducts.add(product);
                }
            }
        }

        if (filteredProducts.isEmpty() && isAdded()) {
            Toast.makeText(requireContext(), "No Product", Toast.LENGTH_SHORT).show();
        }

        setupRecyclerView(filteredProducts);
    }

    private void fetchTypeProducts() {
        httpRequest.callApi().getAlltypeproduct().enqueue(new Callback<Response<ArrayList<TypeProduct>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<TypeProduct>>> call, retrofit2.Response<Response<ArrayList<TypeProduct>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    typeProducts = response.body().getData();
                    if (typeProducts != null) {
                        for (TypeProduct typeProduct : typeProducts) {
                            Log.d("TypeProduct", "ID: " + typeProduct.getId() + ", Name: " + typeProduct.getImage() +
                                    ", Sizes: " + (typeProduct.getSizes() != null ? typeProduct.getSizes().size() : "null"));
                        }
                    }
                    setupRecyclerView2(typeProducts);
                } else {
                    Log.e("FetchTypeProducts", "Failed response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<TypeProduct>>> call, Throwable t) {
                Log.e("FetchTypeProducts", "Error: " + t.getMessage());
            }
        });
    }

    private void fetchProducts() {
        httpRequest.callApi().getAllProducts().enqueue(new Callback<Response<ArrayList<Product>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Product>>> call, retrofit2.Response<Response<ArrayList<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body().getData();
                    if (productList == null) {
                        productList = new ArrayList<>();
                    }
                    setupRecyclerView(productList);
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Error fetching products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Product>>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startCountdownTimer(long duration) {
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int hours = (int) (millisUntilFinished / (1000 * 60 * 60));
                int minutes = (int) (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                int seconds = (int) ((millisUntilFinished % (1000 * 60)) / 1000);

                binding.tvHours.setText(String.format("%02d", hours));
                binding.tvMinutes.setText(String.format("%02d", minutes));
                binding.tvSeconds.setText(String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                binding.tvHours.setText("00");
                binding.tvMinutes.setText("00");
                binding.tvSeconds.setText("00");
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null; // Avoid memory leaks
    }

    @Override
    public void onTypeProductClick(String typeId) {
        filterProductsByType(typeId);
    }

    @Override
    public void addToFavourite(String userId, Product product) {
        FavouriteResponseBody request=new FavouriteResponseBody(userId,product.getId());

        httpRequest.callApi().addToFavourite(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(getContext(), "Add to favourite successfully", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        // Xử lý khi thêm vào giỏ hàng không thành công
                        String errorBody = response.errorBody().string(); // Get error message from error body

                        Toast.makeText( getContext(), "Failed to add to Favourite: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e("API Error", "IOException: " + e.getMessage());
                        Toast.makeText( getContext(), "Failed to add to Favourite: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("...","onFailure"+t.getMessage());
            }
        });
    }
}
