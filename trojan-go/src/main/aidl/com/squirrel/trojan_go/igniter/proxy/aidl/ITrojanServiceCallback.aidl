// ITrojanServiceCallback.aidl
package com.squirrel.trojan_go.igniter.proxy.aidl;

// Declare any non-default types here with import statements

interface ITrojanServiceCallback {
    void onStateChanged(int state, String msg);
    void onTestResult(String testUrl, boolean connected, long delay, String error);
}
