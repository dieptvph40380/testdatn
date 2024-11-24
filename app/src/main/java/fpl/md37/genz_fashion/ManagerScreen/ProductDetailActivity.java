package fpl.md37.genz_fashion.ManagerScreen;
import static java.security.AccessController.getContext;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fpl.md37.genz_fashion.adapter.CustomSpinnerSuppAdapter;
import fpl.md37.genz_fashion.adapter.CustomSpinnerTypeAdapter;
import fpl.md37.genz_fashion.adapter.ImageSliderAdapter;
import fpl.md37.genz_fashion.api.ApiService;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.models.Product;
import fpl.md37.genz_fashion.models.Response;
import fpl.md37.genz_fashion.models.Size;
import fpl.md37.genz_fashion.models.SizeQuantity;
import fpl.md37.genz_fashion.models.Suppliers;
import fpl.md37.genz_fashion.models.TypeProduct;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
public class ProductDetailActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 10;
    private ApiService apiService;
    private HttpRequest httpRequest;
    TextView supplier1,type1;
    private ViewPager2 viewPager;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private int currentPage = 0;
    Product product;
    BottomSheetDialog bottomSheetDialog;
    ArrayList<File> ds_image;
    Spinner spnSupplier,spnType;
    ChipGroup chipGroup;
    private String id_suppliers, id_producttype;
    LinearLayout sizeQuantityContainer;
    ImageView imageViewAdd;
    TextView name,price,quantity,state,description;
    ImageSliderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_products_detail);
        httpRequest = new HttpRequest();
        apiService = httpRequest.callApi();
        ds_image = new ArrayList<>();
        product = (Product) getIntent().getSerializableExtra("product_data");
        if (product == null) {
            finish();
            return;
        }
        viewPager = findViewById(R.id.viewpager_images);
        ArrayList<String> images = product.getImage();
        adapter = new ImageSliderAdapter(this, images);
        viewPager.setAdapter(adapter);
        startAutoSlide();

        name = findViewById(R.id.name_productdetail);
        price = findViewById(R.id.price_productdetail);
        quantity = findViewById(R.id.quantity_productdetail);
        supplier1 = findViewById(R.id.supplier_productdetail);
        type1 = findViewById(R.id.type_productdetail);
        state = findViewById(R.id.status_productdetail);
        description = findViewById(R.id.description_productdetail);
        Button btn_update = findViewById(R.id.update_product);
        ImageView btnout = findViewById(R.id.btnout2);

        btnout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
         refreshProductDetails();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showUpdateBottomSheet();
            }
        });

    }
    private void  refreshProductDetails(){
       name.setText(product.getProduct_name());
       price.setText("$"+product.getPrice());
       quantity.setText("Quantity:"+product.getQuantity());
       state.setText("Status: "+(product.isState() ? "Còn Hàng" : "Hết Hàng"));
       description.setText("Description: "+product.getDescription());
       getSupplierDetails(product.getSuppliersId());
       getTypeDetails(product.getTypeProductId());
       displaySizesAndQuantities();

   }
    private void showUpdateBottomSheet() {
        // Khởi tạo BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_updateproduct);
        bottomSheetDialog.setCancelable(false);

        // Khai báo các view bên trong BottomSheetDialog
        imageViewAdd = bottomSheetDialog.findViewById(R.id.product_image_ud);
        ImageButton cancle = bottomSheetDialog.findViewById(R.id.img_cancel2);
        EditText nameEditText = bottomSheetDialog.findViewById(R.id.product_name_ud);
        EditText priceEditText = bottomSheetDialog.findViewById(R.id.product_price_ud);
        EditText descriptionEditText = bottomSheetDialog.findViewById(R.id.product_description_ud);
        Button updateButton = bottomSheetDialog.findViewById(R.id.update_button_prd);
        chipGroup = bottomSheetDialog.findViewById(R.id.chipGroupSizes_ud);
        spnSupplier = bottomSheetDialog.findViewById(R.id.products_supplier_ud);
        spnType = bottomSheetDialog.findViewById(R.id.products_type_ud);
        MaterialButton btnSelectImage = bottomSheetDialog.findViewById(R.id.updateproductupload_ud);
         cancle.setOnClickListener(v -> bottomSheetDialog.dismiss());
         btnSelectImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 openImageChooser();
             }
         });
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            assert imageViewAdd != null;
            Glide.with(this)
                    .load(product.getImage().get(0))
                    .into(imageViewAdd);
        }
        nameEditText.setText(product.getProduct_name());
        priceEditText.setText(product.getPrice());
        descriptionEditText.setText(product.getDescription());
        getAllSuppliers();
        getAllTypeProducts();
        updateButton.setOnClickListener(v -> {
            String updatedName = nameEditText.getText().toString();
            String updatedPrice = priceEditText.getText().toString();
            String updatedDescription = descriptionEditText.getText().toString();
            int totalQuantity = calculateTotalQuantity();

            updateProduct(product.getId(), updatedName, updatedPrice, String.valueOf(totalQuantity), "1", updatedDescription,id_producttype,id_suppliers,bottomSheetDialog);
        });

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
                layoutParams.height = getResources().getDisplayMetrics().heightPixels;
                bottomSheet.setLayoutParams(layoutParams);
            }
        });
        // Hiển thị BottomSheetDialog
        bottomSheetDialog.show();
    }
    private void updateProduct(String productId, String name, String price, String quantity, String state, String description, String typeId, String supplierId, BottomSheetDialog bottomSheetDialog) {
        // Lấy danh sách size và số lượng từ các EditText trong sizeQuantityContainer
        List<HashMap<String, Object>> sizeQuantities = new ArrayList<>();
        for (int i = 0; i < sizeQuantityContainer.getChildCount(); i++) {
            View child = sizeQuantityContainer.getChildAt(i);
            if (child instanceof EditText) {
                EditText editText = (EditText) child;
                String sizeId = (String) editText.getTag();  // Lấy sizeId từ tag của EditText
                String quantityForSize = editText.getText().toString();
                if (!quantityForSize.isEmpty()) {
                    try {
                        // Tạo HashMap cho từng sizeId và quantity
                        HashMap<String, Object> sizeQuantity = new HashMap<>();
                        sizeQuantity.put("sizeId", sizeId);
                        sizeQuantity.put("quantity", Integer.parseInt(quantityForSize)); // Ensure this is valid integer
                        sizeQuantities.add(sizeQuantity);
                    } catch (NumberFormatException e) {
                        Toast.makeText(ProductDetailActivity.this, "Invalid quantity entered.", Toast.LENGTH_SHORT).show();
                        return; // Early exit if there's an invalid quantity
                    }
                }
            }
        }

        // Chuyển đổi sizeQuantities thành JSON hoặc RequestBody (tuỳ vào API)
        Gson gson = new Gson();
        String sizeQuantitiesJson = gson.toJson(sizeQuantities);

        // Chuyển đổi các giá trị thành RequestBody
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody pricePart = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody quantityPart = RequestBody.create(MediaType.parse("text/plain"), quantity);
        RequestBody statePart = RequestBody.create(MediaType.parse("text/plain"), state);
        RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody typeIdPart = RequestBody.create(MediaType.parse("text/plain"), typeId);
        RequestBody supplierIdPart = RequestBody.create(MediaType.parse("text/plain"), supplierId);
        RequestBody sizeQuantitiesPart = RequestBody.create(MediaType.parse("application/json"), sizeQuantitiesJson);  // Gửi sizeQuantities dưới dạng JSON

        // Tạo danh sách MultipartBody.Part cho hình ảnh (nếu có thay đổi hình ảnh)
        ArrayList<MultipartBody.Part> imageParts = new ArrayList<>();
        for (File imageFile : ds_image) {
            if (imageFile != null && imageFile.exists()) { // Ensure file exists
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), imageFile);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
                imageParts.add(imagePart);
            }
        }

        // Gọi API để cập nhật sản phẩm
        ApiService apiService = httpRequest.callApi();
        Call<Response<Product>> call = apiService.updateProduct(productId, namePart, pricePart, quantityPart, statePart, descriptionPart, supplierIdPart, typeIdPart, sizeQuantitiesPart, imageParts);
        call.enqueue(new Callback<Response<Product>>() {
            @Override
            public void onResponse(Call<Response<Product>> call, retrofit2.Response<Response<Product>> response) {
                if (response.isSuccessful()) {
                    Product updatedProduct = response.body().getData(); // Giả sử API trả về sản phẩm mới trong body
                    if (updatedProduct != null) {
                        product = updatedProduct;  // Cập nhật lại đối tượng sản phẩm
                        ArrayList<String> updatedImages = product.getImage();
                        adapter.updateImages(updatedImages);
                    }
                    bottomSheetDialog.dismiss();
                    refreshProductDetails(); // Cập nhật danh sách sản phẩm

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updated_product", updatedProduct);
                    setResult(Activity.RESULT_OK, resultIntent);
                    Toast.makeText(ProductDetailActivity.this, "Product updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("UpdateProduct", "Error: " + response.message());
                    Log.d("UpdateProduct", "Error Code: " + response.code());
                    try {
                        Log.d("UpdateProduct", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ProductDetailActivity.this, "Unable to update product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Product>> call, Throwable t) {
                Log.d("UpdateProduct", "Failure: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openImageChooser() {
        if (ContextCompat.checkSelfPermission(ProductDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProductDetailActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // Kiểm tra xem người dùng có chọn nhiều ảnh hay không
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount(); // Số lượng ảnh được chọn
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    // Hiển thị ảnh trên ImageView (nếu bạn muốn hiển thị lần lượt)
                    if (imageViewAdd != null && i == 0) { // Chỉ hiển thị ảnh đầu tiên để minh họa
                        imageViewAdd.setImageURI(imageUri);
                        imageViewAdd.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }
                    // Thêm ảnh vào danh sách File nếu cần thiết
                    File imageFile = createFileFromUri(imageUri);
                    ds_image.add(imageFile);
                }
            } else if (data.getData() != null) {
                // Trường hợp chỉ chọn một ảnh
                Uri imageUri = data.getData();
                if (imageViewAdd != null) {
                    imageViewAdd.setImageURI(imageUri);
                    imageViewAdd.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }

                // Thêm ảnh vào danh sách File
                File imageFile = createFileFromUri(imageUri);
                ds_image.add(imageFile);
            }
        }
    }
    private File createFileFromUri(Uri uri) {
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File file = new File(ProductDetailActivity.this.getCacheDir(), fileName);
        try (InputStream inputStream = ProductDetailActivity.this.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    // Lấy danh sách va id nhà cung cấp từ API
    private void getAllSuppliers() {
        httpRequest.callApi().getAllsuppliers().enqueue(new Callback<Response<ArrayList<Suppliers>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Suppliers>>> call, retrofit2.Response<Response<ArrayList<Suppliers>>> response) {
                if (response.isSuccessful() && response.body().getStatus() == 200) {
                    ArrayList<Suppliers> suppliersList = response.body().getData();
                    // Cập nhật Spinner với danh sách nhà cung cấp
                    CustomSpinnerSuppAdapter supplierAdapter = new CustomSpinnerSuppAdapter(ProductDetailActivity.this, suppliersList);
                    spnSupplier.setAdapter(supplierAdapter);

                    // Lựa chọn nhà cung cấp hiện tại
                    for (int i = 0; i < suppliersList.size(); i++) {
                        if (suppliersList.get(i).getId().equals(product.getSuppliersId())) {
                            spnSupplier.setSelection(i);
                            break;
                        }
                    }
                    spnSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            id_suppliers = suppliersList.get(position).getId();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Suppliers>>> call, Throwable t) {
                Log.e("ProductDetail", "Failed to fetch suppliers", t);
            }
        });
    }

    // Lấy danh sách va id loại sản phẩm từ API
    private void getAllTypeProducts() {
        httpRequest.callApi().getAlltypeproduct().enqueue(new Callback<Response<ArrayList<TypeProduct>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<TypeProduct>>> call, retrofit2.Response<Response<ArrayList<TypeProduct>>> response) {
                if (response.isSuccessful() && response.body().getStatus() == 200) {
                    ArrayList<TypeProduct> typeProductList = response.body().getData();
                    // Cập nhật Spinner với danh sách loại sản phẩm
                    CustomSpinnerTypeAdapter typeProductAdapter = new CustomSpinnerTypeAdapter(ProductDetailActivity.this, typeProductList);
                    spnType.setAdapter(typeProductAdapter);

                    // Lựa chọn loại sản phẩm hiện tại
                    for (int i = 0; i < typeProductList.size(); i++) {
                        if (typeProductList.get(i).getId().equals(product.getTypeProductId())) {
                            spnType.setSelection(i);
                            break;
                        }
                    }
                    spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            id_producttype = typeProductList.get(position).getId();
                            fetchSizesForSelectedType(id_producttype);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<TypeProduct>>> call, Throwable t) {
                Log.e("ProductDetail", "Failed to fetch type products", t);
            }
        });
    }
    //lay danh sach size
    private void fetchSizesForSelectedType(String typeId) {
        httpRequest.callApi().getTypeProductById(typeId).enqueue(new Callback<Response<TypeProduct>>() {
            @Override
            public void onResponse(Call<Response<TypeProduct>> call, retrofit2.Response<Response<TypeProduct>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TypeProduct typeProduct = response.body().getData();
                    List<Size> sizes = typeProduct.getSizes(); // Lấy danh sách Size
                    updateChipGroupSizes(sizes); // Cập nhật ChipGroup với danh sách Size
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không thể lấy kích thước cho loại sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<TypeProduct>> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi khi lấy dữ liệu kích thước: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateChipGroupSizes(List<Size> sizes) {
        chipGroup.removeAllViews();
        sizeQuantityContainer = bottomSheetDialog.findViewById(R.id.quantityLayout_ud);
        sizeQuantityContainer.removeAllViews();

        for (Size size : sizes) {
            Chip chip = new Chip(this);
            chip.setText(size.getName());
            chipGroup.addView(chip);

            // Tạo EditText để nhập số lượng
            EditText quantityEditText = new EditText(this);
            quantityEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            quantityEditText.setHint("Quantity for " + size.getName());
            quantityEditText.setTag(size.getId());
            sizeQuantityContainer.addView(quantityEditText);

            // Lấy số lượng hiện tại nếu có
            for (SizeQuantity sizeQuantity : product.getSizeQuantities()) {
                if (sizeQuantity.getSizeId().equals(size.getId())) {
                    quantityEditText.setText(sizeQuantity.getQuantity());
                    break;
                }
            }
        }
    }
    private int calculateTotalQuantity() {
        int totalQuantity = 0;
        for (int i = 0; i < sizeQuantityContainer.getChildCount(); i++) {
            View child = sizeQuantityContainer.getChildAt(i);
            if (child instanceof EditText) {
                EditText editText = (EditText) child;
                String quantityText = editText.getText().toString();
                if (!quantityText.isEmpty()) {
                    totalQuantity += Integer.parseInt(quantityText);
                }
            }
        }
        return totalQuantity;
    }
    //lay thong tin supplier theo id
    private void getSupplierDetails(String id) {
        httpRequest.callApi().getSupplierById(id).enqueue(new Callback<Response<Suppliers>>() {
            @Override
            public void onResponse(Call<Response<Suppliers>> call, retrofit2.Response<Response<Suppliers>> response) {
                if (response.isSuccessful() && response.body().getStatus() == 200) {
                    Suppliers suppliers = response.body().getData();
                    supplier1.setText("Supplier: " + suppliers.getName());
                }
            }

            @Override
            public void onFailure(Call<Response<Suppliers>> call, Throwable t) {
                Log.e("ProductDetail", "Failed to fetch product type details", t);
            }
        });
    }
    //lay thong tin type theo id
    private void getTypeDetails(String id) {
        httpRequest.callApi().getTypeProductById(id).enqueue(new Callback<Response<TypeProduct>>() {
            @Override
            public void onResponse(Call<Response<TypeProduct>> call, retrofit2.Response<Response<TypeProduct>> response) {
                if (response.isSuccessful() && response.body().getStatus() == 200) {
                    TypeProduct typeProduct = response.body().getData();
                    // Hiển thị kết quả trong TextView
                    type1.setText("Type Product: " + typeProduct.getName());
                }
            }

            @Override
            public void onFailure(Call<Response<TypeProduct>> call, Throwable t) {
                Log.e("ProductDetail", "Failed to fetch product type details", t);
            }
        });
    }
    //lay so luong size
    private void displaySizesAndQuantities() {
        httpRequest.callApi().getTypeProductById(product.getTypeProductId()).enqueue(new Callback<Response<TypeProduct>>() {
            @Override
            public void onResponse(Call<Response<TypeProduct>> call, retrofit2.Response<Response<TypeProduct>> response) {
                if (response.isSuccessful() && response.body().getStatus() == 200) {
                    TypeProduct typeProduct = response.body().getData();

                    if (typeProduct != null && typeProduct.getSizes() != null && !typeProduct.getSizes().isEmpty()) {
                        StringBuilder sizesBuilder = new StringBuilder("Sizes and Quantities:\n");
                        for (Size size : typeProduct.getSizes()) {
                            // Kiểm tra số lượng tương ứng với từng size
                            int quantity = 0;
                            for (SizeQuantity sizeQuantity : product.getSizeQuantities()) {
                                if (size.getId().equals(sizeQuantity.getSizeId())) {
                                    quantity = Integer.parseInt(sizeQuantity.getQuantity());
                                    break;
                                }
                            }
                            sizesBuilder.append(size.getName())
                                    .append(": ")
                                    .append(quantity)
                                    .append("     ");
                        }

                        // Cập nhật vào TextView
                        TextView sizesTextView = findViewById(R.id.size_quantities_productdetail);
                        sizesTextView.setText(sizesBuilder.toString());
                    } else {
                        TextView sizesTextView = findViewById(R.id.size_quantities_productdetail);
                        sizesTextView.setText("Sizes and Quantities: Not available");
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<TypeProduct>> call, Throwable t) {
                TextView sizesTextView = findViewById(R.id.size_quantities_productdetail);
                sizesTextView.setText("Failed to load sizes and quantities");
            }
        });
    }
    //chay slide anh
    private void startAutoSlide() {
        sliderHandler.postDelayed(slideRunnable, 3000); // Chuyển ảnh mỗi 3 giây
    }
    private final Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager.getAdapter() != null) {
                currentPage = (currentPage + 1) % viewPager.getAdapter().getItemCount();
                viewPager.setCurrentItem(currentPage, true); // true để có hiệu ứng chuyển ảnh
            }
            sliderHandler.postDelayed(this, 3000); // Lặp lại sau 3 giây
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderHandler.removeCallbacks(slideRunnable); // Xóa runnable khi activity bị hủy
    }
}