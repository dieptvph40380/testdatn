package fpl.md37.genz_fashion.UserScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.genz_fashion.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPForgotPassActivity extends AppCompatActivity {
    EditText edt_otp1, edt_otp2, edt_otp3, edt_otp4, edt_otp5, edt_otp6;
    Long timout = 60L;
    TextView resendTv;
    String phoneNumber;
    ProgressBar progressBar;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    FirebaseAuth firebaseAuth;
    private PhoneAuthCredential autoRetrievedCredential;
    Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_forgot_pass);

        edt_otp1 = findViewById(R.id.edt_otp1);
        edt_otp2 = findViewById(R.id.edt_otp2);
        edt_otp3 = findViewById(R.id.edt_otp3);
        edt_otp4 = findViewById(R.id.edt_otp4);
        edt_otp5 = findViewById(R.id.edt_otp5);
        edt_otp6 = findViewById(R.id.edt_otp6);
        btn_confirm = findViewById(R.id.btn_confirm);
        resendTv = findViewById(R.id.tv_resend);
        progressBar = findViewById(R.id.progressbar);

        firebaseAuth = FirebaseAuth.getInstance();

        if (getIntent().hasExtra("phoneNumber")) {
            phoneNumber = getIntent().getStringExtra("phoneNumber");
        } else {
            Toast.makeText(this, "Phone number not provided", Toast.LENGTH_SHORT).show();
            finish();  // Kết thúc activity nếu không có số điện thoại
            return;
        }

        setupOTPInputs();

        sendOTP(phoneNumber,false);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = edt_otp1.getText().toString() +
                        edt_otp2.getText().toString() +
                        edt_otp3.getText().toString() +
                        edt_otp4.getText().toString() +
                        edt_otp5.getText().toString() +
                        edt_otp6.getText().toString();

                // Kiểm tra nếu mã OTP có đủ 6 ký tự
                if (otp.length() == 6) {
                    Toast.makeText(OTPForgotPassActivity.this, "OTP entered: " + otp, Toast.LENGTH_SHORT).show();
                    Log.d("OTPForgotPassActivity", "OTP entered successfully: " + otp);
                    Log.d("OTPForgotPassActivity", "OTP entered successfully: " + phoneNumber);
                } else {
                    Toast.makeText(OTPForgotPassActivity.this, "Please enter a 6-digit OTP", Toast.LENGTH_SHORT).show();
                    Log.w("OTPForgotPassActivity", "Incomplete OTP entered: " + otp);
                }

                if (verificationCode != null && !verificationCode.isEmpty()) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                    setInprogress(true);
                    // Nếu xác thực thành công, chuyển sang Activity đặt lại mật khẩu
                    startActivity(new Intent(OTPForgotPassActivity.this, RePassActivity.class));
                    setInprogress(false);  // Kết thúc trạng thái loading
                } else {
                    Toast.makeText(OTPForgotPassActivity.this, "Vui lòng đợi mã xác minh được gửi", Toast.LENGTH_SHORT).show();
                }

            }
        });


        resendTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOTP(phoneNumber,true);
            }
        });

    }

    private void setupOTPInputs() {
        TextWatcher otpTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra nếu có ký tự nhập vào thì chuyển tiếp
                if (s.length() == 1) {
                    if (edt_otp1.isFocused()) {
                        edt_otp2.requestFocus();
                    } else if (edt_otp2.isFocused()) {
                        edt_otp3.requestFocus();
                    } else if (edt_otp3.isFocused()) {
                        edt_otp4.requestFocus();
                    } else if (edt_otp4.isFocused()) {
                        edt_otp5.requestFocus();
                    } else if (edt_otp5.isFocused()) {
                        edt_otp6.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        // Gắn TextWatcher vào từng EditText
        edt_otp1.addTextChangedListener(otpTextWatcher);
        edt_otp2.addTextChangedListener(otpTextWatcher);
        edt_otp3.addTextChangedListener(otpTextWatcher);
        edt_otp4.addTextChangedListener(otpTextWatcher);
        edt_otp5.addTextChangedListener(otpTextWatcher);
        edt_otp6.addTextChangedListener(otpTextWatcher);
    }

    public void sendOTP(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInprogress(true);
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions
                        .newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timout, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Log.d("Login_OTP2", "onVerificationCompleted triggered");
                                setInprogress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(OTPForgotPassActivity.this, "OTP gửi thất bại", Toast.LENGTH_SHORT).show();
                                Log.d("OTP gửi thất bại", "OTP gửi thất bại: "+e.getMessage());
                                setInprogress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                                Toast.makeText(OTPForgotPassActivity.this, "OTP đã được gửi", Toast.LENGTH_SHORT).show();
                                Log.d("OTP đã được gửi: ", "OTP đã được gửi: " );
                                Log.d("OTP đã được gửi: ", "OTP đã được gửi: "+verificationCode );
                                Log.d("OTP đã được gửi: ", "OTP đã được gửi: "+resendingToken );
                                setInprogress(false);
                            }
                        });
        if (isResend) {
            if (resendingToken != null) {
                PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
            } else {
                Toast.makeText(OTPForgotPassActivity.this, "Token hết hạn, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                setInprogress(false);
            }
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    public void setInprogress(boolean inprogress) {
        if (inprogress) {
            progressBar.setVisibility(View.VISIBLE);
            btn_confirm.setEnabled(false); // Vô hiệu hóa nút
        } else {
            progressBar.setVisibility(View.GONE);
            btn_confirm.setEnabled(true); // Kích hoạt lại nút
        }
    }

    public void startResendTimer() {
        resendTv.setEnabled(false);
        timout = 60L; // Khởi tạo lại biến timout nếu cần thiết
        new CountDownTimer(timout * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timout--;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resendTv.setText("Resend OTP in " + timout );
                    }
                });
            }

            @Override
            public void onFinish() {
                timout = 60L;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resendTv.setText("Resend OTP ");
                        resendTv.setEnabled(true);
                    }
                });
            }
        }.start();
    }

}