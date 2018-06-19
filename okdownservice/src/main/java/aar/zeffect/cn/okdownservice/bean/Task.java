package aar.zeffect.cn.okdownservice.bean;

import java.io.Serializable;

public class Task implements Serializable {
    private String url;
    private String savePath;
    private boolean wifiRequired = false;

    public boolean isWifiRequired() {
        return wifiRequired;
    }

    public Task setWifiRequired(boolean wifiRequired) {
        this.wifiRequired = wifiRequired;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Task setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getSavePath() {
        return savePath;
    }

    public Task setSavePath(String savePath) {
        this.savePath = savePath;
        return this;
    }
}
