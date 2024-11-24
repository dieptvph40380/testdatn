package fpl.md37.genz_fashion.ManagerScreen;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.util.ArrayList;

import fpl.md37.genz_fashion.adapter.AdapterTypeProduct;
import fpl.md37.genz_fashion.api.ApiService;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.handel.Item_Handle_Typeproduct;
import fpl.md37.genz_fashion.models.Response;
import fpl.md37.genz_fashion.models.Size;
import fpl.md37.genz_fashion.models.TypeProduct;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class TypeProductFragment extends Fragment implements Item_Handle_Typeproduct {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ArrayList<TypeProduct> typeProducts;
    private ArrayList<Size> sizes;
    private RecyclerView recyclerView;
    private AdapterTypeProduct adapter;
    private HttpRequest httpRequest;
    private static final int PICK_IMAGE_REQUEST = 10;
    private Uri imageUri;
    private boolean isUpdating = false;
    private BottomSheetDialog bottomSheetDialog; // Khai báo BottomSheetDialog

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_type_product, container, false);
        checkPermissions();
        initializeViews(view);
        ImageView btnback = view.findViewById(R.id.btnout);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivityManager.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
            }
        });
        httpRequest = new HttpRequest();
        fetchTypeProducts();
        fetchSizes();
        //search Typeproduct
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Tùy chọn: Xử lý khi người dùng nhấn "Search" trên bàn phím
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Lọc danh sách mỗi khi có thay đổi trong ô tìm kiếm
                filter(newText);
                return true;
            }
        });
        return view;
    }

    private void filter(String text) {
        ArrayList<TypeProduct> filteredList = new ArrayList<>();
        for (TypeProduct item : typeProducts) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        setupRecyclerView(filteredList);
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerTypeProduct);
        MaterialButton add = view.findViewById(R.id.btnadd);
        add.setOnClickListener(v -> showAddTypeProductDialog());
    }

    private void showAddTypeProductDialog() {

        bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_add_typeproduct);

        EditText editItemName = bottomSheetDialog.findViewById(R.id.edtName);
        MaterialButton btnSubmit = bottomSheetDialog.findViewById(R.id.addtype);
        ImageButton btnBack = bottomSheetDialog.findViewById(R.id.buttonClose);
        MaterialButton btnSelectImage = bottomSheetDialog.findViewById(R.id.addtypeproductupload);
        ChipGroup chipGroup = bottomSheetDialog.findViewById(R.id.chipGroupSizes);
        btnBack.setOnClickListener(v -> bottomSheetDialog.dismiss());


        btnSelectImage.setOnClickListener(v -> openImageChooser());
        if (sizes != null) {
            for (Size size : sizes) {
                Chip chip = new Chip(requireContext());
                chip.setText(size.getName());
                chip.setTag(size.getId());
                chip.setCheckable(true);
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {

                    if (isChecked) {

                    } else {

                    }
                });
                chipGroup.addView(chip);
            }
        }

        btnSubmit.setOnClickListener(v -> {
            String itemName = editItemName.getText().toString();
            String sizeIds = getSelectedSizeIds();

            if (itemName.isEmpty()) {
                editItemName.setError("Please enter the product name");
                return;
            }


            if (imageUri == null) {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }


            if (sizeIds.isEmpty()) {
                Toast.makeText(requireContext(), "Please select at least one size", Toast.LENGTH_SHORT).show();
                return;
            }
            addTypeProduct(itemName, sizeIds, bottomSheetDialog);
        });


        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
                layoutParams.height = getResources().getDisplayMetrics().heightPixels / 2;
                bottomSheet.setLayoutParams(layoutParams);
            }
        });
        bottomSheetDialog.show();
    }

    private void showUpdateTypeProductDialog(TypeProduct typeProduct) {

        bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_edit_typeproduct);


        EditText editItemName = bottomSheetDialog.findViewById(R.id.edtName);
        MaterialButton btnUpdate = bottomSheetDialog.findViewById(R.id.updatetype);
        ImageButton btnBack = bottomSheetDialog.findViewById(R.id.buttonClose);
        MaterialButton btnSelectImage = bottomSheetDialog.findViewById(R.id.addtypeproductupload);
        ImageView imageView = bottomSheetDialog.findViewById(R.id.imageDialog1);
        ChipGroup chipGroup = bottomSheetDialog.findViewById(R.id.chipGroupSizes);


        editItemName.setText(typeProduct.getName());
        if (typeProduct.getImage() != null && !typeProduct.getImage().isEmpty()) {
            assert imageView != null;
            Glide.with(requireContext())
                    .load(typeProduct.getImage())
                    .into(imageView);
        }

        btnBack.setOnClickListener(v -> bottomSheetDialog.dismiss());


        btnSelectImage.setOnClickListener(v -> openImageChooser());


        if (sizes != null) {
            for (Size size : sizes) {
                Chip chip = new Chip(requireContext());
                chip.setText(size.getName());
                chip.setTag(size.getId());
                chip.setCheckable(true);
                chip.setChecked(typeProduct.getSizes().contains(size.getId()));
                chipGroup.addView(chip);
            }
        }


        btnUpdate.setOnClickListener(v -> {
            String itemName = editItemName.getText().toString();
            String sizeIds = getSelectedSizeIds1(chipGroup);
            updateTypeProduct(typeProduct.getId(), itemName, sizeIds, bottomSheetDialog);
        });

        // Thiết lập chiều cao cho BottomSheetDialog
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
                layoutParams.height = getResources().getDisplayMetrics().heightPixels / 2;
                bottomSheet.setLayoutParams(layoutParams);
            }
        });

        bottomSheetDialog.show();
    }

    private String getSelectedSizeIds() {
        StringBuilder sizeIds = new StringBuilder();
        ChipGroup chipGroup = bottomSheetDialog.findViewById(R.id.chipGroupSizes);

        if (chipGroup != null) {
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    if (sizeIds.length() > 0) {
                        sizeIds.append(","); // Thêm dấu phẩy giữa các ID
                    }

                    String sizeId = chip.getTag().toString();
                    sizeIds.append(sizeId);
                }
            }
        }

        return sizeIds.toString();
    }

    private String getSelectedSizeIds1(ChipGroup chipGroup) {
        StringBuilder sizeIds = new StringBuilder();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                if (sizeIds.length() > 0) {
                    sizeIds.append(","); // Ngăn cách bằng dấu phẩy
                }
                sizeIds.append(chip.getTag());
            }
        }
        return sizeIds.toString();
    }

    // Add TypeProduct
    private void addTypeProduct(String itemName, String sizeIds, BottomSheetDialog bottomSheetDialog) {
        // Chuyển đổi các giá trị thành RequestBody
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), itemName);
        RequestBody sizesPart = RequestBody.create(MediaType.parse("text/plain"), sizeIds); // ID kích thước dạng chuỗi

        // Chuyển đổi URI thành MultipartBody.Part
        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            File file = new File(getRealPathFromURI(imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        // Gọi API để thêm loại sản phẩm
        ApiService apiService = httpRequest.callApi();
        Call<ResponseBody> call = apiService.addTypeProduct(namePart, sizesPart, imagePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    bottomSheetDialog.dismiss();
                    fetchTypeProducts();
                    Toast.makeText(getContext(), "Product type added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("AddTypeProduct", "Error: " + response.message());
                    Log.d("AddTypeProduct", "Error Code: " + response.code());
                    Log.d("AddTypeProduct", "Error Body: " + response.errorBody().toString());
                    Toast.makeText(getContext(), "Unable to add product type ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("AddTypeProduct", "Failure: " + t.getMessage());
            }
        });
    }

    //Update Product type
    private void updateTypeProduct(String typeId, String itemName, String sizeIds, BottomSheetDialog bottomSheetDialog) {
        // Chuyển đổi các giá trị thành RequestBody
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), itemName);
        RequestBody sizesPart = RequestBody.create(MediaType.parse("text/plain"), sizeIds); // ID kích thước dạng chuỗi

        // Chuyển đổi URI thành MultipartBody.Part (nếu có)
        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            File file = new File(getRealPathFromURI(imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        // Gọi API để cập nhật loại sản phẩm
        ApiService apiService = httpRequest.callApi();
        Call<ResponseBody> call = apiService.updateTypeProduct(typeId, namePart, sizesPart, imagePart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Xử lý phản hồi thành công
                    bottomSheetDialog.dismiss();
                    fetchTypeProducts();
                    Toast.makeText(getContext(), "Product type updated successfully!", Toast.LENGTH_SHORT).show();
                } else {

                    Log.d("UpdateTypeProduct", "Error: " + response.message());
                    Log.d("UpdateTypeProduct", "Error Code: " + response.code());
                    Log.d("UpdateTypeProduct", "Error Body: " + response.errorBody().toString());
                    Toast.makeText(getContext(), "Unable to update product type ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.e("UpdateTypeProduct", "Failure: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchTypeProducts() {
        httpRequest.callApi().getAlltypeproduct().enqueue(new Callback<Response<ArrayList<TypeProduct>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<TypeProduct>>> call, retrofit2.Response<Response<ArrayList<TypeProduct>>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        typeProducts = response.body().getData();
                        if (typeProducts != null) {
                            for (TypeProduct typeProduct : typeProducts) {
                                Log.d("TypeProduct", "ID: " + typeProduct.getId() + ", Name: " + typeProduct.getImage() + ", Sizes: " + (typeProduct.getSizes() != null ? typeProduct.getSizes().size() : "null"));
                            }
                        }
                        setupRecyclerView(typeProducts);
                        Toast.makeText(getContext(), "List displayed successfully! ", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<TypeProduct>>> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }

    private void fetchSizes() {
        httpRequest.callApi().getAllSizes().enqueue(new Callback<Response<ArrayList<Size>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Size>>> call, retrofit2.Response<Response<ArrayList<Size>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    sizes = response.body().getData();

                    Log.d("SizeList", "Number of sizes: " + sizes.size());


                    for (Size size : sizes) {
                        Log.d("SizeName", "ID: " + size.getId() + ", Name: " + size.getName());
                    }
                } else {
                    Log.d("SizeList", "Response not successful or empty data");
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Size>>> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }

    private void setupRecyclerView(ArrayList<TypeProduct> ds) {
        adapter = new AdapterTypeProduct(getContext(), ds, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    // Phương thức để mở ứng dụng chọn ảnh
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ImageView imageViewAdd = bottomSheetDialog.findViewById(R.id.imageDialog);
            if (imageViewAdd != null) {
                imageViewAdd.setImageURI(imageUri);
            }
            if (isUpdating) {
                // Nếu đang ở chế độ cập nhật
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    ImageView imageViewUpdate = bottomSheetDialog.findViewById(R.id.imageDialog);
                    if (imageViewUpdate != null) {
                        imageViewUpdate.setImageURI(imageUri); // Hiển thị ảnh trong dialog cập nhật
                    }
                }
            } else {
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    ImageView imageViewAdd1 = bottomSheetDialog.findViewById(R.id.imageDialog1);
                    if (imageViewAdd1 != null) {
                        imageViewAdd1.setImageURI(imageUri);
                    }
                }
            }
        }
    }


    // Phương thức để lấy đường dẫn thực tế từ URI
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Callback<Response<TypeProduct>> responseCallback = new Callback<Response<TypeProduct>>() {
        @Override
        public void onResponse(Call<Response<TypeProduct>> call, retrofit2.Response<Response<TypeProduct>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    fetchTypeProducts();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<TypeProduct>> call, Throwable t) {

        }
    };

    @Override
    public void Delete(TypeProduct typeProduct) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("yes", (dialog, which) -> {
            httpRequest.callApi()
                    .deleteTypeProduct(typeProduct.getId())
                    .enqueue(responseCallback);
            fetchTypeProducts();
            Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("no", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }


    @Override
    public void Update(TypeProduct typeProduct) {
        showUpdateTypeProductDialog(typeProduct);
    }
}

