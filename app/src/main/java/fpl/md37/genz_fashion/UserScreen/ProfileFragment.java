package fpl.md37.genz_fashion.UserScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import fpl.md37.genz_fashion.ManagerScreen.SignInActivity;

public class ProfileFragment extends Fragment {
    private LinearLayout layout_your_file, layout_payment, layout_order, layout_setting, layout_help, layout_privacy, layout_out;
    private ImageView btnbackProfile, imgProfile;
    private TextView tvProfileName;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fstore;
    private String userID;
//private View animatedDivider;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        btnbackProfile = view.findViewById(R.id.btnBack_profile);
        imgProfile = view.findViewById(R.id.img_ProfileView);
        tvProfileName = view.findViewById(R.id.tvProfileName);

//         animatedDivider = view.findViewById(R.id.animated_divider);
//
//        // Lấy AnimationDrawable từ background của View
//        AnimationDrawable animationDrawable = (AnimationDrawable) animatedDivider.getBackground();
//
//        // Bắt đầu chạy animation
//        animationDrawable.start();

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid(); // Lấy ID người dùng từ Firebase

        loadUserProfile();

        btnbackProfile.setOnClickListener(view1 -> navigateToHomeFragment());

        layout_your_file = view.findViewById(R.id.profile_profile);
        layout_your_file.setOnClickListener(v -> replaceFragment(new PersonalInformationFragment_(), R.id.frameLayout));

        layout_payment = view.findViewById(R.id.profile_payment);
        layout_payment.setOnClickListener(v ->navigateToActivity(PayMothodsFragment.class));

        layout_order = view.findViewById(R.id.profile_cart);
        layout_order.setOnClickListener(v -> navigateToActivity(MyOrderActivity.class));

        layout_setting = view.findViewById(R.id.profile_setting);
        layout_setting.setOnClickListener(view12 -> replaceFragment(new SettingFragment(), R.id.frameLayout));

        layout_help = view.findViewById(R.id.profile_help);
        layout_help.setOnClickListener(view12 -> replaceFragment(new HelpCenterFragment(), R.id.frameLayout));

        layout_privacy = view.findViewById(R.id.profile_policy);
        layout_privacy.setOnClickListener(view12 -> replaceFragment(new PrivacyPolicyFragment(), R.id.frameLayout));

        layout_out = view.findViewById(R.id.profile_out);
        layout_out.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserProfile() {
        if (getActivity() == null) return;

        SharedPreferences preferences = getActivity().getSharedPreferences("user_info", Activity.MODE_PRIVATE);
        String savedName = preferences.getString("name", "");
        String savedAvatar = preferences.getString("avatar", "");

        // Hiển thị thông tin từ SharedPreferences trước
        tvProfileName.setText(savedName);
        if (!savedAvatar.isEmpty()) {
            setAvatarImage(savedAvatar);
        } else {
            setDefaultAvatar(); // Hiển thị avatar mặc định nếu không có avatar trong SharedPreferences
        }

        // Lấy thông tin từ Firestore
        DocumentReference documentReference = fstore.collection("Client").document(userID);
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String firestoreName = task.getResult().getString("name");
                String firestoreAvatar = task.getResult().getString("avatar");

                // Cập nhật UI và SharedPreferences nếu có dữ liệu từ Firestore
                if (firestoreName != null && !firestoreName.isEmpty()) {
                    tvProfileName.setText(firestoreName);
                    preferences.edit().putString("name", firestoreName).apply();
                }

                if (firestoreAvatar != null && !firestoreAvatar.isEmpty()) {
                    setAvatarImage(firestoreAvatar);
                    preferences.edit().putString("avatar", firestoreAvatar).apply();
                } else {
                    setDefaultAvatar(); // Hiển thị avatar mặc định nếu không có avatar trong Firestore
                }
            } else {
                Log.d("ProfileFragment", "Failed to fetch user data from Firestore");
            }
        });
    }

    private void setDefaultAvatar() {
        Glide.with(this)
                .load(R.drawable.default_avatar) // Ảnh mặc định từ drawable
                .into(imgProfile);
    }


    private void setAvatarImage(String avatar) {
        try {
            byte[] decodedString = Base64.decode(avatar, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(this)
                    .load(decodedBitmap)
                    .placeholder(R.drawable.default_avatar) // Đặt ảnh tạm trong khi tải
                    .into(imgProfile);
        } catch (IllegalArgumentException e) {
            Log.d("ProfileFragment", "Invalid Base64 string for avatar");
            setDefaultAvatar(); // Hiển thị avatar mặc định khi xảy ra lỗi
        }
    }


    private void navigateToHomeFragment() {
        Fragment newFragment = new HomeFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.bounce_in, R.anim.bounce_out);
        transaction.replace(R.id.frameLayout_profile, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.postDelayed(() -> bottomNavigationView.setSelectedItemId(R.id.nav_home), 300);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.rotate_in, R.anim.zoom_out);
    }

    private void replaceFragment(Fragment targetFragment, int frameId) {
        if (getActivity() != null) {
            View bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNavigationView != null) {
                bottomNavigationView.setVisibility(View.GONE);
            }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.sile_right, R.anim.slide_left)
                    .replace(frameId, targetFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToActivity(Class<?> targetActivity) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), targetActivity);
            startActivity(intent);
        }
    }
}

