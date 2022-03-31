package com.squirrel.trojan_go.igniter;

import android.content.Intent;
import android.net.Uri;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squirrel.trojan_go.igniter.common.constants.Constants;
import com.squirrel.trojan_go.igniter.common.os.Task;
import com.squirrel.trojan_go.igniter.common.os.Threads;
import com.squirrel.trojan_go.igniter.common.utils.PreferenceUtils;
import com.squirrel.trojan_go.igniter.config.GlobalConfig;
import com.squirrel.trojan_go.igniter.config.TrojanConfig;
import com.squirrel.trojan_go.igniter.connection.TrojanConnection;
import com.squirrel.trojan_go.igniter.helper.LogHelper;
import com.squirrel.trojan_go.igniter.helper.ProxyHelper;
import com.squirrel.trojan_go.igniter.helper.TrojanHelper;
import com.squirrel.trojan_go.igniter.proxy.aidl.ITrojanService;
import com.squirrel.trojan_go.igniter.service.ProxyService;

public class MainActivity extends AppCompatActivity  implements TrojanConnection.Callback {
    private static final String TAG = "MainActivity";
    private static final int VPN_REQUEST_CODE = 233;

    String configJSON = "{\n" +
            "  \"run_type\": \"client\",\n" +
            "  \"local_addr\": \"127.0.0.1\",\n" +
            "  \"local_port\": 39119,\n" +
            "  \"remote_addr\": \"style.wireshop.net\",\n" +
            "  \"remote_port\": 443,\n" +
            "  \"password\": [\n" +
            "    \"TNYS0OSB8FZygp736e2ERc3DaawSACz9lFXuAI3aDpCDyRK3ejnq5CYOl48xZx\"\n" +
            "  ],\n" +
            "  \"ssl\": {\n" +
            "    \"sni\": \"style.wireshop.net\"\n" +
            "  }\n" +
            "}";

    private int proxyState = ProxyService.STATE_NONE;
    private ITrojanService trojanService;
    private final TrojanConnection connection = new TrojanConnection(new Handler(),false);

    private Button startStopButton;
    private Switch clashSwitch;

    public void initConnection() {
        connection.disconnect(getApplicationContext());
        connection.connect(getApplicationContext(), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initConnection();
        setContentView(R.layout.activity_main);
        startStopButton = findViewById(R.id.startStopButton);
        clashSwitch = findViewById(R.id.clashSwitch);

        startStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TrojanConfig ins = GlobalConfig.getTrojanConfigInstance();
                ins.setConfig(configJSON);

                if (!GlobalConfig.getTrojanConfigInstance().isValidRunningConfig()) {
                    Toast.makeText(MainActivity.this,
                            R.string.invalid_configuration,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (proxyState == ProxyService.STATE_NONE || proxyState == ProxyService.STOPPED) {
                    TrojanHelper.WriteTrojanConfig(
                            GlobalConfig.getTrojanConfigInstance(),
                            GlobalConfig.getTrojanConfigPath()
                    );
                    TrojanHelper.ShowConfig(GlobalConfig.getTrojanConfigPath());
                    // start ProxyService
                    Intent i = VpnService.prepare(getApplicationContext());
                    if (i != null) {
                        startActivityForResult(i, VPN_REQUEST_CODE);
                    } else {
                        ProxyHelper.startProxyService(getApplicationContext());
                    }
                } else if (proxyState == ProxyService.STARTED) {
                    // stop ProxyService
                    ProxyHelper.stopProxyService(getApplicationContext());
                }
            }
        });
        boolean enableClash = PreferenceUtils.getBooleanPreference(getContentResolver(),
                Uri.parse(Constants.PREFERENCE_URI), Constants.PREFERENCE_KEY_ENABLE_CLASH, true);
        clashSwitch.setChecked(enableClash);
        clashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Generally speaking, it's better to insert content into ContentProvider in background
                // thread, but that may cause data inconsistency when user starts proxy right after
                // switching.
                PreferenceUtils.putBooleanPreference(getContentResolver(),
                        Uri.parse(Constants.PREFERENCE_URI), Constants.PREFERENCE_KEY_ENABLE_CLASH,
                        isChecked);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (VPN_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            ProxyHelper.startProxyService(getApplicationContext());
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void updateViews(int state) {
        proxyState = state;
        switch (state) {
            case ProxyService.STARTING: {
                startStopButton.setText(R.string.button_service__starting);
                startStopButton.setEnabled(false);
                startStopButton.setBackgroundResource(0);
                break;
            }
            case ProxyService.STARTED: {
                startStopButton.setText(R.string.button_service__stop);
                startStopButton.setEnabled(true);
                startStopButton.setBackgroundResource(R.color.colorPrimary);
                break;
            }
            case ProxyService.STOPPING: {
                startStopButton.setText(R.string.button_service__stopping);
                startStopButton.setEnabled(false);
                startStopButton.setBackgroundResource(0);
                break;
            }
            default: {
                startStopButton.setText(R.string.button_service__start);
                startStopButton.setEnabled(true);
                startStopButton.setBackgroundResource(0);
                break;
            }
        }
    }


    // implement TrojanConnection.Callback methods
    @Override
    public void onServiceConnected(final ITrojanService service) {
        LogHelper.i(TAG, "onServiceConnected");
        trojanService = service;
    }

    @Override
    public void onServiceDisconnected() {
        LogHelper.i(TAG, "onServiceDisconnected");
        trojanService = null;
    }

    @Override
    public void onStateChanged(final int state, String msg) {
        LogHelper.i(TAG, "onStateChanged# state: " + state + " msg: " + msg);
        Threads.instance().runOnWorkThread(new Task() {
            @Override
            public void onRun() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateViews(state);
                    }
                });
            }
        });
    }

    @Override
    public void onTestResult(final String testUrl, final boolean connected, final long delay, @NonNull final String error) {
        if (connected) {
            LogHelper.e(TAG, "Test successfully: ");
        } else {
            LogHelper.e(TAG, "Test Error: " + error);
        }
    }

    @Override
    public void onBinderDied() {
        LogHelper.i(TAG, "onBinderDied");
        connection.disconnect(getApplicationContext());
        // connect the new binder
        // todo is it necessary to re-connect?
        connection.connect(getApplicationContext(), this);
    }
}
