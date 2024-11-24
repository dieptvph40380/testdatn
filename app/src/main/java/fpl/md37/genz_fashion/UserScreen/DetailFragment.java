package fpl.md37.genz_fashion.UserScreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;


public class DetailFragment extends Fragment {

    private ImageView productImagePlaceholder;
    private TextView productDetailsTitle, productName, productPrice, productDescription, productRating;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho fragment này
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // Ánh xạ các view
        productImagePlaceholder = view.findViewById(R.id.productImagePlaceholder);
        productDetailsTitle = view.findViewById(R.id.productDetailsTitle);
        productName = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);
        productDescription = view.findViewById(R.id.productDescription);
        productRating = view.findViewById(R.id.productRating);

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("productName");
            String price = bundle.getString("productPrice");
            String description = bundle.getString("productDescription");
            String imageUrl = bundle.getString("productImage");
            String rating = bundle.getString("productRating");

            // Hiển thị thông tin sản phẩm lên các view
            productName.setText(name);
            productPrice.setText(price);
            productDescription.setText(description);
            productRating.setText(rating);

            // Load hình ảnh sản phẩm
            Glide.with(this).load(imageUrl).into(productImagePlaceholder);
        }

        return view;
    }
}