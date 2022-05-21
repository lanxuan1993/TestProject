package com.example.mvp.module.main.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.mvp.R;
import com.example.mvp.databinding.ActivityMainTabBinding;
import com.example.mvp.module.main.adapter.MainTabPagerAdapter;
import com.example.mvp.module.main.contract.MainContract;
import com.example.mvp.module.main.presenter.MainPresenter;
import com.example.widget.textview.CustomTextView;
import com.google.android.material.tabs.TabLayout;
import com.mvp.base.BaseActivity;

import java.util.ArrayList;

public class MainTabActivity extends BaseActivity<MainPresenter>
        implements MainContract.View {
    private static final String TAG = MainTabActivity.class.getSimpleName();

    private ActivityMainTabBinding viewBinding;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    public static final String CHANGE_TAB = "change_tab";
    public static final String TAB_ID_INDEX = "index";
    public static final String TAB_ID_CATEGORY = "category";
    public static final String TAB_ID_DISCOVER = "discover";
    public static final String TAB_ID_ACCOUNT = "account";
    public static final String TAB_ID_NEW_TAB = "newTab";

    public String[] tabId = new String[]{
            TAB_ID_INDEX,
            TAB_ID_CATEGORY,
            TAB_ID_DISCOVER,
            TAB_ID_ACCOUNT
    };

    private ArrayList<Fragment> mFragments = new ArrayList<>();

    private MainTabPagerAdapter mAdapter;

    private int mBackKeyPressedCount;

    @Override
    protected View getLayoutView() {
        viewBinding = ActivityMainTabBinding.inflate(getLayoutInflater());
        View rootView = viewBinding.getRoot();
        return rootView;
    }

    @Override
    protected int getLayoutResId() {
        return 0;
    }

//    @Override
//    protected int getLayoutResId() {
//        return R.layout.activity_main;
//    }

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void initView() {
        super.initView();
        mViewPager = viewBinding.tabPager;
        mTabLayout = viewBinding.tabLayout;
        initTabFragment();
        changeTab(getIntent());
    }

    @Override
    protected void requestData() {

    }

    public int getPositionFromPageId(String pageId) {
        for (int i = 0; i < tabId.length; i++) {
            String tabIdStr = tabId[i];
            if (TextUtils.equals(tabIdStr, pageId)) {
                return i;
            }
        }
        return -1;
    }

    public String getPageIdByPosition(int position) {
        if (position < 0 || position >= tabId.length) {
            position = 0;
        }
        return tabId[position];
    }

    private boolean changeTab(Intent intent) {
        String pageId = intent.getStringExtra(CHANGE_TAB);
        int index = getPositionFromPageId(pageId);
        if (index != -1) {
            mViewPager.setCurrentItem(index, false);
            return true;
        }
        return false;
    }

    public boolean changeTab(int index) {
        if (index >= 0 && index < mFragments.size()) {
            mViewPager.setCurrentItem(index, false);
            return true;
        }
        return false;
    }


    private void initTabFragment() {
        //初始化底部导航栏信息
        initFragment();
        mAdapter = new MainTabPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(1);

        mTabLayout.setupWithViewPager(mViewPager);
        // 清除掉TabLayout中的ViewPagerOnTabSelectedListener
        // 并重写onTabSelected方法，实现没有滑动立即切换Tab
        mTabLayout.clearOnTabSelectedListeners();
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), false);
            }
        });

        mTabLayout.setClipChildren(false);
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                String pageId = getPageIdByPosition(i);
                View view = mAdapter.getTabView(this, mTabLayout, i, pageId);
                if (view == null) {
                    finish();
                    return;
                }

                tab.setCustomView(view);
                if (TAB_ID_INDEX.equals(pageId)) {
                    view.setSelected(true);
                    view.setOnTouchListener(new View.OnTouchListener() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            return false;
                        }
                    });
                }
            }
        }
        if (mFragments.size() == 5) {
            ViewGroup realTabs = ((ViewGroup) mTabLayout.getChildAt(0));
            if (realTabs != null) {
                realTabs.setClipToPadding(false);
                realTabs.setClipChildren(false);
                //获取NEW TAB的position
                int newTabPosition = getPositionFromPageId(TAB_ID_NEW_TAB);
                if (realTabs.getChildAt(newTabPosition) instanceof ViewGroup) {
                    ViewGroup realTab0 = (ViewGroup) realTabs.getChildAt(newTabPosition);
                    realTab0.setClipChildren(false);
                    realTab0.setClipToPadding(false);
                }
            }
        }

        mViewPager.addOnPageChangeListener(onPageChangeListener);
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        private int previousPosition = 0;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (mTabLayout != null) {
                // 获取上一个tab，字体变为默认粗细
                TabLayout.Tab tab = mTabLayout.getTabAt(previousPosition);
                if (tab != null) {
                    TabLayout.TabView tabView = tab.view;
                    ((CustomTextView) tabView.findViewById(R.id.title)).setTypeface(Typeface.DEFAULT);
                }
                // 获取当前tab，字体加粗
                TabLayout.Tab currentTab = mTabLayout.getTabAt(position);
                if (currentTab != null) {
                    TabLayout.TabView tabView = currentTab.view;
                    ((CustomTextView) tabView.findViewById(R.id.title)).setTypeface(Typeface.DEFAULT_BOLD);
                }
            }
            previousPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private void initFragment() {
        mFragments = new ArrayList<>();
        for (String tabIdStr : tabId) {
            switch (tabIdStr) {
                case TAB_ID_INDEX:
                    mFragments.add(new HomeFragment());
                    break;
                case TAB_ID_CATEGORY:
                    mFragments.add(new ServiceFragment());
                    break;
                case TAB_ID_DISCOVER:
                    mFragments.add(new DiscoverFragment());
                    break;
                case TAB_ID_ACCOUNT:
                    mFragments.add(new MineFragment());
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        mBackKeyPressedCount++;
        if (mBackKeyPressedCount == 2) {
            this.finish();
        } else {
            Toast.makeText(this, "再次點選返回鍵即可退出",
                    Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBackKeyPressedCount = 0;
                }
            }, 2 * 1000);
        }
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