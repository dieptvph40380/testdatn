package fpl.md37.genz_fashion.ManagerScreen;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import fpl.md37.genz_fashion.adapter.AdapterProduct;
import fpl.md37.genz_fashion.adapter.CustomSpinnerSuppAdapter;
import fpl.md37.genz_fashion.adapter.CustomSpinnerTypeAdapter;
import fpl.md37.genz_fashion.api.ApiService;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.handel.Item_Handle_Product;
import fpl.md37.genz_fashion.models.Product;
import fpl.md37.genz_fashion.models.Response;
import fpl.md37.genz_fashion.models.Size;
import fpl.md37.genz_fashion.models.Suppliers;
import fpl.md37.genz_fashion.models.TypeProduct;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


public class ProductsFragment extends Fragment  implements Item_Handle_Product{
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 10;
    private HttpRequest httpRequest;
    RecyclerView rcv;
    BottomSheetDialog bottomSheetDialog;
    private String id_suppliers, id_producttype;
    private ArrayList<TypeProduct> typeProductArrayList;
    private ArrayList<Suppliers> suppliersArrayList;
    Spinner spinnerType, spinnerSupplier;
    ArrayList<File> ds_image;
    private ArrayList<Product> productList = new ArrayList<>();
    ChipGroup chipGroup;
    LinearLayout sizeQuantityContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_products, container, false);
        httpRequest = new HttpRequest();
        ds_image = new ArrayList<>();
        initializeViews(view);
        fetchProducts();
        return view;
    }

    private void initializeViews(View view) {
        rcv = view.findViewById(R.id.rcv_view_product);
        MaterialButton add = view.findViewById(R.id.btn_add_products_admin);
        add.setOnClickListener(v -> showAddProductDialog());
    }



    private void showAddProductDialog() {
        bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_addproducts);
        bottomSheetDialog.setCancelable(false);

        ImageButton cancle = bottomSheetDialog.findViewById(R.id.img_cancel);
        MaterialButton btn_uploatimg = bottomSheetDialog.findViewById(R.id.addproductupload);
        EditText edtName = bottomSheetDialog.findViewById(R.id.product_name_dg);
        EditText edtPrice = bottomSheetDialog.findViewById(R.id.product_price_dg);
        EditText edtDescription = bottomSheetDialog.findViewById(R.id.product_description_dg);
        chipGroup = bottomSheetDialog.findViewById(R.id.chipGroupSizes_dg);

        Button btn_addprd = bottomSheetDialog.findViewById(R.id.add_button_prd);
        spinnerType = bottomSheetDialog.findViewById(R.id.products_type_sp);
        spinnerSupplier = bottomSheetDialog.findViewById(R.id.products_supplier_sp);

        if (spinnerType != null) {
            configSpinerType();
            configSupplier();
        } else {
            Log.e("ProductsFragment", "Spinner not found in dialog layout");
        }

        if (cancle != null) {
            cancle.setOnClickListener(v -> bottomSheetDialog.dismiss());
        }

        btn_uploatimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openImageChooser();
            }

        });

        btn_addprd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String name = edtName.getText().toString();
                    String price = edtPrice.getText().toString();
                    String description = edtDescription.getText().toString();
                    int totalQuantity = calculateTotalQuantity();

                // Kiểm tra dữ liệu đầu vào
                if (name.isEmpty() || price.isEmpty() || description.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng điền tất cả các trường", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ds_image.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng chọn ít nhất một hình ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }


                addProduct(name, price, String.valueOf(totalQuantity), "1", description, id_producttype, id_suppliers, bottomSheetDialog);
            }
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

        bottomSheetDialog.show();
    }

    private void addProduct(String name, String price, String quantity, String state, String description, String typeId, String supplierId, BottomSheetDialog bottomSheetDialog) {
        // Lấy danh sách size và số lượng từ các EditText trong sizeQuantityContainer
        List<HashMap<String, Object>> sizeQuantities = new ArrayList<>();

        for (int i = 0; i < sizeQuantityContainer.getChildCount(); i++) {
            View child = sizeQuantityContainer.getChildAt(i);
            if (child instanceof EditText) {
                EditText editText = (EditText) child;
                String sizeId = (String) editText.getTag();  // Lấy sizeId từ tag của EditText
                String quantityForSize = editText.getText().toString();

                if (!quantityForSize.isEmpty()) {
                    // Tạo HashMap cho từng sizeId và quantity
                    HashMap<String, Object> sizeQuantity = new HashMap<>();
                    sizeQuantity.put("sizeId", sizeId);
                    sizeQuantity.put("quantity", Integer.parseInt(quantityForSize));

                    // Thêm vào danh sách sizeQuantities
                    sizeQuantities.add(sizeQuantity);
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

        // Tạo danh sách MultipartBody.Part cho hình ảnh
        ArrayList<MultipartBody.Part> imageParts = new ArrayList<>();
        for (File imageFile : ds_image) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), imageFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
            imageParts.add(imagePart);
        }

        // Gọi API để thêm sản phẩm
        ApiService apiService = httpRequest.callApi();
        Call<Response<Product>> call = apiService.addProduct(namePart, pricePart, quantityPart, statePart, descriptionPart, supplierIdPart, typeIdPart, sizeQuantitiesPart, imageParts);
        call.enqueue(new Callback<Response<Product>>() {
            @Override
            public void onResponse(Call<Response<Product>> call, retrofit2.Response<Response<Product>> response) {
                if (response.isSuccessful()) {
                    bottomSheetDialog.dismiss();
                    fetchProducts(); // Cập nhật danh sách sản phẩm
                    Toast.makeText(getContext(), "Product added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("AddProduct", "Error: " + response.message());
                    Log.d("AddProduct", "Error Code: " + response.code());
                    try {
                        Log.d("AddProduct", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Unable to add product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<Product>> call, Throwable t) {
                Log.d("AddProduct", "Failure: " + t.getMessage());
                Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateChipGroupSizes(List<Size> sizes) {
        chipGroup.removeAllViews();
        sizeQuantityContainer = bottomSheetDialog.findViewById(R.id.quantityLayout);
        sizeQuantityContainer.removeAllViews();

        // Danh sách chứa sizeId và quantity cho mỗi sản phẩm
        List<HashMap<String, Object>> sizeQuantities = new ArrayList<>();

        for (Size size : sizes) {
            Chip chip = new Chip(getContext());
            chip.setText(size.getName());
            chip.setCheckable(true);

            // Lắng nghe sự kiện chọn chip
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Tạo EditText để nhập số lượng cho size này
                    EditText quantityInput = new EditText(getContext());
                    quantityInput.setHint("Quantity for size " + size.getName());
                    quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                    quantityInput.setTag(size.getId());  // Lưu sizeId vào tag của EditText

                    // Thêm EditText vào container
                    sizeQuantityContainer.addView(quantityInput);
                } else {
                    // Xóa EditText khi bỏ chọn
                    EditText quantityInput = sizeQuantityContainer.findViewWithTag(size.getId());
                    if (quantityInput != null) {
                        sizeQuantityContainer.removeView(quantityInput);
                    }
                }
            });

            chipGroup.addView(chip);
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


    private void fetchProducts() {
        httpRequest.callApi().getAllProducts().enqueue(new Callback<Response<ArrayList<Product>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Product>>> call, retrofit2.Response<Response<ArrayList<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                     productList = response.body().getData(); // Sử dụng ArrayList ở đây
                    if (productList != null) {
                        for (Product product : productList) {
                            Log.d("Product", "ID: " + product.getImage() );
                        }
                    }
                    setupRecyclerView(productList);
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Product>>> call, Throwable t) {
                Toast.makeText(getContext(), "Có lỗi xảy ra: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView(ArrayList<Product> products) {
        AdapterProduct adapter = new AdapterProduct(getContext(), products,this);
        rcv.setLayoutManager(new GridLayoutManager(getContext(),2));
        rcv.setAdapter(adapter);
    }

    private void openImageChooser() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            // Kiểm tra xem người dùng có chọn nhiều ảnh hay không
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount(); // Số lượng ảnh được chọn
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    // Hiển thị ảnh trên ImageView (nếu bạn muốn hiển thị lần lượt)
                    ImageView imageViewAdd = bottomSheetDialog.findViewById(R.id.product_image);
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
                ImageView imageViewAdd = bottomSheetDialog.findViewById(R.id.product_image);
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
            File file = new File(getContext().getCacheDir(), fileName);
            try (InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
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

    private void configSpinerType() {
        httpRequest.callApi().getAlltypeproduct().enqueue(getTypeProductAPI);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    id_producttype = typeProductArrayList.get(position).getId();
                    fetchSizesForSelectedType(id_producttype);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerType.setSelection(0);
    }

    private void fetchSizesForSelectedType(String typeId) {
        httpRequest.callApi().getTypeProductById(typeId).enqueue(new Callback<Response<TypeProduct>>() {
            @Override
            public void onResponse(Call<Response<TypeProduct>> call, retrofit2.Response<Response<TypeProduct>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TypeProduct typeProduct = response.body().getData();
                    List<Size> sizes = typeProduct.getSizes(); // Lấy danh sách Size
                    updateChipGroupSizes(sizes); // Cập nhật ChipGroup với danh sách Size
                } else {
                    Toast.makeText(getContext(), "Không thể lấy kích thước cho loại sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Response<TypeProduct>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu kích thước: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configSupplier() {
        httpRequest.callApi().getAllsuppliers().enqueue(getSupplierAPI);
        spinnerSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_suppliers = suppliersArrayList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerSupplier.setSelection(0);
    }

    Callback<Response<ArrayList<TypeProduct>>> getTypeProductAPI = new Callback<Response<ArrayList<TypeProduct>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<TypeProduct>>> call, retrofit2.Response<Response<ArrayList<TypeProduct>>> response) {
            if (response.isSuccessful() && response.body().getStatus() == 200) {
                typeProductArrayList = response.body().getData();

                String[] items = new String[typeProductArrayList.size()];
                for (int i = 0; i < typeProductArrayList.size(); i++) {
                    items[i] = typeProductArrayList.get(i).getName();
                }
                CustomSpinnerTypeAdapter adapterSpin = new CustomSpinnerTypeAdapter(getContext(), typeProductArrayList);
                spinnerType.setAdapter(adapterSpin);
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<TypeProduct>>> call, Throwable t) {
            Log.e("ProductsFragment", "Failed to fetch type products", t);
        }
    };

    Callback<Response<ArrayList<Suppliers>>> getSupplierAPI = new Callback<Response<ArrayList<Suppliers>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Suppliers>>> call, retrofit2.Response<Response<ArrayList<Suppliers>>> response) {
            if (response.isSuccessful() && response.body().getStatus() == 200) {
                suppliersArrayList = response.body().getData();
                String[] items = new String[suppliersArrayList.size()];

                for (int i = 0; i < suppliersArrayList.size(); i++) {
                    items[i] = suppliersArrayList.get(i).getName();
                }
                CustomSpinnerSuppAdapter customSpinnerSuppAdapter = new CustomSpinnerSuppAdapter(getContext(), suppliersArrayList);
                spinnerSupplier.setAdapter(customSpinnerSuppAdapter);
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Suppliers>>> call, Throwable t) {
            Log.e("ProductsFragment", "Failed to fetch suppliers", t);
        }
    };


    @Override
    public void ShowProduct(Product product) {
        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
        intent.putExtra("product_data", product);
        startActivity(intent);
    }

}
