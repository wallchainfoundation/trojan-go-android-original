package com.squirrel.trojan_go.igniter.helper;

public class JNIHelper {

    public static void trojan(String config) {
        trojan.Trojan.runClient(config);
    }

    public static void stop() {
        trojan.Trojan.stopClient();
    }
}
