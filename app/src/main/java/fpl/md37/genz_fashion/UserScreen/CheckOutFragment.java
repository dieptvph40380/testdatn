package fpl.md37.genz_fashion.UserScreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.genz_fashion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import fpl.md37.genz_fashion.ManagerScreen.SignUpActivity;
import fpl.md37.genz_fashion.adapter.AdapterCart;
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


public class CheckOutFragment extends Fragment {

    TextView tvName,tvPhone,tvAddress,totalcheckout,tvPayment,tvPC_ToTal,tvPC_Shipping,tvPC_Voucher,tvPC_Payment,Voucher,tvOrder,tvMethods;
    CheckBox cbChekOut;
    Client currentUserModel;
    ImageView btnBack;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Double PriceTotal =0.0,PriceShip =0.0,PriceVoucher =0.0,PricePayment=0.0;
    Context safeContext;    // Lưu trữ context an toàn
    private RecyclerView recyclerView;
    private CheckOutAdapter adapter;
    private HttpRequest httpRequest;
    private String selectedPaymentMethod;
    private String userId;
    CartData cartData;
    List<ProducItem> products;
    private ActivityResultLauncher<Intent> paymentMethodLauncher;

    public CheckOutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        safeContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_check_out, container, false);

        tvName=v.findViewById(R.id.tv_ClName);
        tvPhone=v.findViewById(R.id.tv_ClPhone);
        tvAddress=v.findViewById(R.id.tv_ClAddress);
        totalcheckout=v.findViewById(R.id.total_CheckOut);
        tvPayment=v.findViewById(R.id.tv_Payment);
        cbChekOut=v.findViewById(R.id.cb_CheckOut);
        tvPC_ToTal=v.findViewById(R.id.tv_TotalPrice);
        tvPC_Shipping=v.findViewById(R.id.tv_DiscountShipping);
        tvPC_Voucher=v.findViewById(R.id.tv_DiscountVouchers);
        tvPC_Payment=v.findViewById(R.id.tv_TotalPayment);
        Voucher=v.findViewById(R.id.Voucher_Cl);
        tvOrder=v.findViewById(R.id.tv_Order);
        btnBack=v.findViewById(R.id.btnBack);
        tvMethods=v.findViewById(R.id.tv_Methods);

        getUserData();

        recyclerView = v.findViewById(R.id.rcv_ClCheckOut);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CheckOutAdapter(getContext());
        recyclerView.setAdapter(adapter);
        httpRequest = new HttpRequest();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("CheckOutFragment", "User ID: " + userId);
            httpRequest.callApi().getOrder(userId).enqueue(getCartID);
        }

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        paymentMethodLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                        Bundle bundle = result.getData().getExtras(); // Lấy Bundle từ Intent
                        if (bundle != null) {
                            String selectedMethod = bundle.getString("selected_method"); // Lấy giá trị phương thức thanh toán
                            if (selectedMethod != null) {
                                tvMethods.setText(selectedMethod);
                                Log.d("CheckOutFragment", "Updated Payment Method: " + selectedMethod);
                                // Cập nhật TextView với phương thức thanh toán
                            }
                        }
                    }
                }
        );


        tvMethods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PayMothodsFragment.class);
                paymentMethodLauncher.launch(intent);
            }
        });

        cbChekOut.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPaymentMethod = tvPayment.getText().toString();
                Log.d("CheckOutFragment", "Selected Payment Method: " + selectedPaymentMethod);
                Toast.makeText(safeContext, "Selected Payment: " + selectedPaymentMethod, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(safeContext, "Payment method deselected", Toast.LENGTH_SHORT).show();
            }
        });

        tvOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbChekOut.isChecked()) {
                    // Kiểm tra điều kiện để đảm bảo các dữ liệu cần thiết đã có
                    if (currentUserModel == null || selectedPaymentMethod == null || cartData == null || cartData.getProducts().isEmpty()) {
                        Toast.makeText(safeContext, "Missing required data to place order.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Tạo đối tượng OrderRequest với id_client, payment_method và danh sách sản phẩm đã chọn
                    OrderRequest orderRequest = new OrderRequest(userId, selectedPaymentMethod, products);
                    Gson gson = new Gson();
                    String orderRequestJson = gson.toJson(orderRequest);
                    Log.d("OrderRequest", "Data sent to API: " + orderRequestJson);
                    // Gửi API addOrder
                    httpRequest.callApi().addOrder(orderRequest).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(safeContext, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getContext(), MyOrderActivity.class);
                                startActivity(intent);
                            } else {
                                // Log chi tiết lỗi từ server
                                try {
                                    if (response.errorBody() != null) {
                                        String errorResponse = response.errorBody().string();
                                        Log.e("OrderError", "Server error: " + errorResponse);
                                        Toast.makeText(safeContext, "Failed to place order: " + errorResponse, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e("OrderError", "Unknown server error.");
                                        Toast.makeText(safeContext, "Failed to place order: Unknown error.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Log.e("OrderError", "Error parsing errorBody: " + e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // Xử lý lỗi kết nối hoặc hệ thống
                            Toast.makeText(safeContext, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("OrderError", "Network error: " + t.getMessage());
                        }
                    });

                } else {
                    // Hiển thị thông báo nếu checkbox chưa được chọn
                    Toast.makeText(safeContext, "Please select the payment method before proceeding.", Toast.LENGTH_SHORT).show();
                }

                CreateOrder orderApi=new CreateOrder();
                try {
                    JSONObject data = orderApi.createOrder(String.valueOf(totalcheckout));
                    String code = data.getString("return_code");

                    if (code.equals("1")) {
                        String token=data.getString("zp_trans_token");
                        ZaloPaySDK.getInstance().payOrder(getActivity(), token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {

                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {

                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {

                            }
                        });
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });




        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new CartFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.bounce_in, R.anim.bounce_out);
                transaction.replace(R.id.layout_checkout, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        return v;
    }

//    @Override
//    public void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        // Xử lý Intent trong fragment
//        if (intent != null) {
//            ZaloPaySDK.getInstance().onResult(intent);
//        }
//    }

    void getUserData() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (isAdded() && safeContext != null) {
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
            }
        });
    }

    Callback<ResponseCart> getCartID = new Callback<ResponseCart>() {
        @Override
        public void onResponse(Call<ResponseCart> call, Response<ResponseCart> response) {
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
                // Hiển thị danh sách sản phẩm trong giỏ hàng
                adapter.setProducts(products);
            } else {
                Log.e("zzzzz Error", "Failed to fetch cart: " + response.message());
                Toast.makeText(getContext(), "Failed to fetch cart: " + response.message(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ResponseCart> call, Throwable t) {
            Log.e("zzzzz Failure", "Network error: " + t.getMessage());
            Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };


}