package com.blue.sky.common.fragment;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/7/27.
 */
public class BaseFragment extends Fragment implements View.OnClickListener {

    protected static final int MSG_EMPTY = 0;
    protected static final int MSG_REFRESH = 1;
    protected static final int MSG_FINISHED = 2;
    protected static final int HIDE_LOADING = 3;
    protected static final int FILE_DELETE_FINISHED = 4;

    protected void startActivity(Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        startActivity(intent);
    }

    protected void startActivity(Class<?> clazz, String key, Serializable value) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    protected void startActivity(Class<?> clazz, String key, String value) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    protected void startActivity(Class<?> clazz, String key, int value) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    protected void sendMessage(Handler handler,int what, Object message){
        Message msg = Message.obtain();
        msg.obj = message;
        msg.what = what;
        handler.sendMessage(msg);
    }

    protected void sendMessageDelayed(Handler handler,int what, Object message, int delay){
        Message msg = Message.obtain();
        msg.obj = message;
        msg.what = what;
        handler.sendMessageDelayed(msg, delay);
    }

    @Override
    public void onClick(View v) {

    }
}
