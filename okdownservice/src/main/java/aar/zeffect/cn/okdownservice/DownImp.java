package aar.zeffect.cn.okdownservice;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener3;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aar.zeffect.cn.okdownservice.bean.DownStatus;
import aar.zeffect.cn.okdownservice.bean.Task;
import aar.zeffect.cn.okdownservice.utils.DownStr;
import aar.zeffect.cn.okdownservice.utils.DownUtils;
import aar.zeffect.cn.okdownservice.utils.FileUtils;
import aar.zeffect.cn.okdownservice.utils.MD5Crypto;


public class DownImp {
    private Context mContext;

    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    public DownImp(Context pTarget) {
        mContext = pTarget;
    }


    public void addTask(Task tempTask) {
        addTasks(Arrays.asList(tempTask), tempTask);
    }


    public void addTasks(final List<Task> tasks, final Task infoTask) {
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (tasks == null) return;
                if (tasks.isEmpty()) {
                    return;
                }
                //
                List<DownloadTask> needTasks = new ArrayList<>(tasks.size());
                for (int i = 0; i < tasks.size(); i++) {
                    Task tempTask = tasks.get(i);
                    if (tempTask == null) continue;
                    String url = tempTask.getUrl();
                    if (TextUtils.isEmpty(url)) continue;
                    String savePath = tempTask.getSavePath();
                    if (TextUtils.isEmpty(savePath)) continue;
                    File saveFile = new File(savePath);
                    DownloadTask task = new DownloadTask.Builder(url, saveFile)
                            .setConnectionCount(1)
                            .setPassIfAlreadyCompleted(true)
                            .setMinIntervalMillisCallbackProcess(1000)
                            .setWifiRequired(tempTask.isWifiRequired())
                            .build();
                    if (StatusUtil.isSameTaskPendingOrRunning(task)) {
                        continue;
                    }
                    needTasks.add(task);
                }
                //
                DownloadTask[] downloadTasks = new DownloadTask[needTasks.size()];
                needTasks.toArray(downloadTasks);
                //
                DownloadTask.enqueue(downloadTasks, new MyDownListener(mContext, infoTask, needTasks.size()));
                if (TextUtils.isEmpty(infoTask.getTag())) {//尝试使用下载链接作为TAG或本地存储路径作为Tag
                    String tempUrl = infoTask.getUrl();
                    if (!TextUtils.isEmpty(tempUrl)) {
                        infoTask.setTag(tempUrl);
                    } else if (!TextUtils.isEmpty(infoTask.getSavePath())) {
                        infoTask.setTag(infoTask.getSavePath());
                    }
                }
                if (!TextUtils.isEmpty(infoTask.getTag())) {//任务队列存在本地
                    File tagFile = new File(mContext.getExternalFilesDir("tag"), infoTask.getTag() + ".info");
                    if (tagFile.exists()) tagFile.delete();
                    if (tagFile.isDirectory()) tagFile.delete();
                    if (!tagFile.exists()) tagFile.getParentFile().mkdirs();
                    FileUtils.write(tagFile.getAbsolutePath(), list2String(downloadTasks).toString());
                    Log.e("zeffect", "save tag:" + tagFile.getAbsolutePath());
                }
                sendBroad(mContext, DownStatus.STATUS_CONNECT, 0, infoTask.getUrl(), new File(infoTask.getSavePath()), infoTask.getTag());
            }
        });
    }


    private JSONArray list2String(DownloadTask[] tasks) {
        if (tasks == null || tasks.length == 0) return new JSONArray();
        JSONArray dataArray = new JSONArray();
        for (int i = 0; i < tasks.length; i++) {
            dataArray.put(tasks[i].getId());
        }
        return dataArray;
    }

    private List<Integer> string2TaskId(String data) {
        try {
            JSONArray taskArray = new JSONArray(data);
            List<Integer> tasks = new ArrayList<>(taskArray.length());
            for (int i = 0; i < taskArray.length(); i++) {
                int taskid = taskArray.optInt(i, -1);
                if (taskid != -1) tasks.add(taskid);
            }
            return tasks;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void cancel(final String tag) {
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(tag)) return;
                File tagFile = new File(mContext.getExternalFilesDir("tag"), MD5Crypto.Md5_32(tag) + ".info");
                if (tagFile.isDirectory()) tagFile.delete();
                if (tagFile.exists()) {
                    List<Integer> taskids = string2TaskId(FileUtils.read(tagFile.getAbsolutePath()));
                    if (taskids != null && !taskids.isEmpty()) {
                        for (int i = 0; i < taskids.size(); i++) {
                            OkDownload.with().downloadDispatcher().cancel(taskids.get(i));
                        }
                    }
                }
                removeTag(tagFile);
            }
        });

    }

    private void removeTag(File tagFile) {
        if (tagFile != null && tagFile.exists()) {
            tagFile.delete();
            Log.e("zeffect", "remove tag:" + tagFile.getAbsolutePath());
        }
    }

    public void cancel(final Task tempTask) {
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
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
                    sendBroad(mContext, DownStatus.STATUS_COMPLETE, 100, task.getUrl(), task.getFile());
                    return;
                }
                if (StatusUtil.isSameTaskPendingOrRunning(task)) {
                    OkDownload.with().downloadDispatcher().cancel(task);
                    return;
                }
                sendBroad(mContext, DownStatus.STATUS_CANCEL, 0, task.getUrl(), task.getFile());
            }
        });

    }

    public void cancelAll() {
        OkDownload.with().downloadDispatcher().cancelAll();
    }


    /***
     * 状态回调，先用广播发出去吧。
     */
    public static class MyDownListener extends DownloadListener3 {

        private Context mContext;

        private Task downTask;

        private NotificationManagerCompat managerCompat;

        private NotificationCompat.Builder builder;

        private int downCount, successCount, faileCount;

        private boolean isMultitask;

        private int notifiId;

        private MyDownListener(Context pTarget, Task needTask, int count) {
            mContext = pTarget;
            this.downTask = needTask;
            managerCompat = NotificationManagerCompat.from(pTarget);
            builder = new NotificationCompat.Builder(pTarget);
            downCount = count;
            successCount = 0;
            faileCount = 0;
            isMultitask = downCount > 1;
            notifiId = new Random().nextInt();
        }

        @Override
        protected void started(@NonNull DownloadTask task) {
            sendBroad(mContext, DownStatus.STATUS_START, isMultitask ? (successCount + faileCount) * 1f / downCount * 100 : 0, task.getUrl(), task.getFile(), downTask.getTag());
            notifi(task, isMultitask ? DownStatus.STATUS_PROGRESS : DownStatus.STATUS_START, isMultitask ? (successCount + faileCount) * 1f / downCount * 100 : 0);
        }

        @Override
        protected void completed(@NonNull DownloadTask task) {
            successCount++;
            if (!isMultitask) {
                notifi(task, DownStatus.STATUS_COMPLETE, 100);
                sendBroad(mContext, DownStatus.STATUS_COMPLETE, 100, task.getUrl(), task.getFile(), downTask.getTag());
            } else {//多任务时，全部下完才发送成功或失败广播
                checkMultitaskCompleted(task);
            }
        }

        @Override
        protected void canceled(@NonNull DownloadTask task) {
            sendBroad(mContext, DownStatus.STATUS_CANCEL, isMultitask ? (successCount + faileCount) * 1f / downCount * 100 : 0, task.getUrl(), task.getFile(), downTask.getTag());
            notifi(task, DownStatus.STATUS_CANCEL, isMultitask ? (successCount + faileCount) * 1f / downCount * 100 : 0);
        }

        @Override
        protected void error(@NonNull DownloadTask task, @NonNull Exception e) {
            faileCount++;
            if (!isMultitask) {
                sendBroad(mContext, DownStatus.STATUS_ERROR, 0, task.getUrl(), task.getFile(), downTask.getTag());
                notifi(task, DownStatus.STATUS_ERROR, 0);
            } else {//多任务时，全部下完才发送成功或失败广播
                checkMultitaskCompleted(task);
            }
        }

        private void checkMultitaskCompleted(@NonNull DownloadTask task) {
            if (!isMultitask) return;
            if (successCount + faileCount < downCount) {//发送下载中的广播
                sendBroad(mContext, DownStatus.STATUS_PROGRESS, (successCount + faileCount) * 1f / downCount * 100, task.getUrl(), task.getFile(), downTask.getTag());
                notifi(task, DownStatus.STATUS_PROGRESS, (successCount + faileCount) * 1f / downCount * 100);
                return;
            } else {
                sendBroad(mContext, faileCount > 0 ? DownStatus.STATUS_ERROR : DownStatus.STATUS_COMPLETE, (successCount + faileCount) * 1f / downCount * 100, task.getUrl(), task.getFile(), downTask.getTag());
                notifi(task, faileCount > 0 ? DownStatus.STATUS_ERROR : DownStatus.STATUS_COMPLETE, (successCount + faileCount) * 1f / downCount * 100);

            }
        }

        @Override
        protected void warn(@NonNull DownloadTask task) {
            sendBroad(mContext, DownStatus.STATUS_WARN, isMultitask ? (successCount + faileCount) * 1f / downCount * 100 : 0, task.getUrl(), task.getFile(), downTask.getTag());
            notifi(task, isMultitask ? DownStatus.STATUS_PROGRESS : DownStatus.STATUS_WARN, isMultitask ? (successCount + faileCount) * 1f / downCount * 100 : 0);
        }

        @Override
        public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {
            sendBroad(mContext, DownStatus.STATUS_RETRY, isMultitask ? (successCount + faileCount) * 1f / downCount * 100 : 0, task.getUrl(), task.getFile(), downTask.getTag());
            notifi(task, isMultitask ? DownStatus.STATUS_PROGRESS : DownStatus.STATUS_RETRY, isMultitask ? (successCount + faileCount) * 1f / downCount * 100 : 0);
        }

        @Override
        public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {
            float progress = currentOffset * 1f / totalLength * 100;
            sendBroad(mContext, DownStatus.STATUS_CONNECT, isMultitask ? (successCount + faileCount) * 1f / downCount * 100 : progress, task.getUrl(), task.getFile(), downTask.getTag());
            notifi(task, isMultitask ? DownStatus.STATUS_PROGRESS : DownStatus.STATUS_CONNECT, isMultitask ? (successCount + faileCount) * 1f / downCount * 100 : progress);
        }

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
            float progress = currentOffset * 1f / totalLength * 100;
            sendBroad(mContext, DownStatus.STATUS_PROGRESS, isMultitask ? (successCount + faileCount) * 1f / downCount * 100 : progress, task.getUrl(), task.getFile(), downTask.getTag());
            notifi(task, DownStatus.STATUS_PROGRESS, isMultitask ? (successCount + faileCount) * 1f / downCount * 100 : progress);
        }


        private void notifi(DownloadTask task, String status, float progress) {
            if (downTask == null) return;
            if (!downTask.isShowNotifi()) {
                managerCompat.cancel(isMultitask ? notifiId : task.getId());
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

            managerCompat.notify(isMultitask ? notifiId : task.getId(), builder.build());
        }


    }

    private static void sendBroad(Context pTarget, String staus, float progress, String tempUrl, File saveFile) {
        sendBroad(pTarget, staus, progress, tempUrl, saveFile, "");
    }

    private static void sendBroad(Context pTarget, String staus, float progress, String tempUrl, File saveFile, String tag) {
        if (pTarget != null) {
            LocalBroadcastManager.getInstance(pTarget)
                    .sendBroadcast(new Intent(DownStr.ACTION_STATU).putExtra(DownStr.DATA, new DownStatus()
                            .setProgress(progress)
                            .setStatus(staus)
                            .setTag(tag)
                            .setSavePath(saveFile.getAbsolutePath())
                            .setUrl(tempUrl)));
        }
    }

}
