package com.synergy.wmc.wmc_plugin;

public interface ResultInitListener {
    void onSuccess(String data);
    void onError(String error,String message,Exception e);
}