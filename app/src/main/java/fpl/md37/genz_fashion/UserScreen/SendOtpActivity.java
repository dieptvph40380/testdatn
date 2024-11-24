package fpl.md37.genz_fashion.UserScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.genz_fashion.databinding.ActivitySendOtpBinding;


public class SendOtpActivity extends AppCompatActivity {

    ActivitySendOtpBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressbar.setVisibility(View.GONE);

        binding.ccp.registerCarrierNumberEditText(binding.edtSoDienThoai);
        binding.buttonGui.setOnClickListener((v) -> {
            if (!binding.ccp.isValidFullNumber()){
                binding.edtSoDienThoai.setError("Phone number not valid");
                return;
            }
            Intent intent = new Intent(SendOtpActivity.this,OTPVerificationActivity.class);
            intent.putExtra("phone",binding.ccp.getFullNumberWithPlus());
            startActivity(intent);
        });

    }

}