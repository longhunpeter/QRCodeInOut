package com.ichronocloud.inout.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ichronocloud.inout.Config;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by peter
 */
public class BaseActivity extends Activity {
    private final String TAG = "peter";
    protected MessageReceiver mMessageReceiver;
    protected String pushMessage;

    public static boolean isForeground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    /**
     * 弹出提示信息
     *
     * @param msg 信息
     */
    public void makeToast(CharSequence msg) {
        makeToast(msg, Toast.LENGTH_SHORT);
    }

    /**
     * 弹出提示信息
     *
     * @param msg      信息
     * @param duration 弹出时间长度
     */
    public void makeToast(CharSequence msg, int duration) {
        Toast.makeText(this, msg, duration).show();
    }

    /**
     * 弹出提示信息
     *
     * @param resId 资源ID
     */
    public void makeToast(int resId) {
        makeToast(resId, Toast.LENGTH_SHORT);
    }

    /**
     * 弹出提示信息
     *
     * @param resId    资源ID
     * @param duration 弹出时间长度
     */
    public void makeToast(int resId, int duration) {
        Toast.makeText(this, resId, duration).show();
    }

    /**
     * 注册广播服务
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Config.MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    /**
     * 接受消息广播
     */
    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Config.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
               pushMessage = intent.getStringExtra(Config.PUSH_EXTRAS);
            }
        }
    }

}
