package fpl.md37.genz_fashion.UserScreen;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContentProviderCompat.requireContext;
import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.models.CartResponseBody;
import fpl.md37.genz_fashion.models.Product;
import fpl.md37.genz_fashion.models.Response;
import fpl.md37.genz_fashion.models.Size;
import fpl.md37.genz_fashion.models.SizeQuantity;
import fpl.md37.genz_fashion.models.TypeProduct;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class DetailUser extends AppCompatActivity {

    private ImageView backArrow, productImagePlaceholder;
    private TextView productName, productPrice, productDescription;
    private Product product;
    private HttpRequest httpRequest = new HttpRequest();
    private Map<String, String> sizeIdMap = new HashMap<>();  // Lưu trữ id của các size

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);

        // Ánh xạ các view trong layout
        backArrow = findViewById(R.id.backArrow);
        productImagePlaceholder = findViewById(R.id.productImagePlaceholder);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);

        // Nút thêm vào giỏ hàng và mua ngay
        LinearLayout addToCartButton = findViewById(R.id.addToCart);
//        LinearLayout buyNowButton = findViewById(R.id.addBuyNow);

        addToCartButton.setOnClickListener(v -> showBottomSheet("Add to Cart"));
//        buyNowButton.setOnClickListener(v -> showBottomSheet("Buy Now"));

        // Nhận dữ liệu sản phẩm từ Intent
        product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            updateProductDetails(product);
            backArrow.setOnClickListener(v -> onBackPressed());
        } else {
            Toast.makeText(this, "Product details not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProductDetails(Product product) {
        if (product != null) {
            String imageUrl = product.getImage() != null && !product.getImage().isEmpty() ? product.getImage().get(0) : "";
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(productImagePlaceholder);
            } else {
                productImagePlaceholder.setImageResource(R.drawable.shark); // Placeholder image
            }

            productName.setText(TextUtils.isEmpty(product.getProduct_name()) ? "Unknown Product" : product.getProduct_name());
            productPrice.setText(TextUtils.isEmpty(product.getPrice()) ? "Price Not Available" : product.getPrice());
            productDescription.setText(TextUtils.isEmpty(product.getDescription()) ? "No description available." : product.getDescription());

            // Lấy và cập nhật danh sách kích thước từ API
            loadSizesFromApi();
        }
    }

    private void loadSizesFromApi() {
        httpRequest.callApi().getTypeProductById(product.getTypeProductId()).enqueue(new Callback<Response<TypeProduct>>() {

            @Override
            public void onResponse(Call<Response<TypeProduct>> call, retrofit2.Response<Response<TypeProduct>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    TypeProduct typeProduct = response.body().getData();
                    if (typeProduct != null && typeProduct.getSizes() != null) {
                        // Cập nhật sizeIdMap từ danh sách sizes
                        for (Size size : typeProduct.getSizes()) {
                            sizeIdMap.put(size.getName(), size.getId());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<TypeProduct>> call, Throwable t) {
                Log.e("API Failure", "Error: " + t.getMessage());
            }
        });
    }

    private void showBottomSheet(String action) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

        // Inflate layout cho BottomSheet
        LinearLayout sheetLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_selected, null);

        // Gắn dữ liệu vào các thành phần trong BottomSheet
        ImageView imgProduct = sheetLayout.findViewById(R.id.imgProduct);
        TextView tvProductPrice = sheetLayout.findViewById(R.id.tvProductPrice);
        TextView tvProductStock = sheetLayout.findViewById(R.id.tvProductStock);
        ChipGroup sizeOptions = sheetLayout.findViewById(R.id.chipGroupSizes);
        TextView tvQuantity = sheetLayout.findViewById(R.id.tvQuantity);
        TextView btnDecrease = sheetLayout.findViewById(R.id.btnDecreaseQuantity); // Là TextView
        TextView btnIncrease = sheetLayout.findViewById(R.id.btnIncreaseQuantity); // Là TextView

        // Biến lưu trữ số lượng hiện tại và tối đa
        final int[] currentQuantity = {1};
        final int[] maxQuantity = {0};

        // Lấy userId
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user != null ? user.getUid() : null;

        if (product != null) {
            String imageUrl = product.getImage() != null && !product.getImage().isEmpty() ? product.getImage().get(0) : "";
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(imgProduct);
            } else {
                imgProduct.setImageResource(R.drawable.shark); // Placeholder image
            }

            tvProductPrice.setText(TextUtils.isEmpty(product.getPrice()) ? "Price Not Available" : product.getPrice());

            // Cập nhật các size vào ChipGroup
            sizeOptions.removeAllViews();
            for (String sizeName : sizeIdMap.keySet()) {
                Chip chip = new Chip(this);
                chip.setText(sizeName);
                chip.setCheckable(true); // Cho phép chọn size
                chip.setOnClickListener(v -> {
                    // Bỏ chọn tất cả các chip khác
                    for (int i = 0; i < sizeOptions.getChildCount(); i++) {
                        Chip otherChip = (Chip) sizeOptions.getChildAt(i);
                        if (otherChip != chip) {
                            otherChip.setChecked(false);
                        }
                    }

                    // Cập nhật số lượng tối đa và hiện tại
                    String selectedSize = chip.getText().toString();
                    maxQuantity[0] = getAvailableQuantity(selectedSize, product.getSizeQuantities());
                    tvProductStock.setText("Still: " + maxQuantity[0]);
                    currentQuantity[0] = 1; // Reset số lượng về 1 khi chọn size mới
                    tvQuantity.setText(String.valueOf(currentQuantity[0]));
                });

                sizeOptions.addView(chip);
            }
        }

        // Xử lý nút tăng/giảm số lượng
        btnIncrease.setOnClickListener(v -> {
            if (currentQuantity[0] < maxQuantity[0]) {
                currentQuantity[0]++;
                tvQuantity.setText(String.valueOf(currentQuantity[0]));
            } else {
                Toast.makeText(this, "Đã đạt số lượng tối đa!", Toast.LENGTH_SHORT).show();
            }
        });

        btnDecrease.setOnClickListener(v -> {
            if (currentQuantity[0] > 1) {
                currentQuantity[0]--;
                tvQuantity.setText(String.valueOf(currentQuantity[0]));
            } else {
                Toast.makeText(this, "Số lượng tối thiểu là 1!", Toast.LENGTH_SHORT).show();
            }
        });

        // Gắn layout vào BottomSheetDialog và hiển thị
        bottomSheetDialog.setContentView(sheetLayout);
        bottomSheetDialog.show();

        // Thêm sản phẩm vào giỏ hàng khi nhấn "Add to Cart"
        sheetLayout.findViewById(R.id.btnBuyNow).setOnClickListener(v -> {
            if (userId != null) {
                // Gọi API thêm vào giỏ hàng với thông tin sản phẩm và userId
                addToCart(userId, product, sizeOptions, currentQuantity[0]);
                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart(String userId, Product product, ChipGroup sizeOptions, int quantity) {
        String selectedSize = null;
        for (int i = 0; i < sizeOptions.getChildCount(); i++) {
            Chip chip = (Chip) sizeOptions.getChildAt(i);
            if (chip.isChecked()) {
                selectedSize = chip.getText().toString();
                break;
            }
        }

        if (selectedSize != null) {
            String sizeId = sizeIdMap.get(selectedSize);
            if (sizeId != null) {
                // Tạo đối tượng CartResponseBody với thông tin giỏ hàng
                CartResponseBody cartResponseBody = new CartResponseBody(userId, product.getId(), sizeId, quantity);

                // Gọi API thêm vào giỏ hàng với đối tượng CartResponseBody
                httpRequest.callApi().addToCart(cartResponseBody).enqueue(new Callback<ResponseBody>() {


                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            // Xử lý khi thêm vào giỏ hàng thành công
                            Toast.makeText(DetailUser.this, "Added to cart successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                // Xử lý khi thêm vào giỏ hàng không thành công
                                String errorBody = response.errorBody().string(); // Get error message from error body
                                Log.e("API Error", "Error message: " + errorBody);
                                Toast.makeText(DetailUser.this, "Failed to add to cart: " + errorBody, Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Log.e("API Error", "IOException: " + e.getMessage());
                                Toast.makeText(DetailUser.this, "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Xử lý khi gọi API thất bại (ví dụ: lỗi mạng)
                        Log.d(".....", "onFailure: " + t.getMessage());
                        Toast.makeText(DetailUser.this, "Added to no cart successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Vui lòng chọn size!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Vui lòng chọn size!", Toast.LENGTH_SHORT).show();
        }
    }



    private int getAvailableQuantity(String selectedSize, List<SizeQuantity> sizeQuantities) {
        if (sizeQuantities == null || sizeIdMap.get(selectedSize) == null) {
            Log.d("SizeQuantity", "SizeQuantities hoặc SizeIdMap không hợp lệ.");
            return 0;
        }

        String sizeId = sizeIdMap.get(selectedSize);
        for (SizeQuantity sq : sizeQuantities) {
            if (sizeId.equals(sq.getSizeId())) {
                try {
                    return Integer.parseInt(sq.getQuantity());
                } catch (NumberFormatException e) {
                    Log.e("SizeQuantity", "Số lượng không hợp lệ: " + sq.getQuantity());
                }
            }
        }
        return 0;
    }

}
