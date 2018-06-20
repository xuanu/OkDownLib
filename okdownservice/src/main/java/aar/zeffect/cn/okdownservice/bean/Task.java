package aar.zeffect.cn.okdownservice.bean;

import android.content.Intent;

import java.io.Serializable;

public class Task implements Serializable {
    private String url;
    private String savePath;
    private boolean wifiRequired = false;


    private boolean showNotifi=false;//是否显示通知栏
    private Intent openIntent;
    private String notifiTitle;


    public boolean isShowNotifi() {
        return showNotifi;
    }

    public Task setShowNotifi(boolean showNotifi) {
        this.showNotifi = showNotifi;
        return this;
    }

    public String getNotifiTitle() {
        return notifiTitle;
    }

    public Task setNotifiTitle(String notifiTitle) {
        this.notifiTitle = notifiTitle;
        return this;
    }

    public Intent getOpenIntent() {
        return openIntent;
    }

    public Task setOpenIntent(Intent openIntent) {
        this.openIntent = openIntent;
        return this;
    }

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
