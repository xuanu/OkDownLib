package aar.zeffect.cn.okdownservice.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.liulishuo.okdownload.StatusUtil;

import java.io.File;

import aar.zeffect.cn.okdownservice.OkDownService;
import aar.zeffect.cn.okdownservice.bean.DownStatus;

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
        pTarget.startService(new Intent(pTarget, OkDownService.class)
                .setAction(DownStr.ADD_TASK_ACTION)
                .putExtra(DownStr.URL, url)
                .putExtra(DownStr.PATH, cacheFile.getAbsolutePath()));
    }


    public static boolean isDown(String url, File cacheFile) {
        return StatusUtil.getStatus(url, cacheFile.getParent(), cacheFile.getName()) == StatusUtil.Status.COMPLETED;
    }

    public static String statu2Str(DownStatus status) {
        if (status == null) return "未下载";
        String statuStr = status.getStatus();
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
