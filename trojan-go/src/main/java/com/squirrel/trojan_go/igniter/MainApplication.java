package com.squirrel.trojan_go.igniter;

import android.app.Application;
import android.content.Context;

import com.squirrel.trojan_go.igniter.helper.InitializerHelper;

public class MainApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        InitializerHelper.runInit(this);
    }
}
