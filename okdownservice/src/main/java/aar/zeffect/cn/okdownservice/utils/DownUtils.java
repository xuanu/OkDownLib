package aar.zeffect.cn.okdownservice.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.liulishuo.okdownload.StatusUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import aar.zeffect.cn.okdownservice.OkDownService;
import aar.zeffect.cn.okdownservice.bean.DownStatus;
import aar.zeffect.cn.okdownservice.bean.Task;

public class DownUtils {


    /***
     * 添加任务
     * @param url 下载链接
     * @param cacheFile 保存文件
     */
    public static void addTask(Context pTarget, String url, File cacheFile) {
        if (pTarget == null) return;
        if (TextUtils.isEmpty(url)) return;
        if (cacheFile == null) return;
        addTask(pTarget, new Task()
                .setSavePath(cacheFile.getAbsolutePath())
                .setUrl(url));
    }

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
    public static void addTasks(Context pTarget, ArrayList<Task> tasks, Task infoTask, String tag) {
        if (pTarget == null) return;
        if (tasks == null || tasks.isEmpty()) return;
        if (infoTask == null) return;
        if (TextUtils.isEmpty(tag)) return;
        infoTask.setTag(tag);
        pTarget.startService(new Intent(pTarget, OkDownService.class)
                .setAction(DownStr.ADD_TASKS_ACTION)
                .putParcelableArrayListExtra(DownStr.TASKS, tasks)
                .putExtra(DownStr.DATA, infoTask));
    }


    public static void cancelTask(Context pTarget, String url, File cacheFile) {
        if (pTarget == null) return;
        if (TextUtils.isEmpty(url)) return;
        if (cacheFile == null) return;
        cancelTask(pTarget, new Task()
                .setSavePath(cacheFile.getAbsolutePath())
                .setUrl(url));
    }

    public static void cancelTask(Context pTarget, Task task) {
        if (pTarget == null) return;
        if (task == null) return;
        pTarget.startService(new Intent(pTarget, OkDownService.class)
                .setAction(DownStr.CANCEL_TASK_ACTION)
                .putExtra(DownStr.DATA, task));
    }

    public static void cancelTag(Context pTarget, String tag) {
        if (TextUtils.isEmpty(tag)) return;
        if (pTarget == null) return;
        pTarget.startService(new Intent(pTarget, OkDownService.class).setAction(DownStr.CANCEL_TAG_TASK_ACTION).putExtra(DownStr.DATA, tag));
    }

    public static void cancelAllTask(Context pTarget) {
        if (pTarget == null) return;
        pTarget.startService(new Intent(pTarget, OkDownService.class)
                .setAction(DownStr.CANCEL_ALL_TASK_ACTION));
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
