package com.squirrel.trojan_go.igniter.helper;

import android.content.Context;
import android.os.Process;
import android.text.TextUtils;

import com.squirrel.trojan_go.igniter.config.GlobalConfig;
import com.squirrel.trojan_go.igniter.R;
import com.squirrel.trojan_go.igniter.common.os.Task;
import com.squirrel.trojan_go.igniter.common.os.Threads;
import com.squirrel.trojan_go.igniter.common.utils.ProcessUtils;
import com.squirrel.trojan_go.igniter.initializer.Initializer;
import com.squirrel.trojan_go.igniter.initializer.MainInitializer;
import com.squirrel.trojan_go.igniter.initializer.ProxyInitializer;
import com.squirrel.trojan_go.igniter.initializer.ToolInitializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class of application initializations. You can just extend {@link Initializer} to create your
 * own initializer and register it in {@link #registerMainInitializers()} or {@link #registerToolsInitializers()}.
 * You should consider carefully to determine which process your initializers are run in.
 */
public class InitializerHelper {
    private static final String TOOL_PROCESS_POSTFIX = ":tools";
    private static final String PROXY_PROCESS_POSTFIX = ":proxy";
    private static List<Initializer> sInitializerList;

    private static void createInitializerList() {
        sInitializerList = new LinkedList<>();
    }

    private static void registerMainInitializers() {
        createInitializerList();
        sInitializerList.add(new MainInitializer());
    }

    private static void registerToolsInitializers() {
        createInitializerList();
        sInitializerList.add(new ToolInitializer());
    }

    private static void registerProxyInitializers() {
        createInitializerList();
        sInitializerList.add(new ProxyInitializer());
    }

    public static void runInit(Context context) {
        final String processName = ProcessUtils.getProcessNameByPID(context, Process.myPid());
        if (isToolProcess(processName)) {
            registerToolsInitializers();
        } else if (isProxyProcess(processName)) {
            registerProxyInitializers();
        } else {
            registerMainInitializers();
        }
        runInit(context, sInitializerList);
        clearInitializerLists();

        copyRawResourceToDir(context, R.raw.cacert, GlobalConfig.getCaCertPath(), true);
        copyRawResourceToDir(context, R.raw.country, GlobalConfig.getCountryMmdbPath(), true);
        copyRawResourceToDir(context, R.raw.clash_config, GlobalConfig.getClashConfigPath(), false);
    }

    private static void clearInitializerLists() {
        sInitializerList = null;
    }

    private static void runInit(final Context context, List<Initializer> initializerList) {
        final List<Initializer> runInWorkerThreadList = new LinkedList<>();
        for (int i = initializerList.size() - 1; i >= 0; i--) {
            if (initializerList.get(i).runsInWorkerThread()) {
                runInWorkerThreadList.add(initializerList.remove(i));
            }
        }
        Threads.instance().runOnWorkThread(new Task() {
            @Override
            public void onRun() {
                runInitList(context, runInWorkerThreadList);
            }
        });
        runInitList(context, initializerList);
    }

    private static void runInitList(Context context, List<Initializer> initializers) {
        for (int i = initializers.size() - 1; i >= 0; i--) {
            initializers.remove(i).init(context);
        }
    }

    private static boolean isMainProcess(String processName) {
        return !isToolProcess(processName) && !isProxyProcess(processName);
    }

    private static boolean isToolProcess(String processName) {
        return TextUtils.equals(processName, TOOL_PROCESS_POSTFIX);
    }

    private static boolean isProxyProcess(String processName) {
        return TextUtils.equals(processName, PROXY_PROCESS_POSTFIX);
    }

    private static void copyRawResourceToDir(final Context context, int resId, String destPathName, boolean override) {
        File file = new File(destPathName);
        if (override || !file.exists()) {
            try {
                try (InputStream is = context.getResources().openRawResource(resId);
                     FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = is.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
