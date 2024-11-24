package fpl.md37.genz_fashion.UserScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fpl.md37.genz_fashion.models.Client;
import fpl.md37.genz_fashion.utils.AndroidUtil;
import fpl.md37.genz_fashion.utils.FirebaseUtil;

public class PersonalInformationFragment_ extends Fragment {

    EditText edtName, edtEmail, edtPhone, edtAddress;
    ImageView profilePic;
    TextView updateProfileBtn;
    Client currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;
    Context safeContext; // Lưu trữ context an toàn

    public PersonalInformationFragment_() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        safeContext = context; // Gắn kết context an toàn
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            if (safeContext != null) {
                                Glide.with(safeContext)
                                        .load(selectedImageUri)
                                        .into(profilePic);
                            }
                        }
                    }
                });
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_information_, container, false);

        profilePic = view.findViewById(R.id.img_Profile);
        edtName = view.findViewById(R.id.edt_ProfileName);
        edtEmail = view.findViewById(R.id.edt_ProfileEmail);
        edtPhone = view.findViewById(R.id.edt_ProfilePhone);
        edtAddress = view.findViewById(R.id.edt_ProfileAddress);

        updateProfileBtn = view.findViewById(R.id.btn_update_profile);


//        Button animatedGradientButton = view.findViewById(R.id.shadow_button);
//        AnimationDrawable animationDrawable = (AnimationDrawable) animatedGradientButton.getBackground();
//        animationDrawable.start(); // Khởi động hiệu ứng gradient động
//
//        animatedGradientButton.setOnClickListener(v -> {
//            // Thực hiện hành động khi bấm vào nút
//            Toast.makeText(this, "Animated Gradient Button Clicked!", Toast.LENGTH_SHORT).show();
//        });



        getUserData();

        updateProfileBtn.setOnClickListener(v -> updateBtnClick());

        profilePic.setOnClickListener(v -> ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null;
                }));

        ImageView btnBack = view.findViewById(R.id.btnout);
        btnBack.setOnClickListener(v -> {
            showBottomNav();
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void showBottomNav() {
        if (getActivity() != null) {
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNavigationView != null) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        }
    }

    void getUserData() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (isAdded() && safeContext != null) {
                if (task.isSuccessful() && task.getResult() != null) {
                    currentUserModel = task.getResult().toObject(Client.class);

                    if (currentUserModel != null) {
                        edtName.setText(currentUserModel.getName());
                        edtEmail.setText(currentUserModel.getEmail());
                        edtPhone.setText(currentUserModel.getPhone());
                        edtAddress.setText(currentUserModel.getAddress());

                        String profilePicPath = currentUserModel.getAvatar();
                        if (profilePicPath != null && !profilePicPath.isEmpty()) {
                            if (isValidBase64(profilePicPath)) {
                                byte[] decodedString = Base64.decode(profilePicPath, Base64.DEFAULT);
                                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                Glide.with(safeContext)
                                        .load(decodedBitmap)
                                        .into(profilePic);
                            } else {
                                Glide.with(safeContext)
                                        .load(profilePicPath)
                                        .into(profilePic);
                            }
                        }
                    } else {
                        AndroidUtil.showToast(safeContext, "User data not found.");
                    }
                } else {
                    AndroidUtil.showToast(safeContext, "Failed to fetch user data.");
                }
            }
        });
    }

    void updateToFirestore() {
        if (safeContext != null) {
            FirebaseUtil.currentUserDetails().set(currentUserModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            AndroidUtil.showToast(safeContext, "Updated successfully");
                        } else {
                            AndroidUtil.showToast(safeContext, "Update failed");
                        }
                    });
        }
    }

    void updateBtnClick() {
        if (currentUserModel == null) {
            AndroidUtil.showToast(safeContext, "User data is not loaded yet. Please try again.");
            return;
        }

        String newUsername = edtName.getText().toString();
        currentUserModel.setName(newUsername);

        String newUserEmail = edtEmail.getText().toString();
        currentUserModel.setEmail(newUserEmail);

        String newUserPhone = edtPhone.getText().toString();
        currentUserModel.setPhone(newUserPhone);

        String newUserAddress = edtAddress.getText().toString();
        currentUserModel.setAddress(newUserAddress);

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", newUsername);

        if (selectedImageUri != null) {
            String base64Image = convertImageToBase64(selectedImageUri);
            currentUserModel.setAvatar(base64Image);
            editor.putString("avatar", base64Image);
        }

        editor.apply();
        updateToFirestore();
    }

    private boolean isValidBase64(String base64String) {
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return decodedBytes.length > 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
