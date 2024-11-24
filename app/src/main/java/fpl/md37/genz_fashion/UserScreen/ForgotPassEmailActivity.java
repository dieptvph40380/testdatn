package fpl.md37.genz_fashion.UserScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.genz_fashion.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import fpl.md37.genz_fashion.ManagerScreen.SignInActivity;

public class ForgotPassEmailActivity extends AppCompatActivity {

    TextInputEditText edtEmail;
    Button btnSendEmail;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pass_email);
        edtEmail = findViewById(R.id.edt_email);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        mAuth = FirebaseAuth.getInstance();

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    edtEmail.setError("Please enter your email");
                    return;
                }

                // Gửi email đặt lại mật khẩu
                sendPasswordResetEmail(email);

            }
        });

    }

    public void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(intent);
                            finish();
                            Snackbar.make(findViewById(android.R.id.content),
                                    "Password reset email sent. Please check your inbox.",
                                    Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(ForgotPassEmailActivity.this,
                                    "Password reset email sent. Please check your inbox: " ,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPassEmailActivity.this,
                                    "Error: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }


}