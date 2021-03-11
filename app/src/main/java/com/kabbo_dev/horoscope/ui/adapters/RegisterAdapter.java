package com.kabbo_dev.horoscope.ui.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kabbo_dev.horoscope.ui.fragments.SignInFragment;
import com.kabbo_dev.horoscope.ui.fragments.SignUpFragment;

public class RegisterAdapter extends FragmentStateAdapter {

    Context context;
    int totalTabs;

    public RegisterAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context, int totalTabs) {
        super(fragmentManager, lifecycle);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return new SignInFragment();

            case 1:
                return new SignUpFragment();

            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return totalTabs;
    }

}