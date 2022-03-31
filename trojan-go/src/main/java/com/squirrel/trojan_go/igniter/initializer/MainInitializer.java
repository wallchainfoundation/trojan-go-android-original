package com.squirrel.trojan_go.igniter.initializer;

import android.content.Context;

import com.squirrel.trojan_go.igniter.config.GlobalConfig;

/**
 * Initializer that runs in Main Process (Default process).
 */
public class MainInitializer extends Initializer {

    @Override
    public void init(Context context) {
        GlobalConfig.Init(context);
    }

    @Override
    public boolean runsInWorkerThread() {
        return false;
    }
}
