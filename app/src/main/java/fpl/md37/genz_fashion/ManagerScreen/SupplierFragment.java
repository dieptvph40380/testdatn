package fpl.md37.genz_fashion.ManagerScreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fpl.md37.genz_fashion.adapter.AdapterSuppliers;
import fpl.md37.genz_fashion.api.ApiService;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.handel.Item_Handel_Suppliers;
import fpl.md37.genz_fashion.models.Response;
import fpl.md37.genz_fashion.models.Suppliers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;




public class SupplierFragment extends Fragment implements Item_Handel_Suppliers {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ArrayList<Suppliers> listSuppliers;
    private RecyclerView recyclerView;
    private AdapterSuppliers adapter;
    private HttpRequest httpRequest;
    private static final int PICK_IMAGE_REQUEST = 10;
    private Uri imageUri;
    private boolean isUpdating = false;
    private BottomSheetDialog bottomSheetDialog;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suppliers, container, false);
        checkPermissions();
        initializeViews(view);
        ImageView btnback=view.findViewById(R.id.btnout);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MainActivityManager.class);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);

            }
        });
        httpRequest =new HttpRequest();

        fetchSuppliers();

///
        // Thiết lập tìm kiếm
        //search Typeproduct
        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Tùy chọn: Xử lý khi người dùng nhấn "Search" trên bàn phím
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        return view;
    }

    private void filter(String query) {
        ArrayList<Suppliers> filteredList = new ArrayList<>();
        for (Suppliers supplier : listSuppliers) {
            if (supplier.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(supplier);
            }
        }
        setupRecyclerView(filteredList);
    }


    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewSuppliers);
        MaterialButton add = view.findViewById(R.id.btnadd);
        add.setOnClickListener(v -> showAddSupplierDialog());
    }

    private void showAddSupplierDialog(){
        // Khởi tạo BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_addsuppliers);

        // Ánh xạ các view trong BottomSheetDialog
        EditText editItemName = bottomSheetDialog.findViewById(R.id.edtName);
        EditText editItemPhone = bottomSheetDialog.findViewById(R.id.edtPhone);
        EditText editItemEmail = bottomSheetDialog.findViewById(R.id.edtEmail);
        EditText editItemDes = bottomSheetDialog.findViewById(R.id.edtInfor);

        MaterialButton btnSubmit = bottomSheetDialog.findViewById(R.id.addsupplier);
        ImageButton btnBack = bottomSheetDialog.findViewById(R.id.buttonClose);
        MaterialButton btnSelectImage = bottomSheetDialog.findViewById(R.id.addsuppliersUpload); // Nút để chọn ảnh
        ImageView imageView = bottomSheetDialog.findViewById(R.id.imageDialog); // ImageView để hiển thị ảnh
        btnBack.setOnClickListener(view -> bottomSheetDialog.dismiss());

        // Mở ứng dụng chọn ảnh khi người dùng nhấn vào nút
        btnSelectImage.setOnClickListener(v -> openImageChooser());

        btnSubmit.setOnClickListener(view -> {
            String itemName = editItemName.getText().toString().trim();
            String itemPhone = editItemPhone.getText().toString().trim();
            String itemEmail = editItemEmail.getText().toString().trim();
            String itemDes = editItemDes.getText().toString().trim();

            if (itemName.isEmpty()) {
                editItemName.setError("Name cannot be empty");
                return;
            }

            // Validate phone (10 digits, starts with 0)
            if (itemPhone.isEmpty()) {
                editItemPhone.setError("Phone number cannot be empty");
                return;
            } else if (!itemPhone.matches("^0\\d{9}$")) {
                editItemPhone.setError("Phone number must have 10 digits and start with 0");
                return;
            }

            // Validate email with @gmail.com format
            if (itemEmail.isEmpty()) {
                editItemEmail.setError("Email address cannot be empty");
                return;
            } else if (!itemEmail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                editItemEmail.setError("Email address must be in a valid format and end with @gmail.com");
                return;
            }

            // Validate description
            if (itemDes.isEmpty()) {
                editItemDes.setError("Description cannot be empty");
                return;
            } else if (itemDes.length() > 50) {
                editItemDes.setError("Description cannot exceed 50 characters");
                return;
            }

            // Validate image selection
            if (imageUri == null) {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }




            addSuppliers(itemName, itemPhone, itemEmail,itemDes, bottomSheetDialog);
        });


        // Thiết lập chiều cao cho BottomSheetDialog
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
                layoutParams.height = getResources().getDisplayMetrics().heightPixels / 2;  ////caanf suwa dder can chinh layout
                bottomSheet.setLayoutParams(layoutParams);
            }
        });

        bottomSheetDialog.show();

    }

    private void addSuppliers(String itemName, String itemPhone, String itemEmail, String itemDes, BottomSheetDialog bottomSheetDialog) {
        // Chuyển đổi các giá trị thành RequestBody
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), itemName);
        RequestBody phonePart = RequestBody.create(MediaType.parse("text/plain"), itemPhone);
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), itemEmail);
        RequestBody desPart = RequestBody.create(MediaType.parse("text/plain"), itemDes);

        // Chuyển đổi URI thành MultipartBody.Part
        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            File file = new File(getRealPathFromURI(imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        ApiService apiService = httpRequest.callApi();
        Call<ResponseBody> call=apiService.addSuppliers(namePart,phonePart,emailPart,desPart,imagePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Xử lý phản hồi thành công
                    bottomSheetDialog.dismiss();
                    fetchSuppliers();
                    Toast.makeText(getContext(), "Supplier added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý lỗi
                    Log.d("AddSuppliers", "Error: " + response.message());
                    Log.d("AddSuppliers", "Error Code: " + response.code());
                    Log.d("AddSuppliers", "Error Body: " + response.errorBody().toString());
                    Toast.makeText(getContext(), "Không thể thêm nhà cung cấp: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void fetchSuppliers(){
        httpRequest.callApi().getAllsuppliers().enqueue(new Callback<Response<ArrayList<Suppliers>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Suppliers>>> call, retrofit2.Response<Response<ArrayList<Suppliers>>> response) {
                if (response.isSuccessful()){
                    if (response.body().getStatus() ==200){
                        listSuppliers=response.body().getData();
                        if (listSuppliers != null){
                            for (Suppliers suppliers: listSuppliers){
                                Log.d("Suppliers","ID"+ suppliers.getId()+"Name: " + suppliers.getName()+ "Phone: "+suppliers.getPhone()+ "Email: "+suppliers.getEmail() +"Description: "+suppliers.getDescription() + suppliers.getImage());
                            }
                        }
                        setupRecyclerView(listSuppliers);
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Suppliers>>> call, Throwable t) {

            }
        });
    }

    private void setupRecyclerView(ArrayList<Suppliers> ds){
        adapter=new AdapterSuppliers(getContext(),ds,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
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

    Callback<Response<Suppliers>> responseCallback=new Callback<Response<Suppliers>>() {
        @Override
        public void onResponse(Call<Response<Suppliers>> call, retrofit2.Response<Response<Suppliers>> response) {
            if (response.isSuccessful()){
                if (response.body().getStatus()==200){
                    fetchSuppliers();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Suppliers>> call, Throwable t) {

        }
    };

    @Override
    public void Delete(Suppliers suppliers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("yes", (dialog, which) -> {
            httpRequest.callApi()
                    .deleteSuppliers(suppliers.getId())
                    .enqueue(responseCallback);
            fetchSuppliers();
            Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("no", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }



    ///
    private void showupdateSupplierDialog(Suppliers suppliers){
        // Khởi tạo BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_update_supplier);

        // Ánh xạ các view trong BottomSheetDialog
        EditText editItemName = bottomSheetDialog.findViewById(R.id.edtName);
        EditText editItemPhone = bottomSheetDialog.findViewById(R.id.edtPhone);
        EditText editItemEmail = bottomSheetDialog.findViewById(R.id.edtEmail);
        EditText editItemDes = bottomSheetDialog.findViewById(R.id.edtInfor);

        MaterialButton btnUpdate = bottomSheetDialog.findViewById(R.id.updatesupplier);
        ImageButton btnBack = bottomSheetDialog.findViewById(R.id.buttonClose);
        MaterialButton btnSelectImage = bottomSheetDialog.findViewById(R.id.updateloadS); // Nút để chọn ảnh
        ImageView imageView = bottomSheetDialog.findViewById(R.id.imageDialog); // ImageView để hiển thị ảnh


        editItemName.setText(suppliers.getName());
        editItemPhone.setText(suppliers.getPhone());
        editItemEmail.setText(suppliers.getEmail());
        editItemDes.setText(suppliers.getDescription());
        if (suppliers.getImage() != null && !suppliers.getImage().isEmpty()) {
            assert imageView != null;
            Glide.with(requireContext())
                    .load(suppliers.getImage())
                    .into(imageView);
        }


        btnBack.setOnClickListener(view -> bottomSheetDialog.dismiss());

        // Mở ứng dụng chọn ảnh khi người dùng nhấn vào nút
        btnSelectImage.setOnClickListener(v -> openImageChooser());

        btnUpdate.setOnClickListener(view -> {
            String itemName = editItemName.getText().toString();
            String itemPhone = editItemPhone.getText().toString();
            String itemEmail = editItemEmail.getText().toString();
            String itemDes = editItemDes.getText().toString();
            updateSuppliers(suppliers.getId(), itemName, itemPhone,itemEmail,itemDes, bottomSheetDialog);


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

    private void updateSuppliers(String typeId, String itemName, String itemPhone, String itemEmail,String itemDes, BottomSheetDialog bottomSheetDialog) {
        // Chuyển đổi các giá trị thành RequestBody
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), itemName);
        RequestBody phonePart = RequestBody.create(MediaType.parse("text/plain"), itemPhone);
        RequestBody emailPart = RequestBody.create(MediaType.parse("text/plain"), itemEmail);
        RequestBody desPart = RequestBody.create(MediaType.parse("text/plain"), itemDes);

        // Chuyển đổi URI thành MultipartBody.Part
        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            File file = new File(getRealPathFromURI(imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        ApiService apiService = httpRequest.callApi();
        Call<ResponseBody> call=apiService.updateSupplier(typeId,namePart,phonePart,emailPart,desPart,imagePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Xử lý phản hồi thành công
                    bottomSheetDialog.dismiss();
                    fetchSuppliers();
                    Toast.makeText(getContext(), "Supplier update successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý lỗi
                    Log.d("UpdateSuppliers", "Error: " + response.message());
                    Log.d("UpdateSuppliers", "Error Code: " + response.code());
                    Log.d("UpdateSuppliers", "Error Body: " + response.errorBody().toString());
                    Toast.makeText(getContext(), "Không thể update nhà cung cấp: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("UpdateTypeProduct", "Failure: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    ////


    private boolean isValidPhone(String phone) {
        return phone != null && !phone.trim().isEmpty() && phone.matches("^0\\d{9}$"); // Bắt đầu bằng 0 và theo sau là 9 chữ số
    }

    private boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() && email.endsWith("@gmail.com");
    }
    private boolean isValidDescription(String description) {
        return description != null && !description.trim().isEmpty() && description.length() <= 50; // Mô tả không được quá 50 ký tự
    }

    @Override
    public void Update(Suppliers suppliers) {
        showupdateSupplierDialog(suppliers);
    }

}