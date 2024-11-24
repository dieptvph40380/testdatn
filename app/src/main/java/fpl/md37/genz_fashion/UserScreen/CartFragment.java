package fpl.md37.genz_fashion.UserScreen;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genz_fashion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fpl.md37.genz_fashion.ManagerScreen.SignUpActivity;
import fpl.md37.genz_fashion.ManagerScreen.VoucherFragment;
import fpl.md37.genz_fashion.adapter.AdapterCart;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.handel.Item_Handel_check;
import fpl.md37.genz_fashion.models.CartData;
import fpl.md37.genz_fashion.models.ProducItem;
import fpl.md37.genz_fashion.models.RemoveFavouriteRequest;
import fpl.md37.genz_fashion.models.ResponseCart;
import fpl.md37.genz_fashion.models.SelectProductRequest;
import fpl.md37.genz_fashion.models.UpdateQuantityRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartFragment extends Fragment implements Item_Handel_check {
    private RecyclerView recyclerView;
    private AdapterCart adapter;
    private TextView txtotal, btn_checkout;
    private HttpRequest httpRequest;
    private ImageView btn_back;
    private List<ProducItem> products;
    private boolean isProductSelected = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        LinearLayout voucher = view.findViewById(R.id.click_voucher);
        voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new VoucherFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.bounce_in, R.anim.bounce_out);
                transaction.replace(R.id.frameLayout_cart, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view_cart);
        txtotal = view.findViewById(R.id.total_cart);
        btn_checkout = view.findViewById(R.id.btn_CheckOut);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterCart(getContext(), this);
        recyclerView.setAdapter(adapter);
        httpRequest = new HttpRequest();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            httpRequest.callApi().getCart(userId).enqueue(getCartID);
        }
        btn_checkout.setEnabled(false);
        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isProductSelected) {
                    // Hiển thị thông báo khi không có sản phẩm nào được chọn
                    Toast.makeText(getContext(), "Vui lòng chọn ít nhất một sản phẩm để tiếp tục", Toast.LENGTH_SHORT).show();
                    return; // Dừng lại và không chuyển màn
                }
