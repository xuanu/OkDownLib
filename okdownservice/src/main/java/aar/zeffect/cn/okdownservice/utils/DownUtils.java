package aar.zeffect.cn.okdownservice.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.liulishuo.okdownload.StatusUtil;

import java.io.File;

import aar.zeffect.cn.okdownservice.OkDownService;

public class DownUtils {

    public static final String ADD_TASK_ACTION = "add.task.action";

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
                .setAction(ADD_TASK_ACTION)
                .putExtra(DownStr.URL, url)
                .putExtra(DownStr.PATH, cacheFile.getAbsolutePath()));
    }


    public static boolean isDown(String url, File cacheFile) {
        return StatusUtil.getStatus(url, cacheFile.getParent(), cacheFile.getName()) == StatusUtil.Status.COMPLETED;
    }

}
