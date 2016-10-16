package com.blue.sky.apk.manager.fragment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import com.blue.sky.apk.manager.R;
import com.blue.sky.apk.manager.adapter.ApkListAdapter;
import com.blue.sky.apk.manager.model.AppInfo;
import com.blue.sky.apk.manager.utils.AppUtil;
import com.blue.sky.common.component.util.FileUtils;
import com.blue.sky.common.component.util.PackageUtils;
import com.blue.sky.common.fragment.BaseFragment;
import com.blue.sky.common.utils.UIHelp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2014/10/14.
 */
public class ApkFragment extends BaseFragment {


    private View rootView;
    private ListView listView;
    private List<AppInfo> apkList = new ArrayList<AppInfo>();
    private ApkListAdapter apkListAdapter;
    private AppInfo selectedAppInfo;

    private TextView txtAppOpen, txtUnInstall, txtDetail, txtAppInstall, txtAppUpgrade, txtDelete;
    private View popView;
    private WindowManager windowManager;

    private TextView tipMessage;
    private ProgressBar progressBar;
    private LinearLayout loading;

    private int scanFlag = 0, tempScanFlag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.sky_activity_common_list, container, false);

        loading = (LinearLayout) rootView.findViewById(R.id.loading);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        tipMessage = (TextView) rootView.findViewById(R.id.tip_message);


        listView = (ListView) rootView.findViewById(R.id.list_view);
        apkListAdapter = new ApkListAdapter(getActivity(), apkList, 1);
        listView.setAdapter(apkListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAppInfo = apkList.get(position);
                showWindow(view);
            }
        });

        initApkList();
        initPopView();

        return rootView;
    }

    private void initPopView() {
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        popView = layoutInflater.inflate(R.layout.apk_list_menu_item, null);
        txtAppOpen = (TextView) popView.findViewById(R.id.app_open);
        txtUnInstall = (TextView) popView.findViewById(R.id.app_unInstall);
        txtDetail = (TextView) popView.findViewById(R.id.app_detail);
        txtAppInstall = (TextView) popView.findViewById(R.id.app_install);
        txtDelete = (TextView) popView.findViewById(R.id.app_delete);
        txtAppUpgrade = (TextView) popView.findViewById(R.id.app_upgrade);

        txtAppOpen.setOnClickListener(this);
        txtUnInstall.setOnClickListener(this);
        txtDetail.setOnClickListener(this);
        txtAppInstall.setOnClickListener(this);
        txtDelete.setOnClickListener(this);
        txtAppUpgrade.setOnClickListener(this);

        windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH:
                    apkList.addAll((List<AppInfo>) msg.obj);
                    apkListAdapter.notifyDataSetChanged();
                    break;
                case MSG_FINISHED:
                    progressBar.setVisibility(View.GONE);
                    tipMessage.setText("扫描完成, 总共有" + apkList.size() + "个安装包");
                    apkListAdapter.notifyDataSetChanged();
                    break;
                case HIDE_LOADING:
                    loading.setVisibility(View.GONE);
                    break;
                case MSG_EMPTY:
                    loading.setVisibility(View.GONE);
                    UIHelp.showToast(getActivity(), "没有找到SD卡");
                    break;
            }
        }
    };

    private void initApkList() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    scanFile(Environment.getExternalStorageDirectory());
                }
            }).start();
            initScanFileStatusTask();
        } else {
            handler.sendEmptyMessage(MSG_EMPTY);
        }
    }

    private void initScanFileStatusTask() {
        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (scanFlag == tempScanFlag) {
                    Log.i(">>>initScanFileStatusTask:", "finished");
                    timer.cancel();
                    sendMessage(handler, MSG_FINISHED, null);
                    sendMessageDelayed(handler, HIDE_LOADING, null, 2000);
                } else {
                    tempScanFlag = scanFlag;
                }
            }
        };
        timer.schedule(task, 1000, 1000);
    }

    // 遍历接收一个文件路径，然后把文件子目录中的所有文件遍历并输出来
    private void scanFile(File root) {
        scanFlag++;
        File files[] = root.listFiles();
        if (files != null) {
            List<AppInfo> apkList = new ArrayList<AppInfo>();
            for (File f : files) {
                scanFlag++;
                if (f.isDirectory()) {
                    scanFile(f);
                } else {
                    if (f.getName().endsWith(".apk")) {
                        AppInfo appInfo = AppUtil.getApkInfo(getActivity(), f.getAbsolutePath());
                        if(appInfo!=null){
                            apkList.add(appInfo);
                        }
                    }
                }
            }
            if(apkList.size()>0){
                sendMessage(handler, MSG_REFRESH, apkList);
            }
        }
    }

    private PopupWindow popupWindow;

    private void showWindow(View parent) {

        int[] location = new int[2];
        //location [0]--->x坐标,location [1]--->y坐标
        parent.getLocationInWindow(location);

        // 创建一个PopuWidow对象
        popupWindow = new PopupWindow(popView, windowManager.getDefaultDisplay().getWidth(), UIHelp.dip2px(getActivity(), 56));
        // 焦点
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        boolean isInstalled = AppUtil.isAppInstalled(getActivity(), selectedAppInfo);
        boolean isUpgrade = AppUtil.isAppUpgrade(getActivity(), selectedAppInfo);

        if (isInstalled) {
            txtAppOpen.setVisibility(View.VISIBLE);
            txtUnInstall.setVisibility(View.VISIBLE);
            txtDetail.setVisibility(View.VISIBLE);
            txtAppInstall.setVisibility(View.GONE);
            if (isUpgrade) {
                txtAppUpgrade.setVisibility(View.VISIBLE);
            } else {
                txtAppUpgrade.setVisibility(View.GONE);
            }
        } else {
            txtAppOpen.setVisibility(View.GONE);
            txtUnInstall.setVisibility(View.GONE);
            txtDetail.setVisibility(View.GONE);
            txtAppUpgrade.setVisibility(View.GONE);
            txtAppInstall.setVisibility(View.VISIBLE);
        }
        popupWindow.showAsDropDown(parent, location[0], 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_open:
                PackageUtils.startApp(getActivity(), selectedAppInfo.getPackageName());
                popupWindow.dismiss();
                break;
            case R.id.app_unInstall:
                popupWindow.dismiss();
                PackageUtils.uninstall(getActivity(), selectedAppInfo.getPackageName());
                apkListAdapter.notifyDataSetChanged();
                break;
            case R.id.app_detail:
                PackageUtils.startInstalledAppDetails(getActivity(), selectedAppInfo.getPackageName());
                popupWindow.dismiss();
                break;
            case R.id.app_install:
                PackageUtils.install(getActivity(), selectedAppInfo.getPackageName());
                popupWindow.dismiss();
                break;
            case R.id.app_delete:
                FileUtils.deleteFile(selectedAppInfo.getFilePath());
                apkList.remove(selectedAppInfo);
                apkListAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
                break;
            case R.id.app_upgrade:
                popupWindow.dismiss();
                PackageUtils.install(getActivity(), selectedAppInfo.getPackageName());
                break;

        }
    }
}