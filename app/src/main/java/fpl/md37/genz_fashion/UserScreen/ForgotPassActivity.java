package fpl.md37.genz_fashion.UserScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.genz_fashion.R;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

public class ForgotPassActivity extends AppCompatActivity {

    Button btnSend;
    TextInputEditText edt_Phone;
    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass);

        edt_Phone=findViewById(R.id.edt_Phone);
        btnSend = findViewById(R.id.btnSendOTP);
        ccp = findViewById(R.id.ccp);

        ccp.registerCarrierNumberEditText(edt_Phone);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = edt_Phone.getText().toString().trim();
                if (!ccp.isValidFullNumber()) {
                    edt_Phone.setError("Phone number not valid");
                    return;
                }
                    // Chuyển sang màn OTPForgotPassActivity và truyền số điện thoại đầy đủ
                    Intent intent = new Intent(getApplicationContext(), OTPForgotPassActivity.class);
                    intent.putExtra("phoneNumber", ccp.getFullNumberWithPlus());
                    startActivity(intent);

            }
        });
    }
}