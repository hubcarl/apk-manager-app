package com.blue.sky.apk.manager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import com.blue.sky.apk.manager.adapter.TabPagerAdapter;
import com.blue.sky.apk.manager.fragment.ApkFragment;
import com.blue.sky.apk.manager.fragment.AppFragment;
import com.blue.sky.apk.manager.fragment.RunningAppFragment;
import com.blue.sky.common.sdk.SDK;
import com.blue.sky.common.utils.UIHelp;
import com.blue.sky.control.astuetz.PagerSlidingTabStrip;

import com.blue.sky.control.qrcode.CaptureActivity;
import com.wandoujia.ads.sdk.fragment.AppListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sky on 2014/10/13.
 */
public class ApkMainActivity extends FragmentActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private TabPagerAdapter adapter;
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private final String[] titles = { "已安装", "安装包","运行中","软件", "游戏"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apk_activity_main);
        fragments.add(new AppFragment());
        fragments.add(new ApkFragment());
        fragments.add(new RunningAppFragment());

        fragments.add(getAdFragment(SDK.WDJ_AD_APP_LIST_APP,"APP"));
        fragments.add(getAdFragment(SDK.WDJ_AD_APP_LIST_GAME,"GAME"));

        tabs = (PagerSlidingTabStrip) findViewById(R.id.video_pager_tabs);
        pager = (ViewPager) findViewById(R.id.video_pager);
        adapter = new TabPagerAdapter(getSupportFragmentManager(),pager, fragments, titles);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        tabs.setTabsStyle(getResources().getDisplayMetrics());
        UIHelp.setHeaderView(this, "应用程序管理", false, "  ", R.drawable.qr_code_short, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApkMainActivity.this,CaptureActivity.class);
                startActivity(intent);
            }
        });

        SDK.initWDJSDK(this);
    }

    private Fragment getAdFragment(String tag, String category){
        AppListFragment localTabsFragment = new AppListFragment();
        Bundle localBundle = new Bundle();
        localBundle.putString("category", category);
        localBundle.putInt("detail_container_id", R.id.container);
        localBundle.putString("detail_back_stack_name", "");
        localBundle.putString("tag", tag);
        localTabsFragment.setArguments(localBundle);
        return localTabsFragment;
    }

    /**
     * 点击返回键只返回桌面不关闭程序
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(intent);
            return true;
        }
        /** 按下其它键，调用父类方法，进行默认操作 */
        return super.dispatchKeyEvent(event);
    }


}