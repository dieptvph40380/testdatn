package fpl.md37.genz_fashion.UserScreen;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.genz_fashion.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RePassActivity extends AppCompatActivity {
    TextInputEditText NewPass,ReNewPass;
    Button btn_confirm;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_re_pass);

        NewPass=findViewById(R.id.NewPass);
        ReNewPass=findViewById(R.id.ReNewPass);
        btn_confirm=findViewById(R.id.btn_Confirm);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newPassword = NewPass.getText().toString().trim();
                String reNewPass = ReNewPass.getText().toString().trim();

                // Kiểm tra nếu các trường nhập liệu rỗng
                if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(reNewPass)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra độ dài mật khẩu mới
                if (newPassword.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra trùng khớp giữa mật khẩu mới và mật khẩu xác nhận
                if (!newPassword.equals(reNewPass)) {
                    Toast.makeText(getApplicationContext(), "Mật khẩu xác nhận không khớp với mật khẩu mới", Toast.LENGTH_SHORT).show();
                    return;
                }


                updatePassword(newPassword);
            }
        });

    }

    private void updatePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    // Chuyển người dùng về màn hình đăng nhập hoặc một màn hình khác sau khi đổi mật khẩu thành công
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

}