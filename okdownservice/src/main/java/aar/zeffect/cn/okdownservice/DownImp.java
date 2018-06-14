package aar.zeffect.cn.okdownservice;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.liulishuo.okdownload.DownloadSerialQueue;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener3;

import java.io.File;

import aar.zeffect.cn.okdownservice.bean.DownStatus;
import aar.zeffect.cn.okdownservice.utils.DownStr;

public class DownImp {
    private DownloadSerialQueue downloadSerialQueue;

    private Context mContext;

    public DownImp(Context pTarget) {
        mContext = pTarget;
        downloadSerialQueue = new DownloadSerialQueue(downloadListener3);
    }


    public void addTask(String url, File saveFile) {
        if (TextUtils.isEmpty(url)) return;
        if (saveFile == null) return;
        if (downloadSerialQueue == null)
            downloadSerialQueue = new DownloadSerialQueue(downloadListener3);
        DownloadTask task = new DownloadTask.Builder(url, saveFile)
                .setConnectionCount(1)
                .setPassIfAlreadyCompleted(true)
                .setMinIntervalMillisCallbackProcess(1000)
                .build();
        downloadSerialQueue.enqueue(task);
    }

    public void pause() {
        if (downloadSerialQueue != null) downloadSerialQueue.pause();
    }

    public void resume() {
        if (downloadSerialQueue != null) downloadSerialQueue.resume();
    }


    /***
     * 状态回调，先用广播发出去吧。
     */
    private DownloadListener3 downloadListener3 = new DownloadListener3() {
        @Override
        protected void started(DownloadTask task) {
            sendBroad(DownStatus.STATUS_START, task);
        }

        @Override
        protected void completed(DownloadTask task) {
            sendBroad(DownStatus.STATUS_COMPLETE, task);
        }

        @Override
        protected void canceled(DownloadTask task) {
            sendBroad(DownStatus.STATUS_CANCEL, task);
        }

        @Override
        protected void error(DownloadTask task, Exception e) {
            sendBroad(DownStatus.STATUS_ERROR, task);
        }

        @Override
        protected void warn(DownloadTask task) {
            sendBroad(DownStatus.STATUS_WARN, task);
        }

        @Override
        public void retry(DownloadTask task, ResumeFailedCause cause) {
            sendBroad(DownStatus.STATUS_RETRY, task);
        }

        @Override
        public void connected(DownloadTask task, int blockCount, long currentOffset, long totalLength) {
            sendBroad(DownStatus.STATUS_CONNECT, task, (int) (currentOffset * 1f / totalLength * 100));
        }

        @Override
        public void progress(DownloadTask task, long currentOffset, long totalLength) {
            sendBroad(DownStatus.STATUS_CONNECT, task, (int) (currentOffset * 1f / totalLength * 100));
        }
    };

    private void sendBroad(String staus, DownloadTask task) {
        sendBroad(staus, task, 0);
    }

    private void sendBroad(String staus, DownloadTask task, int progress) {
        if (mContext != null) {
            LocalBroadcastManager.getInstance(mContext)
                    .sendBroadcast(new Intent(DownStr.ACTION_STATU).putExtra(DownStr.DATA, new DownStatus()
                            .setProgress(progress)
                            .setStatus(staus)
                            .setSavePath(task.getFile().getAbsolutePath())
                            .setUrl(task.getUrl())));
        }
    }

}
