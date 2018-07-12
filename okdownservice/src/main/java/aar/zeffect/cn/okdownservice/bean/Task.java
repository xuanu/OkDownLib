package aar.zeffect.cn.okdownservice.bean;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Task implements Parcelable {

    private String url;
    private String savePath;
    private boolean wifiRequired = false;


    private boolean showNotifi = false;//是否显示通知栏
    private Intent openIntent;
    private String notifiTitle;

    private String tag;

    public String getTag() {
        return tag;
    }

    public Task setTag(String tag) {
        this.tag = tag;
        return this;
    }

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
        if (savePath == null)
            savePath = "dont't set savepath";
        return savePath;
    }

    public Task setSavePath(String savePath) {
        this.savePath = savePath;
        return this;
    }


    public Task(String tag, String downUrl, String savePath) {
        if (tag == null)
            throw new NullPointerException("tag must not empty");
        this.tag = tag;
        this.url = downUrl;
        this.savePath = savePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.savePath);
        dest.writeByte(this.wifiRequired ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showNotifi ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.openIntent, flags);
        dest.writeString(this.notifiTitle);
        dest.writeString(this.tag);
    }

    protected Task(Parcel in) {
        this.url = in.readString();
        this.savePath = in.readString();
        this.wifiRequired = in.readByte() != 0;
        this.showNotifi = in.readByte() != 0;
        this.openIntent = in.readParcelable(Intent.class.getClassLoader());
        this.notifiTitle = in.readString();
        this.tag = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

}
