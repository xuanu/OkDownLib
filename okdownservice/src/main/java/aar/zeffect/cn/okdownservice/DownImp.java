package aar.zeffect.cn.okdownservice;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener3;

import java.io.File;

import aar.zeffect.cn.okdownservice.bean.DownStatus;
import aar.zeffect.cn.okdownservice.bean.Task;
import aar.zeffect.cn.okdownservice.utils.DownStr;

public class DownImp {
    private Context mContext;

    public DownImp(Context pTarget) {
        mContext = pTarget;
    }


    public void addTask(Task tempTask) {
        if (tempTask == null) return;
        String url = tempTask.getUrl();
        if (TextUtils.isEmpty(url)) return;
        String savePath = tempTask.getSavePath();
        if (TextUtils.isEmpty(savePath)) return;
        File saveFile = new File(savePath);
        DownloadTask task = new DownloadTask.Builder(url, saveFile)
                .setConnectionCount(1)
                .setPassIfAlreadyCompleted(true)
                .setMinIntervalMillisCallbackProcess(1000)
                .build();
        if (StatusUtil.isCompleted(task)) {
            sendBroad(mContext, DownStatus.STATUS_COMPLETE, task);
            return;
        }
        if (StatusUtil.isSameTaskPendingOrRunning(task)) {
            sendBroad(mContext, DownStatus.STATUS_PROGRESS, task);
            return;
        }
        task.enqueue(new MyDownListener(mContext));
    }

    public void cancel(Task tempTask) {
        if (tempTask == null) return;
        String url = tempTask.getUrl();
        if (TextUtils.isEmpty(url)) return;
        String savePath = tempTask.getSavePath();
        if (TextUtils.isEmpty(savePath)) return;
        File saveFile = new File(savePath);
        DownloadTask task = new DownloadTask.Builder(url, saveFile)
                .setConnectionCount(1)
                .setPassIfAlreadyCompleted(true)
                .setMinIntervalMillisCallbackProcess(1000)
                .build();
        if (StatusUtil.isCompleted(task)) {
            sendBroad(mContext, DownStatus.STATUS_COMPLETE, task);
            return;
        }
        if (StatusUtil.isSameTaskPendingOrRunning(task)) {
            OkDownload.with().downloadDispatcher().cancel(task);
            return;
        }
    }

    public void cancelAll() {
        OkDownload.with().downloadDispatcher().cancelAll();
    }


    /***
     * 状态回调，先用广播发出去吧。
     */
    private static class MyDownListener extends DownloadListener3 {

        private Context mContext;

        public MyDownListener(Context pTarget) {
            mContext = pTarget;
        }

        @Override
        protected void started(DownloadTask task) {
            sendBroad(mContext, DownStatus.STATUS_START, task);
        }

        @Override
        protected void completed(DownloadTask task) {
            sendBroad(mContext, DownStatus.STATUS_COMPLETE, task);
        }

        @Override
        protected void canceled(DownloadTask task) {
            sendBroad(mContext, DownStatus.STATUS_CANCEL, task);
        }

        @Override
        protected void error(DownloadTask task, Exception e) {
            sendBroad(mContext, DownStatus.STATUS_ERROR, task);
        }

        @Override
        protected void warn(DownloadTask task) {
            sendBroad(mContext, DownStatus.STATUS_WARN, task);
        }

        @Override
        public void retry(DownloadTask task, ResumeFailedCause cause) {
            sendBroad(mContext, DownStatus.STATUS_RETRY, task);
        }

        @Override
        public void connected(DownloadTask task, int blockCount, long currentOffset, long totalLength) {
            sendBroad(mContext, DownStatus.STATUS_CONNECT, task, currentOffset * 1f / totalLength * 100);
        }

        @Override
        public void progress(DownloadTask task, long currentOffset, long totalLength) {
            sendBroad(mContext, DownStatus.STATUS_CONNECT, task, currentOffset * 1f / totalLength * 100);
        }


    }

    private static void sendBroad(Context pTarget, String staus, DownloadTask task) {
        sendBroad(pTarget, staus, task, 0);
    }

    private static void sendBroad(Context pTarget, String staus, DownloadTask task, float progress) {
        if (pTarget != null) {
            LocalBroadcastManager.getInstance(pTarget)
                    .sendBroadcast(new Intent(DownStr.ACTION_STATU).putExtra(DownStr.DATA, new DownStatus()
                            .setProgress(progress)
                            .setStatus(staus)
                            .setSavePath(task.getFile().getAbsolutePath())
                            .setUrl(task.getUrl())));
        }
    }

}
