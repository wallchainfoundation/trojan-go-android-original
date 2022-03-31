// ITrojanService.aidl
package com.squirrel.trojan_go.igniter.proxy.aidl;
import com.squirrel.trojan_go.igniter.proxy.aidl.ITrojanServiceCallback;
// Declare any non-default types here with import statements

interface ITrojanService {
    int getState();
    void testConnection(String testUrl);
    void showDevelopInfoInLogcat();
    oneway void registerCallback(in ITrojanServiceCallback callback);
    oneway void unregisterCallback(in ITrojanServiceCallback callback);
}
