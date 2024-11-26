package fpl.md37.genz_fashion.UserScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import fpl.md37.genz_fashion.adapter.CheckOutAdapter;
import fpl.md37.genz_fashion.api.CreateOrder;
import fpl.md37.genz_fashion.api.HttpRequest;
import fpl.md37.genz_fashion.models.CartData;
import fpl.md37.genz_fashion.models.Client;
import fpl.md37.genz_fashion.models.OrderRequest;
import fpl.md37.genz_fashion.models.ProducItem;
import fpl.md37.genz_fashion.models.ResponseCart;
import fpl.md37.genz_fashion.utils.AndroidUtil;
import fpl.md37.genz_fashion.utils.FirebaseUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class CheckOutActivity extends AppCompatActivity {

    TextView tvName, tvPhone, tvAddress, totalcheckout, tvPayment, tvPC_ToTal, tvPC_Shipping, tvPC_Voucher, tvPC_Payment, Voucher, tvOrder, tvMethods;
    CheckBox cbChekOut;
    Client currentUserModel;
    ImageView btnBack;
    Double PriceTotal = 0.0, PriceShip = 0.0, PriceVoucher = 0.0, PricePayment = 0.0;
    Context safeContext;
    private RecyclerView recyclerView;
    private CheckOutAdapter adapter;
    private HttpRequest httpRequest;
    private String selectedPaymentMethod;
    private String userId;
    CartData cartData;
    String totalString;
    List<ProducItem> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_check_out);

        tvName = findViewById(R.id.tv_ClName);
        tvPhone = findViewById(R.id.tv_ClPhone);
        tvAddress = findViewById(R.id.tv_ClAddress);
        totalcheckout = findViewById(R.id.total_CheckOut);
        tvPayment = findViewById(R.id.tv_Payment);
        cbChekOut = findViewById(R.id.cb_CheckOut);
        tvPC_ToTal = findViewById(R.id.tv_TotalPrice);
        tvPC_Shipping = findViewById(R.id.tv_DiscountShipping);
        tvPC_Voucher = findViewById(R.id.tv_DiscountVouchers);
        tvPC_Payment = findViewById(R.id.tv_TotalPayment);
        Voucher = findViewById(R.id.Voucher_Cl);
        tvOrder = findViewById(R.id.tv_Order);
        btnBack = findViewById(R.id.btnBack);
        tvMethods = findViewById(R.id.tv_Methods);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        getUserData();

        recyclerView = findViewById(R.id.rcv_ClCheckOut);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CheckOutAdapter(this);
        recyclerView.setAdapter(adapter);
        httpRequest = new HttpRequest();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("CheckOutActivity", "User ID: " + userId);
            httpRequest.callApi().getOrder(userId).enqueue(getCartIDAc);
        }


        tvMethods.setOnClickListener(view -> {
            Intent intent = new Intent(CheckOutActivity.this, PayMothodsFragment.class);
            startActivityForResult(intent, 100);
        });

        cbChekOut.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPaymentMethod = tvPayment.getText().toString();
                Log.d("CheckOutActivity", "Selected Payment Method: " + selectedPaymentMethod);
             /*   Toast.makeText(safeContext, "Selected Payment: " + selectedPaymentMethod, Toast.LENGTH_SHORT).show();*/
            } else {
                Toast.makeText(safeContext, "Payment method deselected", Toast.LENGTH_SHORT).show();
            }
        });

        tvOrder.setOnClickListener(view -> {
            CreateOrder orderApi = new CreateOrder();
            Log.d("CreateOrder", "Response data: " + totalString);
            try {
                JSONObject data = orderApi.createOrder(totalString);
                String code = data.getString("return_code");
                Log.d("CreateOrder", "Response data: " + data.toString());

                if (code.equals("1")) {
                    String token = data.getString("zp_trans_token");
                    ZaloPaySDK.getInstance().payOrder(CheckOutActivity.this, token, "demozpdk://app", new PayOrderListener() {
                        @Override
                        public void onPaymentSucceeded(String s, String s1, String s2) {
                            Intent intent1=new Intent(CheckOutActivity.this, PaymentNotication.class);
                            intent1.putExtra("result","Thanh toán thành công");

                            startActivity(intent1);
                        }

                        @Override
                        public void onPaymentCanceled(String s, String s1) {
                            Intent intent1=new Intent(CheckOutActivity.this, PaymentNotication.class);
                            intent1.putExtra("result","Hủy thanh toán");
                            Log.d("ZaloPay", "Payment Canceled");
                            startActivity(intent1);
                        }

                        @Override
                        public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                            Intent intent1=new Intent(CheckOutActivity.this, PaymentNotication.class);
                            intent1.putExtra("result","Lỗi thanh toán");
                            Log.e("ZaloPay", "Payment Error: " + zaloPayError);
//                          q // Nếu có
                            Log.e("ZaloPay", "Additional Info: " + s + ", " + s1);

                            startActivity(intent1);
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        btnBack.setOnClickListener(view -> {
            Fragment newFragment = new CartFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.bounce_in, R.anim.bounce_out);
            transaction.replace(R.id.layout_checkout, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("ZaloPay", "onNewIntent triggered");
        ZaloPaySDK.getInstance().onResult(intent);
    }

    void getUserData() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                currentUserModel = task.getResult().toObject(Client.class);

                if (currentUserModel != null) {
                    tvName.setText(currentUserModel.getName());
                    tvPhone.setText(currentUserModel.getPhone());
                    tvAddress.setText(currentUserModel.getAddress());
                } else {
                    AndroidUtil.showToast(safeContext, "User data not found.");
                }
            } else {
                AndroidUtil.showToast(safeContext, "Failed to fetch user data.");
            }
        });
    }

    Callback<ResponseCart> getCartIDAc = new Callback<ResponseCart>() {
        @Override
        public void onResponse(Call<ResponseCart> call, Response<ResponseCart>response) {
            Log.d("zzzz Call", "URL: " + call.request().url());
            if (response.isSuccessful()) {
                // Log toàn bộ phản hồi để kiểm tra
                String jsonResponse = new Gson().toJson(response.body());
                Log.d("zzzzz Response", "Response: " + jsonResponse);
                cartData = response.body().getData();
                double totalPrice = cartData.getTotalPrice();
                String idCart = cartData.getId();
                Log.d("CheckOutFragment", "Cart ID: " + idCart);
                products = cartData.getProducts();

//                String voucher =Voucher.getText().toString();
                String voucher =tvPC_Voucher.getText().toString();
                PriceVoucher=Double.parseDouble(voucher);

                String ship= tvPC_Shipping.getText().toString();
                PriceShip=Double.parseDouble(ship);
                tvPC_ToTal.setText("" + totalPrice);
                PricePayment= totalPrice-(PriceVoucher+PriceShip);
                tvPC_Payment.setText(""+PricePayment);
                totalcheckout.setText(""+PricePayment);
                totalString = String.format("%.0f", PricePayment);
                // Hiển thị danh sách sản phẩm trong giỏ hàng
                adapter.setProducts(products);
            } else {
                Log.e("zzzzz Error", "Failed to fetch cart: " + response.message());
                Toast.makeText(CheckOutActivity.this, "Failed to fetch cart: " + response.message(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ResponseCart> call, Throwable t) {
            Log.e("zzzzz Failure", "Network error: " + t.getMessage());
            Toast.makeText(CheckOutActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };



}
