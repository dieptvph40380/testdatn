package fpl.md37.genz_fashion.ManagerScreen;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import fpl.md37.genz_fashion.adapter.AdapterVoucher;
import fpl.md37.genz_fashion.api.ApiService;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.handel.Item_Handle_Voucher;
import fpl.md37.genz_fashion.models.Response;
import fpl.md37.genz_fashion.models.Voucher;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class VoucherFragment extends Fragment implements Item_Handle_Voucher {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private ArrayList<Voucher> listvoucher;
    private AdapterVoucher adapter;
    private HttpRequest httpRequest;
    private static final int PICK_IMAGE_REQUEST = 10;
    private Uri imageUri;
    private BottomSheetDialog bottomSheetDialog;
    private boolean isUpdating = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voucher, container, false);
        recyclerView = view.findViewById(R.id.rcv_view);
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
        initializeViews(view);
        fetchVoucher();
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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

    private void filter(String text) {
        ArrayList<Voucher> filteredList = new ArrayList<>();
        for (Voucher item : listvoucher) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        setupRecyclerView(filteredList);
    }

    private void fetchVoucher() {
        httpRequest.callApi().getAllVoucher().enqueue(new Callback<Response<ArrayList<Voucher>>>() {
            @Override
            public void onResponse(Call<Response<ArrayList<Voucher>>> call, retrofit2.Response<Response<ArrayList<Voucher>>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        listvoucher = response.body().getData();
                        setupRecyclerView(listvoucher);
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<ArrayList<Voucher>>> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }

    private void setupRecyclerView(ArrayList<Voucher> ds) {
        adapter = new AdapterVoucher(getContext(), ds, this);
        adapter.startDailyUpdate();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void initializeViews(View view) {
        MaterialButton add = view.findViewById(R.id.btnadd_voucher);
        add.setOnClickListener(v -> showAddVoucherDialog());
    }

    private void showAddVoucherDialog() {
        // Tạo BottomSheetDialog và ánh xạ layout
        bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_addvoucher);

        // Ánh xạ các thành phần trong layout
        EditText editName = bottomSheetDialog.findViewById(R.id.edtName);
        EditText editDiscountValue = bottomSheetDialog.findViewById(R.id.discountValue);
        EditText editMinimumOrderValue = bottomSheetDialog.findViewById(R.id.minimumOrderValue);
        EditText editValidFrom = bottomSheetDialog.findViewById(R.id.edtValidFrom);
        EditText editValidUntil = bottomSheetDialog.findViewById(R.id.edtValidUntil);
        EditText editDescription = bottomSheetDialog.findViewById(R.id.descreption_voucher);

        Spinner spinnerDiscountType = bottomSheetDialog.findViewById(R.id.spinnerDiscountType);
        MaterialButton btnSubmit = bottomSheetDialog.findViewById(R.id.addvoucher);
        MaterialButton btnSelectImage = bottomSheetDialog.findViewById(R.id.uploadvoucher); // Nút để chọn ảnh
        ImageView imageView = bottomSheetDialog.findViewById(R.id.imgVoucher); // ImageView để hiển thị ảnh

        editValidFrom.setOnClickListener(v -> openDatePickerDialog(editValidFrom));
        editValidUntil.setOnClickListener(v -> openDatePickerDialog(editValidUntil));

        ImageButton btnBack = bottomSheetDialog.findViewById(R.id.img_cancel);
        btnSelectImage.setOnClickListener(v -> openImageChooser());
        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> bottomSheetDialog.dismiss());
        btnSubmit.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String discountValue = editDiscountValue.getText().toString().trim();
            String minimumOrderValue = editMinimumOrderValue.getText().toString().trim();
            String validFrom = editValidFrom.getText().toString().trim();
            String validUntil = editValidUntil.getText().toString().trim();
            String description = editDescription.getText().toString().trim();

            String discountType = spinnerDiscountType.getSelectedItem().toString();

            // Kiểm tra các trường nhập liệu trước khi thêm voucher
            if (name.isEmpty() || discountValue.isEmpty() || minimumOrderValue.isEmpty() || validFrom.isEmpty() || validUntil.isEmpty() || description.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else {
                // Gọi hàm addVoucher để thêm voucher
                addVoucher(name, description, discountValue, discountType, validFrom, validUntil, minimumOrderValue, bottomSheetDialog);
            }
        });
        // Thiết lập lại kích thước BottomSheetDialog
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
                bottomSheet.setLayoutParams(layoutParams);
            }
        });

        // Hiển thị BottomSheetDialog
        bottomSheetDialog.show();
    }

    private void showUpdateVoucherDialog(Voucher voucher) {
        // Tạo BottomSheetDialog và ánh xạ layout
        bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.dialog_update_voucher);

        // Ánh xạ các thành phần trong layout
        EditText editName = bottomSheetDialog.findViewById(R.id.edtName1);
        EditText editDiscountValue = bottomSheetDialog.findViewById(R.id.discountValue1);
        EditText editMinimumOrderValue = bottomSheetDialog.findViewById(R.id.minimumOrderValue1);
        EditText editValidFrom = bottomSheetDialog.findViewById(R.id.edtValidFrom1);
        EditText editValidUntil = bottomSheetDialog.findViewById(R.id.edtValidUntil1);
        EditText editDescription = bottomSheetDialog.findViewById(R.id.descreption_voucher1);

        Spinner spinnerDiscountType = bottomSheetDialog.findViewById(R.id.spinnerDiscountType1);
        MaterialButton btnSubmit = bottomSheetDialog.findViewById(R.id.updatevoucher);
        MaterialButton btnSelectImage = bottomSheetDialog.findViewById(R.id.uploadvoucher1);
        ImageView imageView = bottomSheetDialog.findViewById(R.id.imgVoucher1);


        editName.setText(voucher.getName());
        editDiscountValue.setText(String.valueOf(voucher.getDiscountValue()));
        editMinimumOrderValue.setText(String.valueOf(voucher.getMinimumOrderValue()));
        editValidFrom.setText(voucher.getValidFrom());
        editValidUntil.setText(voucher.getValidUntil());
        editDescription.setText(voucher.getDescription());


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.discount_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiscountType.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(voucher.getDiscountType());
        spinnerDiscountType.setSelection(spinnerPosition);


        btnSelectImage.setOnClickListener(v -> openImageChooser());


        if (voucher.getImage() != null) {
            Glide.with(requireContext()).load(voucher.getImage()).into(imageView);
        }

        editValidFrom.setOnClickListener(v -> openDatePickerDialog(editValidFrom));
        editValidUntil.setOnClickListener(v -> openDatePickerDialog(editValidUntil));

        ImageButton btnBack = bottomSheetDialog.findViewById(R.id.img_cancel);

        btnBack.setOnClickListener(v -> bottomSheetDialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String discountValue = editDiscountValue.getText().toString().trim();
            String minimumOrderValue = editMinimumOrderValue.getText().toString().trim();
            String validFrom = editValidFrom.getText().toString().trim();
            String validUntil = editValidUntil.getText().toString().trim();
            String description = editDescription.getText().toString().trim();

            String discountType = spinnerDiscountType.getSelectedItem().toString();

            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Name cannot be empty.", Toast.LENGTH_SHORT).show();
            } else if (discountValue.isEmpty()) {
                Toast.makeText(getContext(), "Discount value cannot be empty.", Toast.LENGTH_SHORT).show();
            } else if (minimumOrderValue.isEmpty()) {
                Toast.makeText(getContext(), "Minimum order value cannot be empty.", Toast.LENGTH_SHORT).show();
            } else if (validFrom.isEmpty()) {
                Toast.makeText(getContext(), "Valid from date cannot be empty.", Toast.LENGTH_SHORT).show();
            } else if (validUntil.isEmpty()) {
                Toast.makeText(getContext(), "Valid until date cannot be empty.", Toast.LENGTH_SHORT).show();
            } else if (description.isEmpty()) {
                Toast.makeText(getContext(), "Description cannot be empty.", Toast.LENGTH_SHORT).show();
            } else {
                updateVoucher(voucher.getId(), name, description, discountValue, discountType, validFrom, validUntil, minimumOrderValue, bottomSheetDialog);
            }

        });


        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
                bottomSheet.setLayoutParams(layoutParams);
            }
        });

        bottomSheetDialog.show();
    }

    private void openDatePickerDialog(final EditText editText) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        editText.setText(selectedDate);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    // Add TypeProduct
    private void addVoucher(String itemName, String itemDes,
                            String itemValue, String itemType, String itemFrom,
                            String itemUntil, String itemMini, BottomSheetDialog bottomSheetDialog) {
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), itemName);
        RequestBody desPart = RequestBody.create(MediaType.parse("text/plain"), itemDes);
        RequestBody valuePart = RequestBody.create(MediaType.parse("text/plain"), itemValue);
        RequestBody typePart = RequestBody.create(MediaType.parse("text/plain"), itemType);
        RequestBody fromPart = RequestBody.create(MediaType.parse("text/plain"), itemFrom);
        RequestBody untilPart = RequestBody.create(MediaType.parse("text/plain"), itemUntil);
        RequestBody miniPart = RequestBody.create(MediaType.parse("text/plain"), itemMini);


        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            File file = new File(getRealPathFromURI(imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        ApiService apiService = httpRequest.callApi();
        Call<ResponseBody> call = apiService.addVoucher(namePart, desPart, valuePart, typePart, fromPart, untilPart, miniPart, imagePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    bottomSheetDialog.dismiss();
                    fetchVoucher();
                    Toast.makeText(getContext(), "Voucher added successfully!", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(getContext(), "Unable to add voucher ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    // Update Voucher
    private void updateVoucher(String voucherId, String itemName, String itemDes,
                               String itemValue, String itemType, String itemFrom,
                               String itemUntil, String itemMini, BottomSheetDialog bottomSheetDialog) {

        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), itemName);
        RequestBody desPart = RequestBody.create(MediaType.parse("text/plain"), itemDes);
        RequestBody valuePart = RequestBody.create(MediaType.parse("text/plain"), itemValue);
        RequestBody typePart = RequestBody.create(MediaType.parse("text/plain"), itemType);
        RequestBody fromPart = RequestBody.create(MediaType.parse("text/plain"), itemFrom);
        RequestBody untilPart = RequestBody.create(MediaType.parse("text/plain"), itemUntil);
        RequestBody miniPart = RequestBody.create(MediaType.parse("text/plain"), itemMini);

        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            File file = new File(getRealPathFromURI(imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), file);
            imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        }

        ApiService apiService = httpRequest.callApi();
        Call<ResponseBody> call = apiService.updateVoucher(voucherId, namePart, desPart, valuePart, typePart, fromPart, untilPart, miniPart, imagePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    bottomSheetDialog.dismiss();
                    fetchVoucher();
                    Toast.makeText(getContext(), "Voucher updated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Unable to update voucher", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("UpdateVoucher", "Failure: " + t.getMessage());
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ImageView imageViewAdd = bottomSheetDialog.findViewById(R.id.imgVoucher);
            if (imageViewAdd != null) {
                imageViewAdd.setImageURI(imageUri);
            }
            if (isUpdating) {
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    ImageView imageViewUpdate = bottomSheetDialog.findViewById(R.id.imgVoucher);
                    if (imageViewUpdate != null) {
                        imageViewUpdate.setImageURI(imageUri);
                    }
                }
            } else {
                if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    ImageView imageViewAdd1 = bottomSheetDialog.findViewById(R.id.imgVoucher1);
                    if (imageViewAdd1 != null) {
                        imageViewAdd1.setImageURI(imageUri);
                    }
                }
            }
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


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

    Callback<Response<Voucher>> responseCallback = new Callback<Response<Voucher>>() {
        @Override
        public void onResponse(Call<Response<Voucher>> call, retrofit2.Response<Response<Voucher>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    fetchVoucher();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Voucher>> call, Throwable t) {

        }
    };

    @Override
    public void Delete(Voucher voucher) {

    }

    @Override
    public void Update(Voucher voucher) {
        showUpdateVoucherDialog(voucher);
    }
}