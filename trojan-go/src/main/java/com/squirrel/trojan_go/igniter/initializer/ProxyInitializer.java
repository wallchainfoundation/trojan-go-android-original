package com.squirrel.trojan_go.igniter.initializer;

import android.content.Context;

import com.squirrel.trojan_go.igniter.config.GlobalConfig;
import com.squirrel.trojan_go.igniter.helper.LogHelper;
import com.squirrel.trojan_go.igniter.config.TrojanConfig;
import com.squirrel.trojan_go.igniter.helper.TrojanHelper;

public class ProxyInitializer extends Initializer {
    private static final String TAG = "ProxyInitializer";

    @Override
    public void init(Context context) {
        GlobalConfig.Init(context);
        TrojanConfig cacheConfig = TrojanHelper.readTrojanConfig(GlobalConfig.getTrojanConfigPath());
        if (cacheConfig == null) {
            LogHelper.e(TAG, "read null trojan config");
        } else {
            GlobalConfig.setTrojanConfigInstance(cacheConfig);
        }
        if (!GlobalConfig.getTrojanConfigInstance().isValidRunningConfig()) {
            LogHelper.e(TAG, "Invalid trojan config!");
        }
    }

    @Override
    public boolean runsInWorkerThread() {
        return false;
    }
}
