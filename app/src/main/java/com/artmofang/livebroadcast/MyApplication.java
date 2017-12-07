package com.artmofang.livebroadcast;

import android.app.Application;

import com.tumblr.remember.Remember;

/**
 * Created by Administrator on 2017/12/1.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Remember.init(getApplicationContext(), "com.artmofang.live");
    }
}
