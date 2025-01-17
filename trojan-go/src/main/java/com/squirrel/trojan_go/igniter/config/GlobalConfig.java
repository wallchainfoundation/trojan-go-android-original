package com.squirrel.trojan_go.igniter.config;

import android.content.Context;
import android.os.Environment;

import com.squirrel.trojan_go.igniter.helper.PathHelper;

import java.io.File;

public class GlobalConfig {

    private static String cacheDir;
    private static String filesDir;
    private static String externalFilesDir;
    private static TrojanConfig trojanConfigInstance;

    public static void Init(Context ctx) {
        cacheDir = ctx.getCacheDir().getAbsolutePath();
        filesDir = ctx.getFilesDir().getAbsolutePath();
        File externalDocDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File igniterExternalFileDir = new File(externalDocDir, "igniter");
        externalFilesDir = igniterExternalFileDir.getAbsolutePath();
        trojanConfigInstance = new TrojanConfig();
    }

    public static String getCaCertPath() {
        return PathHelper.combine(cacheDir, "cacert.pem");
    }

    public static String getCountryMmdbPath() {
        return PathHelper.combine(filesDir, "Country.mmdb");
    }

    public static String getClashConfigPath() {
        return PathHelper.combine(filesDir, "config.yaml");
    }

    public static String getTrojanConfigPath() {
        return PathHelper.combine(filesDir, "config.json");
    }

    public static String getTrojanConfigListPath() {
        return PathHelper.combine(filesDir, "config_list.json");
    }

    public static String getPreferencesFilePath() {
        return PathHelper.combine(filesDir, "preferences.txt");
    }

    public static String getExemptedAppListPath() {
        return PathHelper.combine(externalFilesDir, "exempted_app_list.txt");
    }

    public static void setTrojanConfigInstance(TrojanConfig config) {
        trojanConfigInstance = config;
    }

    public static TrojanConfig getTrojanConfigInstance() {
        return trojanConfigInstance;
    }
}
