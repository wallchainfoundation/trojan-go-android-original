package com.squirrel.trojan_go.igniter.helper;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;

import androidx.core.content.ContextCompat;

import com.squirrel.trojan_go.igniter.BuildConfig;
import com.squirrel.trojan_go.igniter.config.GlobalConfig;
import com.squirrel.trojan_go.igniter.service.ProxyService;
import com.squirrel.trojan_go.igniter.R;
import com.squirrel.trojan_go.igniter.config.TrojanConfig;

/**
 * Helper class for starting or stopping {@link ProxyService}. Before starting {@link ProxyService},
 * make sure the TrojanConfig is valid (with the help of {@link #isTrojanConfigValid()} and whether
 * user has consented VPN Service (with the help of {@link #isVPNServiceConsented(Context)}.
 * <br/>
 * It's recommended to start launcher activity when the config is invalid or user hasn't consented
 * VPN service.
 */
public abstract class ProxyHelper {
    public static boolean isTrojanConfigValid() {
        TrojanConfig cacheConfig = TrojanHelper.readTrojanConfig(GlobalConfig.getTrojanConfigPath());
        if (cacheConfig == null) {
            return false;
        }
        if (BuildConfig.DEBUG) {
            TrojanHelper.ShowConfig(GlobalConfig.getTrojanConfigPath());
        }
        return cacheConfig.isValidRunningConfig();
    }

    public static boolean isVPNServiceConsented(Context context) {
        return VpnService.prepare(context.getApplicationContext()) == null;
    }

    public static void startProxyService(Context context) {
        ContextCompat.startForegroundService(context, new Intent(context, ProxyService.class));
    }
    public static void stopProxyService(Context context) {
        Intent intent = new Intent(context.getString(R.string.stop_service));
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }
}
