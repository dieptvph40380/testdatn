package fpl.md37.genz_fashion.ManagerScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.genz_fashion.R;

public class MainActivityManager extends AppCompatActivity {
    LinearLayout products,typeproduct,support,voucher,orders,supplierss,statis,infor,detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_admin);

        products = findViewById(R.id.products_admin);
        typeproduct = findViewById(R.id.producttype_admin);
        support = findViewById(R.id.support_admin);
        voucher = findViewById(R.id.voucher_admin);
        orders = findViewById(R.id.orders_admin);
        supplierss = findViewById(R.id.suppliers_admin);
        statis = findViewById(R.id.statis_admin);
        infor = findViewById(R.id.information_admin);
        detail = findViewById(R.id.ordersdetails_admin);

        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ProductsFragment());
            }
        });

        typeproduct.setOnClickListener(v -> {
            replaceFragment(new TypeProductFragment());
        });

        supplierss.setOnClickListener(v -> {
            replaceFragment(new SupplierFragment());
        });
        voucher.setOnClickListener(v -> {
            replaceFragment(new VoucherFragment());
        });

//        supplierss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivityManager.this, SuppliersFragment.class));
//            }
//        });

        statis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new StatisticalFragment());
            }
        });
         infor.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 replaceFragment(new InformationFragment());
             }
         });

    }
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.sile_right,R.anim.slide_left);
        transaction.replace(R.id.frameLayout1, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}