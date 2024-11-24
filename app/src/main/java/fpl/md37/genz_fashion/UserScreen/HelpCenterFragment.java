package fpl.md37.genz_fashion.UserScreen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genz_fashion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import fpl.md37.genz_fashion.QaA.Question1;
import fpl.md37.genz_fashion.QaA.Question2;
import fpl.md37.genz_fashion.QaA.Question3;
import fpl.md37.genz_fashion.QaA.Question4;


public class HelpCenterFragment extends Fragment {
    private ImageView btnBack_help;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_center, container, false);
        btnBack_help = view.findViewById(R.id.btnBack_help);
        LinearLayout facebookItem = view.findViewById(R.id.facebookItem);
        LinearLayout whatsappItem = view.findViewById(R.id.whatsappItem);
        LinearLayout instagramItem = view.findViewById(R.id.instagramItem);
        TextView a =view.findViewById(R.id.a);
        TextView b =view.findViewById(R.id.b);
        TextView c =view.findViewById(R.id.c);
        TextView d =view.findViewById(R.id.d);

        a.setOnClickListener(v -> startActivity(new Intent(getActivity(),  Question1.class)));
        b.setOnClickListener(v -> startActivity(new Intent(getActivity(), Question2.class)));
        c.setOnClickListener(v -> startActivity(new Intent(getActivity(), Question3.class)));
        d.setOnClickListener(v -> startActivity(new Intent(getActivity(), Question4.class)));



        btnBack_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showBottomNav();
                Fragment newFragment = new ProfileFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);// hiệu ứng mơ dần
                transaction.replace(R.id.frameLayout_help, newFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        // Zalo
        whatsappItem.setOnClickListener(v -> openZaloAppWithNumber("0917089175"));

        // Instagram
        instagramItem.setOnClickListener(v -> openInstagramApp());

        // Facebook
        facebookItem.setOnClickListener(v -> openFacebookApp());


        return view;
    }


    // Mở Zalo
    private void openZaloAppWithNumber(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://zalo.me/" + phoneNumber));
            intent.setPackage("com.zing.zalo");
            startActivity(intent);
        } catch (Exception e) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://zalo.me/" + phoneNumber));
            startActivity(webIntent);
            Toast.makeText(getActivity(), "Không tìm thấy ứng dụng Zalo, mở trang web thay thế", Toast.LENGTH_SHORT).show();
        }
    }
    // Mở Facebook
    private void openFacebookApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/namth564?mibextid=LQQJ4d"));
            startActivity(intent);
        } catch (Exception e) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/namth564?mibextid=LQQJ4d"));
            startActivity(webIntent);
            Toast.makeText(getActivity(), "Không tìm thấy ứng dụng Facebook, mở trang web thay thế", Toast.LENGTH_SHORT).show();
        }
    }
    // Mở Instagram
    private void openInstagramApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://instagram.com/_u/iamth_nam"));
            intent.setPackage("com.instagram.android");
            startActivity(intent);
        } catch (Exception e) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/iamth_nam"));
            startActivity(webIntent);
            Toast.makeText(getActivity(), "Không tìm thấy ứng dụng Instagram, mở trang web thay thế", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBottomNav() {
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

}