package com.synergy.wmc.wmc_plugin;

public class ExceptionObject {
    private String error;
    private String message;
    private Exception e;

    public ExceptionObject() {
    }

    public ExceptionObject(String error, String message, Exception e) {
        this.error = error;
        this.message = message;
        this.e = e;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }
}
