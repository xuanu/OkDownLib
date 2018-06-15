package aar.zeffect.cn.okdownservice.bean;

import java.io.Serializable;

public class DownStatus implements Serializable {
    public static final String STATUS_START = "STATUS_START", STATUS_COMPLETE = "STATUS_COMPLETE", STATUS_CANCEL = "STATUS_CANCEL", STATUS_ERROR = "STATUS_ERROR", STATUS_WARN = "STATUS_WARN", STATUS_RETRY = "STATUS_RETRY", STATUS_CONNECT = "STATUS_CONNECT", STATUS_PROGRESS = "STATUS_PROGRESS";


    private String url="";
    private String savePath="";
    private String status="";
    private int progress=0;

    public String getUrl() {
        return url;
    }

    public DownStatus setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getSavePath() {
        return savePath;
    }

    public DownStatus setSavePath(String savePath) {
        this.savePath = savePath;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public DownStatus setStatus(String status) {
        this.status = status;
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public DownStatus setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    @Override
    public String toString() {
        return "DownStatus{" +
                "url='" + url + '\'' +
                ", savePath='" + savePath + '\'' +
                ", status='" + status + '\'' +
                ", progress=" + progress +
                '}';
    }
}
