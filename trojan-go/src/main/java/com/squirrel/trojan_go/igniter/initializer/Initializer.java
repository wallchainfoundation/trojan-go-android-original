package com.squirrel.trojan_go.igniter.initializer;

import android.content.Context;

public abstract class Initializer {

    public abstract void init(Context context);

    public abstract boolean runsInWorkerThread();
}
