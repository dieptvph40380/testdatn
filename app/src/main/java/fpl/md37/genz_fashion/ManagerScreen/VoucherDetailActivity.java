package fpl.md37.genz_fashion.ManagerScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;

public class VoucherDetailActivity extends AppCompatActivity {
    ImageView voucher_image;
    TextView voucher_name,voucher_des,voucher_value,voucher_mini,voucher_type,voucher_from,voucher_until;
    ImageButton voucher_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voucher_detail);
        voucher_back=findViewById(R.id.btnBack);
        voucher_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        voucher_image=findViewById(R.id.imgVoucher);
        voucher_name=findViewById(R.id.txtVoucherName);
        voucher_des=findViewById(R.id.txtVoucherDescription);
        voucher_value=findViewById(R.id.txtDiscountValue);
        voucher_type=findViewById(R.id.txtDiscountType);
        voucher_mini=findViewById(R.id.txtMinimumOrder);
        voucher_from=findViewById(R.id.txtValidFrom);
        voucher_until=findViewById(R.id.txtValidUntil);
        Intent intent=getIntent();
        String image=intent.getStringExtra("image");
        String name=intent.getStringExtra("name");
        String des=intent.getStringExtra("description");
        double discountValue = intent.getDoubleExtra("discountValue", 0.0);
        double minimumOrderValue = intent.getDoubleExtra("minimumOrderValue", 0.0);
        String type=intent.getStringExtra("discountType");
        String from=intent.getStringExtra("validFrom");
        String until=intent.getStringExtra("validUntil");
        Glide.with(this).load(image).into(voucher_image);
        voucher_name.setText(name);
        voucher_des.setText(des);
        if ("percent".equals(type)) {
            voucher_value.setText("Discount: " + discountValue + " %");
        } else if ("fixed".equals(type)) {
            voucher_value.setText("Discount: " + discountValue + " VND");
        } else {
            voucher_value.setText("Discount: " + discountValue);
        }
        voucher_type.setText("Type: " + type);

        voucher_mini.setText("Minimum Order Value: " +minimumOrderValue+" VND");
        voucher_from.setText("Valid From:" +from);
        voucher_until.setText("Valid Until:" +until);

    }
}