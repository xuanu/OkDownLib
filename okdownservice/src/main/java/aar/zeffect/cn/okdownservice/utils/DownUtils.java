package aar.zeffect.cn.okdownservice.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.StatusUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import aar.zeffect.cn.okdownservice.DownImp;
import aar.zeffect.cn.okdownservice.OkDownService;
import aar.zeffect.cn.okdownservice.bean.DownStatus;
import aar.zeffect.cn.okdownservice.bean.Task;

public class DownUtils {


    public static void addTask(Context pTarget, Task task) {
        if (pTarget == null) return;
        if (task == null) return;
        pTarget.startService(new Intent(pTarget, OkDownService.class)
                .setAction(DownStr.ADD_TASK_ACTION)
                .putExtra(DownStr.DATA, task));
    }

    /**
     * 如果
     *
     * @param pTarget
     * @param tasks    需要下载的列表
     * @param infoTask 用于标记任务的Task,我回调用的时候会用这个Task的tag(必须有值)，可以用这个来确定是不是我需要的队列。
     */
    public static void addTasks(Context pTarget, ArrayList<Task> tasks, Task infoTask) {
        if (pTarget == null) return;
        if (tasks == null || tasks.isEmpty()) return;
        if (infoTask == null) return;
        pTarget.startService(new Intent(pTarget, OkDownService.class)
                .setAction(DownStr.ADD_TASKS_ACTION)
                .putParcelableArrayListExtra(DownStr.TASKS, tasks)
                .putExtra(DownStr.DATA, infoTask));
    }


    public static void cancelTask(Context pTarget, Task tempTask) {
        if (pTarget == null) return;
        if (tempTask == null) return;
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
            DownImp.sendBroad(pTarget, DownStatus.STATUS_COMPLETE, 100, task.getUrl(), task.getFile(), tempTask.getTag());
            return;
        }
        if (StatusUtil.isSameTaskPendingOrRunning(task)) {
            OkDownload.with().downloadDispatcher().cancel(task);
            return;
        }
        DownImp.sendBroad(pTarget, DownStatus.STATUS_CANCEL, 0, task.getUrl(), task.getFile(), tempTask.getTag());
    }

    public static void cancelTag(Context pTarget, String tag) {
        if (TextUtils.isEmpty(tag)) return;
        if (pTarget == null) return;
        if (TextUtils.isEmpty(tag)) return;
        File tagFile = new File(pTarget.getExternalFilesDir("tag"), MD5Crypto.Md5_32(tag) + ".info");
        if (tagFile.isDirectory()) tagFile.delete();
        if (tagFile.exists()) {
            List<Integer> taskids = DownImp.string2TaskId(FileUtils.read(tagFile.getAbsolutePath()));
            if (taskids != null && !taskids.isEmpty()) {
                for (int i = 0; i < taskids.size(); i++) {
                    OkDownload.with().downloadDispatcher().cancel(taskids.get(i));
                }
                DownImp.sendBroad(pTarget, DownStatus.STATUS_CANCEL, 0, "", null, tag);
            }
        }
        if (tagFile != null && tagFile.exists()) {
            tagFile.delete();
        }
    }

    public static void cancelAllTask() {
        OkDownload.with().downloadDispatcher().cancelAll();
    }


    public static boolean isCompleted(String url, File cacheFile) {
        return StatusUtil.getStatus(url, cacheFile.getParent(), cacheFile.getName()) == StatusUtil.Status.COMPLETED;
    }

    public static String statu2Str(DownStatus status) {
        if (status == null) return "未下载";
        String statuStr = status.getStatus();
        return statu2Str(statuStr);
    }

    public static String statu2Str(String status) {
        if (TextUtils.isEmpty(status)) return "未下载";
        String statuStr = status;
        if (statuStr.equals(DownStatus.STATUS_START)) return "已开始";
        else if (statuStr.equals(DownStatus.STATUS_CANCEL)) return "已取消";
        else if (statuStr.equals(DownStatus.STATUS_COMPLETE)) return "已完成";
        else if (statuStr.equals(DownStatus.STATUS_CONNECT)) return "连接中";
        else if (statuStr.equals(DownStatus.STATUS_ERROR)) return "错误";
        else if (statuStr.equals(DownStatus.STATUS_PROGRESS)) return "下载中";
        else if (statuStr.equals(DownStatus.STATUS_RETRY)) return "重试中";
        else if (statuStr.equals(DownStatus.STATUS_WARN)) return "警告";
        else return "未下载";
    }


    /**
     * 获取广播中的下载信息
     *
     * @param intent
     * @return
     */
    public static DownStatus getStatus(Intent intent) {
        if (intent == null) return null;
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) return null;
        if (action.equals(DownStr.ACTION_STATU)) {
            DownStatus status = (DownStatus) intent.getSerializableExtra(DownStr.DATA);
            return status;
        } else return null;
    }

}
