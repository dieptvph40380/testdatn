package fpl.md37.genz_fashion.UserScreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.genz_fashion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import fpl.md37.genz_fashion.ManagerScreen.ProfileCustomerFragment;

public class PayMothodsFragment extends AppCompatActivity {

   private ImageView btnBack_payment;

   private TextView tvZalo,tvMomo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pay_mothods);

        btnBack_payment = findViewById(R.id.btnBack_payment);
        tvZalo=findViewById(R.id.tv_zalopay);
        tvMomo=findViewById(R.id.tv_momo);

        btnBack_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showBottomNav();
                // Hiển thị lại BottomNavigationView
                showBottomNav();
                // Quay về màn Profile
                Intent intent = new Intent(PayMothodsFragment.this, ProfileFragment.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Đảm bảo không lưu stack
                startActivity(intent);
                finish();
            }
        });

        tvZalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayMothods(view, "HaloPay");
            }
        });
        tvMomo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayMothods(view, "MoMo");
            }
        });


    }

    private void showBottomNav() {
        BottomNavigationView bottomNavigationView =findViewById(R.id.bottom_nav);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    public void PayMothods(View view, String method) {

        // Tạo Fragment mới
        Fragment checkOutFragment = new CheckOutFragment();

        // Gửi dữ liệu qua Bundle
        // Trong PayMothodsFragment
        Bundle bundle = new Bundle();
        bundle.putString("selected_method", method); // Gán phương thức thanh toán vào Bundle

        Intent resultIntent = new Intent();
        resultIntent.putExtras(bundle); // Đưa Bundle vào Intent

        this.setResult(Activity.RESULT_OK, resultIntent); // Trả kết quả cho Activity gọi
        this.finish();

        // Thay thế Fragment hiện tại bằng CheckOutFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout_pay, checkOutFragment); // R.id.fragment_container là ID của layout chứa các Fragment
        transaction.addToBackStack(null); // Thêm vào stack để có thể quay lại
        transaction.commit();

    }

}