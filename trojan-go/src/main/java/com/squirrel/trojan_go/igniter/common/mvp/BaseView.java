package com.squirrel.trojan_go.igniter.common.mvp;

public interface BaseView<T extends BasePresenter> {
    void setPresenter(T presenter);
}