//                // Tạo một instance mới của Fragment
//                CheckOutFragment newFragment = new CheckOutFragment();
//                // Lấy đối tượng FragmentTransaction để thực hiện thay đổi fragment
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                // Áp dụng hiệu ứng chuyển động khi thay đổi fragment
//                transaction.setCustomAnimations(R.anim.bounce_in, R.anim.bounce_out);
//                // Thay thế Fragment hiện tại bằng Fragment mới
//                transaction.replace(R.id.frameLayout_cart, newFragment);
//                // Thêm giao diện vào back stack để người dùng có thể quay lại
//                transaction.addToBackStack(null);
//                // Xác nhận giao dịch và thay đổi fragment
//                transaction.commit();
                Intent intent = new Intent(getContext(),CheckOutActivity.class);
                startActivity(intent);

            }
        });


        btn_back = view.findViewById(R.id.back_button);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomNav();
                Fragment newFragment = new HomeFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.bounce_in, R.anim.bounce_out);
                transaction.replace(R.id.frameLayout_cart, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);

                bottomNavigationView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                    }
                }, 300);
            }
        });

        return view;
    }

    Callback<ResponseCart> getCartID = new Callback<ResponseCart>() {
        @Override
        public void onResponse(Call<ResponseCart> call, Response<ResponseCart> response) {
            if (response.isSuccessful()) {
                CartData cartData = response.body().getData();
                double totalPrice = cartData.getTotalPrice();
                List<ProducItem> products = cartData.getProducts();

                txtotal.setText("Total Price: $" + totalPrice);
                Log.d("CartFragment", "Total Price: " + totalPrice);
                if (products != null && !products.isEmpty()) {
                    for (ProducItem product : products) {
                        Log.d("CartFragment", "Product ID: " + product.getId());
                        Log.d("CartFragment", "Product Name: " + product.getProductId().getProduct_name());
                        Log.d("CartFragment", "Product Quantity: " + product.getQuantity());

                    }
                }
                adapter.setProducts(products);
                isProductSelected = products.stream().anyMatch(product -> product.isSelected());
                btn_checkout.setEnabled(isProductSelected);
            } else {
                Toast.makeText(getContext(), "Failed to fetch cart: " + response.message(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ResponseCart> call, Throwable t) {
            Log.e("zzzzz Failure", "Network error: " + t.getMessage());
            Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void showBottomNav() {
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProductChecked(ProducItem product, boolean isChecked) {
        // Lấy ID người dùng từ Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Tạo danh sách ID sản phẩm đã chọn
            List<String> selectedProductIds = new ArrayList<>();

            // Nếu sản phẩm được chọn, thêm vào danh sách selectedProductIds
            if (isChecked) {
                selectedProductIds.add(product.getId());
            }
            // Gọi API để cập nhật sản phẩm được chọn
            SelectProductRequest request = new SelectProductRequest(userId, selectedProductIds);
            httpRequest.callApi().selectProducts(request).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Cập nhật lại giỏ hàng sau khi thay đổi sản phẩm
                        // Kiểm tra trạng thái sản phẩm
                        isProductSelected = !selectedProductIds.isEmpty();
                        btn_checkout.setEnabled(isProductSelected);
                        httpRequest.callApi().getCart(userId).enqueue(getCartID);
                    } else {
                        isProductSelected = !selectedProductIds.isEmpty();
                        btn_checkout.setEnabled(isProductSelected);
                        httpRequest.callApi().getCart(userId).enqueue(getCartID);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Hiển thị thông báo lỗi nếu gặp sự cố kết nối
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void updateQuantity(String userId, String productId, String sizeId, String action) {
        Log.d("CartFragment", "Updating quantity...");
        Log.d("CartFragment", "User ID: " + userId);
        Log.d("CartFragment", "Product ID: " + productId);
        Log.d("CartFragment", "Size ID: " + sizeId);
        Log.d("CartFragment", "Action: " + action);

        // Tạo đối tượng yêu cầu
        UpdateQuantityRequest request = new UpdateQuantityRequest(userId, productId, sizeId, action);

        // Gọi API để cập nhật số lượng
        httpRequest.callApi().updateProductQuantity(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Giỏ hàng đã được cập nhật thành công
                    Log.d("CartFragment", "Quantity updated successfully.");
                    Toast.makeText(getContext(), "Quantity updated", Toast.LENGTH_SHORT).show();

                    // Lấy lại giỏ hàng mới
                    httpRequest.callApi().getCart(userId).enqueue(new Callback<ResponseCart>() {
                        @Override
                        public void onResponse(Call<ResponseCart> call, Response<ResponseCart> response) {
                            if (response.isSuccessful()) {
                                CartData cartData = response.body().getData();
                                double totalPrice = cartData.getTotalPrice();
                                List<ProducItem> updatedProducts = cartData.getProducts();

                                // Cập nhật giá trị total và danh sách sản phẩm mới
                                txtotal.setText("Total Price: $" + totalPrice);
                                adapter.setProducts(updatedProducts);  // Cập nhật danh sách sản phẩm trong adapter
                            } else {
                                Toast.makeText(getContext(), "Failed to fetch updated cart: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseCart> call, Throwable t) {
                            Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    // Xử lý phản hồi lỗi
                    try {
                        String errorResponse = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.d("CartFragment", "Failed to update quantity: " + errorResponse + response.message());
                    } catch (IOException e) {
                        Log.e("CartFragment", "Error parsing error response: " + e.getMessage());
                    }
                    Toast.makeText(getContext(), "Failed to update quantity: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("CartFragment", "Network error: " + t.getMessage());
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void removeCart(String userId, String productId) {
        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.confim_delete, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Initialize dialog elements
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvDialogMessage = dialogView.findViewById(R.id.tvDialogMessage);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        // Set up button actions
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            // Proceed with API call to remove the product
            RemoveFavouriteRequest request = new RemoveFavouriteRequest(userId, productId);
            httpRequest.callApi().removeCart(request).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        httpRequest.callApi().getCart(userId).enqueue(getCartID);
                        Toast.makeText(getContext(), "Product removed from the cart", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("MyCart", "Error removing product: " + response.code());
                        Toast.makeText(getContext(), "Failed to remove product", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("MyCart", "Error: " + t.getMessage());
                    Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Show the dialog
        dialog.show();
    }


}