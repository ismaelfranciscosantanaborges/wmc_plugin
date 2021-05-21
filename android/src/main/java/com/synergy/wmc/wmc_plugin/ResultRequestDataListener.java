package com.synergy.wmc.wmc_plugin;

public interface ResultRequestDataListener {
    void onSuccess(byte[] data);
    void onError(String error,String message,Exception e);
}
