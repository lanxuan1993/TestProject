package com.example.mvp.module.main.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;


import com.jiongbook.evaluation.R;
import com.jiongbook.evaluation.databinding.ActivityMainTab2Binding;
import com.jiongbook.evaluation.databinding.TabBottomGroupBinding;
import com.mvp.base.BaseActivity;

import com.example.mvp.module.main.contract.MainContract;
import com.example.mvp.module.main.presenter.MainPresenter;

public class MainTabActivity2 extends BaseActivity<MainPresenter>
        implements MainContract.View, View.OnClickListener {
    private static final String TAG = MainTabActivity2.class.getSimpleName();
    private ActivityMainTab2Binding viewBinding;
    private TabBottomGroupBinding tabViewBinding;
    FrameLayout containerFl;
    RadioGroup tabRg;
    RadioButton homeRb;
    RadioButton serviceRb;
    RadioButton discoverRb;
    RadioButton mineRb;

    private String[] mFragmentTag = new String[]{"home", "service", "discover", "mine"};
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private ServiceFragment serviceFragment;
    private DiscoverFragment discoverFragment;
    private MineFragment mineFragment;

    public static final int HOME = 0;
    public static final int WEALTH = 1;
    public static final int MESSAGE = 2;
    public static final int MINE = 3;
    public static int mCurrIndex = HOME;

    @Override
    protected View getLayoutView() {
        viewBinding = ActivityMainTab2Binding.inflate(getLayoutInflater());
        View rootView = viewBinding.getRoot();
        return rootView;
    }

    @Override
    protected int getLayoutResId() {
        return 0;
    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCurrIndex = extras.getInt("index", 0);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        containerFl = viewBinding.flContainer;
        tabViewBinding = viewBinding.tab;
        tabRg = tabViewBinding.rgTab;
        homeRb = tabViewBinding.rbHome;
        serviceRb = tabViewBinding.rbAction;
        discoverRb = tabViewBinding.rbStudy;
        mineRb = tabViewBinding.rbMine;
        fragmentManager = getSupportFragmentManager();
        homeRb.setOnClickListener(this);
        serviceRb.setOnClickListener(this);
        discoverRb.setOnClickListener(this);
        mineRb.setOnClickListener(this);

        showCurrFragment(HOME);
    }

    public void showCurrFragment(int index) {
        mCurrIndex = index;
        if (mCurrIndex == HOME) {
            tabRg.check(R.id.rb_home);
        } else if (mCurrIndex == WEALTH) {
            tabRg.check(R.id.rb_action);
        } else if (mCurrIndex == MESSAGE) {
            tabRg.check(R.id.rb_study);
        } else if (mCurrIndex == MINE) {
            tabRg.check(R.id.rb_mine);
        }
        showFragment();
    }

    @Override
    public void onClick(View view) {
        if (homeRb.equals(view)) {
            mCurrIndex = HOME;
        } else if (serviceRb.equals(view)) {
            mCurrIndex = WEALTH;
        } else if (discoverRb.equals(view)) {
            mCurrIndex = MESSAGE;
        } else if (mineRb.equals(view)) {
            mCurrIndex = MINE;
        }
        showFragment();
    }

    private void showFragment() {
        fragmentTransaction = fragmentManager.beginTransaction();
        for (int i = 0; i < mFragmentTag.length; i++) {
            // 预加载所有fragment
            Fragment fragment = fragmentManager.findFragmentByTag(mFragmentTag[i]);
            if (fragment == null) {
                fragment = instantFragment(i);
            }
            if (!fragment.isAdded()) {
                fragmentTransaction.add(R.id.fl_container, fragment, mFragmentTag[i]);
            }

            //显示当前fragment,隐藏其他fragment
            if (i == mCurrIndex) {
                fragmentTransaction.show(fragment);
                fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.RESUMED);
            } else {
                fragmentTransaction.hide(fragment);
                fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.STARTED);
            }
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    private Fragment instantFragment(int currIndex) {
        switch (currIndex) {
            case WEALTH:
                return new ServiceFragment();
            case MESSAGE:
                return new DiscoverFragment();
            case MINE:
                return new MineFragment();
            case HOME:
            default:
                return new HomeFragment();
        }
    }

    @Override
    protected void requestData() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(Throwable throwable) {

    }
}