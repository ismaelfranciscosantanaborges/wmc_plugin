package com.synergy.wmc.wmc_plugin;

import java.util.Map;

public interface ResultRequestListener {
    void onSuccess(Map<String,String> data);
    void onError(String error,String message,Exception e);
}
