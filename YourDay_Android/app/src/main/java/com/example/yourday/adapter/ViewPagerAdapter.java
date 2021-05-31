package com.example.yourday.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.yourday.fragment.SettingFragment;
import com.example.yourday.fragment.TodayFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TodayFragment();
            case 1:
                return new SettingFragment();
            default:
                return new TodayFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
