package fpl.md37.genz_fashion.UserScreen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.genz_fashion.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import fpl.md37.genz_fashion.ManagerScreen.Password_Manager;


public class SettingFragment extends Fragment {
    private ImageView btnBack_setting;
    private LinearLayout lin_pass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        btnBack_setting = view.findViewById(R.id.btnBack_setting);
        lin_pass= view.findViewById(R.id.PasswordManager);


        btnBack_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showBottomNav();
                Fragment newFragment = new ProfileFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);// hiệu ứng mơ dần
                transaction.replace(R.id.frameLayout_setting, newFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        lin_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                replaceFragment(new Password_Manager(), R.id.frameLayout);
            }
        });

        return view;
    }

    private void showBottomNav() {
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    private void replaceFragment(Fragment targetFragment, int frameId) {
        if (getActivity() != null) {
            View bottomNavigationView = getActivity().findViewById(R.id.bottom_nav);
            if (bottomNavigationView != null) {
                bottomNavigationView.setVisibility(View.GONE);
            }
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(frameId, targetFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

}
