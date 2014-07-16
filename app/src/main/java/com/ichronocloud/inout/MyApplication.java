package com.ichronocloud.inout;

import android.app.Application;

import com.ichronocloud.inout.util.Utils;


import cn.jpush.android.api.JPushInterface;


/**
 * Created by lxl on 2014/6/12.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        Utils.setAlias(this);
    }

}
