package fpl.md37.genz_fashion.ManagerScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.genz_fashion.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Password_Manager extends Fragment {
    TextInputEditText edt_Current,edt_NewPass,edt_Confirm;
    FirebaseAuth mAuth;
    Button btnChangePassword;


    public Password_Manager() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_password_manager, container, false);

        mAuth=FirebaseAuth.getInstance();

        edt_Current=v.findViewById(R.id.edt_CurrentPass);
        edt_NewPass=v.findViewById(R.id.edt_NewPass);
        edt_Confirm=v.findViewById(R.id.edt_ConfirmPass);
        btnChangePassword=v.findViewById(R.id.btnChange);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPassword = edt_Current.getText().toString().trim();
                String newPassword = edt_NewPass.getText().toString().trim();
                String confirmPassword = edt_Confirm.getText().toString().trim();

                // Kiểm tra nếu các trường nhập liệu rỗng
                if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra độ dài mật khẩu mới
                if (newPassword.length() < 6) {
                    Toast.makeText(getContext(), "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra trùng khớp giữa mật khẩu mới và mật khẩu xác nhận
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp với mật khẩu mới", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra mật khẩu mới không giống mật khẩu hiện tại
                if (currentPassword.equals(newPassword)) {
                    Toast.makeText(getContext(), "Mật khẩu mới phải khác mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Nếu các điều kiện trên đều thỏa mãn, tiến hành đổi mật khẩu

                updatePassword(currentPassword, newPassword);

                Intent intent = new Intent(getContext(), SignInActivity.class);
                startActivity(intent);

            }
        });

        return  v;
    }

    private void updatePassword(String currentPassword, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            // Xác thực lại người dùng trước khi đổi mật khẩu
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}