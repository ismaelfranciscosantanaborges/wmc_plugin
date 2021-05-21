package com.synergy.wmc.wmc_plugin;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.HttpDelete;

public class HttpDeleteWithBody extends HttpDelete {
    private HttpEntity entity;

    public HttpDeleteWithBody(String url) {
        super(url);
    }

    public HttpEntity getEntity() {
        return this.entity;
    }

    public void setEntity(final HttpEntity entity) {
        this.entity = entity;
    }
}
