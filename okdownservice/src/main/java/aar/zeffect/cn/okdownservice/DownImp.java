package aar.zeffect.cn.okdownservice;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener3;

import java.io.File;
import java.text.DecimalFormat;

import aar.zeffect.cn.okdownservice.bean.DownStatus;
import aar.zeffect.cn.okdownservice.bean.Task;
import aar.zeffect.cn.okdownservice.utils.DownStr;
import aar.zeffect.cn.okdownservice.utils.DownUtils;

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
                .setWifiRequired(tempTask.isWifiRequired())
                .build();
        if (StatusUtil.isSameTaskPendingOrRunning(task)) {
            sendBroad(mContext, DownStatus.STATUS_PROGRESS, task);
            return;
        }
        task.enqueue(new MyDownListener(mContext, tempTask));
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

        private Task downTask;

        private NotificationManagerCompat managerCompat;

        private NotificationCompat.Builder builder;

        public MyDownListener(Context pTarget, Task needTask) {
            mContext = pTarget;
            this.downTask = needTask;
            managerCompat = NotificationManagerCompat.from(pTarget);
            builder = new NotificationCompat.Builder(pTarget);
        }

        @Override
        protected void started(DownloadTask task) {
            sendBroad(mContext, DownStatus.STATUS_START, task);
            notifi(task, DownStatus.STATUS_START);
        }

        @Override
        protected void completed(DownloadTask task) {
            sendBroad(mContext, DownStatus.STATUS_COMPLETE, task);
            notifi(task, DownStatus.STATUS_COMPLETE, 100);
        }

        @Override
        protected void canceled(DownloadTask task) {
            sendBroad(mContext, DownStatus.STATUS_CANCEL, task);
            notifi(task, DownStatus.STATUS_CANCEL);
        }

        @Override
        protected void error(DownloadTask task, Exception e) {
            sendBroad(mContext, DownStatus.STATUS_ERROR, task);
            notifi(task, DownStatus.STATUS_ERROR);
        }

        @Override
        protected void warn(DownloadTask task) {
            sendBroad(mContext, DownStatus.STATUS_WARN, task);
            notifi(task, DownStatus.STATUS_WARN);
        }

        @Override
        public void retry(DownloadTask task, ResumeFailedCause cause) {
            sendBroad(mContext, DownStatus.STATUS_RETRY, task);
            notifi(task, DownStatus.STATUS_RETRY);
        }

        @Override
        public void connected(DownloadTask task, int blockCount, long currentOffset, long totalLength) {
            float progress = currentOffset * 1f / totalLength * 100;
            sendBroad(mContext, DownStatus.STATUS_CONNECT, task, progress);
            notifi(task, DownStatus.STATUS_CONNECT, progress);
        }

        @Override
        public void progress(DownloadTask task, long currentOffset, long totalLength) {
            float progress = currentOffset * 1f / totalLength * 100;
            sendBroad(mContext, DownStatus.STATUS_PROGRESS, task, progress);
            notifi(task, DownStatus.STATUS_PROGRESS, progress);
        }


        private void notifi(DownloadTask task, String status) {
            BreakpointInfo breakpointInfo = StatusUtil.getCurrentInfo(task);
            float progress = breakpointInfo.getTotalOffset() * 1f / breakpointInfo.getTotalLength();
            notifi(task, status, progress * 100);
        }

        private void notifi(DownloadTask task, String status, float progress) {
            if (!downTask.isShowNotifi()) {
                managerCompat.cancel(task.getId());
                return;
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String fileTitle = downTask.getNotifiTitle();
            if (TextUtils.isEmpty(fileTitle)) fileTitle = task.getUrl();
            builder.setContentTitle(fileTitle)
                    .setSmallIcon(R.drawable.downimp_ic_cloud_download_black_24dp)
                    .setContentText(DownUtils.statu2Str(status))
                    .setContentInfo("已下载:" + decimalFormat.format(progress) + "%")
                    .setAutoCancel(true);
            if (status.equals(DownStatus.STATUS_PROGRESS)
                    || status.equals(DownStatus.STATUS_CONNECT)) {
                builder.setProgress(100, (int) progress, false);
            } else if (status.equals(DownStatus.STATUS_WARN)
                    || status.equals(DownStatus.STATUS_ERROR)
                    || status.equals(DownStatus.STATUS_RETRY)
                    || status.equals(DownStatus.STATUS_START)) {
                builder.setProgress(100, 0, true);
            } else if (status.equals(DownStatus.STATUS_CANCEL)) {
                builder.setProgress(100, 0, false);
            } else if (status.equals(DownStatus.STATUS_COMPLETE)) {
                if (downTask.getOpenIntent() != null) {
                    if (mContext.getPackageManager().resolveActivity(downTask.getOpenIntent(), PackageManager.MATCH_DEFAULT_ONLY) != null) {
                        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, downTask.getOpenIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(pendingIntent);
                    }
                }
            } else builder.setProgress(0, 0, false);

            managerCompat.notify(task.getId(), builder.build());
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
