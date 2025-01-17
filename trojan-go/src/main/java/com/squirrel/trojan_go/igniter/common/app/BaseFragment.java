package com.squirrel.trojan_go.igniter.common.app;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public abstract class BaseFragment extends Fragment {
    protected View mRootView;
    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view;
    }

    protected <T extends View> T findViewById(@IdRes int id) {
        return mRootView.findViewById(id);
    }

    protected void runOnUiThread(Runnable runnable) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(runnable);
        }
    }

    protected void finishActivity() {
        FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            activity.finish();
        }
    }
}
