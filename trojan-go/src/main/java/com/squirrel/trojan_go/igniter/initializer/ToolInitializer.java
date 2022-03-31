package com.squirrel.trojan_go.igniter.initializer;

import android.content.Context;

import com.squirrel.trojan_go.igniter.config.GlobalConfig;

/**
 * Initializer that runs in Tools Process.
 */
public class ToolInitializer extends Initializer {

    @Override
    public void init(Context context) {
        GlobalConfig.Init(context);
    }

    @Override
    public boolean runsInWorkerThread() {
        return false;
    }
}
