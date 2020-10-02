package com.azuka.android.trakcerapps.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.azuka.android.trakcerapps.Tab.TabOne;
import com.azuka.android.trakcerapps.Tab.TabTwo;

public class TabAdapter extends FragmentStatePagerAdapter {

    int behavior;

    public TabAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.behavior = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                return new TabOne();
            case 1:
                return new TabTwo();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return behavior;
    }
}