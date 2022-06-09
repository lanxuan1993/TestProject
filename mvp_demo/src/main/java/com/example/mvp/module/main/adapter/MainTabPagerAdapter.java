package com.example.mvp.module.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;


import com.example.widget.textview.CustomTextView;
import com.google.android.material.tabs.TabLayout;
import com.jiongbook.evaluation.R;

import java.util.ArrayList;

public class MainTabPagerAdapter extends FragmentPagerAdapter {

    public int[] tabName = new int[]{
            R.string.main_home,
            R.string.main_category,
            R.string.main_service,
            R.string.main_mine
    };

    private int[] tabIcon = {
            R.drawable.main_btn_home,
            R.drawable.main_btn_service,
            R.drawable.main_btn_discover,
            R.drawable.main_btn_mine
    };

    private ArrayList<Fragment> mFragments;

    public MainTabPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragments = fragments;
    }

    public View getTabView(Context context, TabLayout tabLayout, int position, String tabId) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, tabLayout, false);

        CustomTextView title = view.findViewById(R.id.title);
        ImageView icon = view.findViewById(R.id.icon);
        title.setText(tabName[position]);
        icon.setImageResource(tabIcon[position]);

        return view;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFragments.get(position).hashCode();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}

