package fpl.md37.genz_fashion.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import fpl.md37.genz_fashion.models.Client;

public class AndroidUtil {

    // Hiển thị thông báo Toast
    public static void showToast(Context context, String message) {
        if (context != null && message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
    public static void passUserModelAsIntent(Intent intent, Client model){
        intent.putExtra("username",model.getName());
        intent.putExtra("phone",model.getPhone());
        intent.putExtra("userId",model.getUserId());
        intent.putExtra("fcmToken",model.getFcmToken());

    }

    public static Client getUserModelFromIntent(Intent intent){
        Client userModel = new Client();
        userModel.setName(intent.getStringExtra("username"));
        userModel.setPhone(intent.getStringExtra("phone"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }

    // Cập nhật ảnh hồ sơ
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        if (context != null && imageUri != null && imageView != null) {
            Glide.with(context)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform()) // Cắt hình tròn
                    .into(imageView);
        }
    }
}
