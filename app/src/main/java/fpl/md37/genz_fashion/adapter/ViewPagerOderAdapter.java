package fpl.md37.genz_fashion.adapter;

import android.support.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fpl.md37.genz_fashion.UserScreen.ActiveFragment;
import fpl.md37.genz_fashion.UserScreen.CancelledFragment;
import fpl.md37.genz_fashion.UserScreen.CompletedFragment;

public class ViewPagerOderAdapter extends FragmentStateAdapter {
    public ViewPagerOderAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ActiveFragment();
            case 1:
                return new CompletedFragment();
            case 2:
                return new CancelledFragment();
            default:
                return new ActiveFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
