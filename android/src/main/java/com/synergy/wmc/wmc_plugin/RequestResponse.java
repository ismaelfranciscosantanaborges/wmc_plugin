package com.synergy.wmc.wmc_plugin;

import java.util.Map;

public class RequestResponse {

    private Map<String, String> result;
    private ExceptionObject exception;

    public RequestResponse() {
    }

    public RequestResponse(Map<String, String> result, ExceptionObject exception) {
        this.result = result;
        this.exception = exception;
    }

    public RequestResponse(ExceptionObject exception) {
        this.exception = exception;
    }

    public Map<String, String> getResult() {
        return result;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }

    public ExceptionObject getException() {
        return exception;
    }

    public void setException(ExceptionObject exception) {
        this.exception = exception;
    }
}
