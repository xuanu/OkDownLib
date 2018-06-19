package aar.zeffect.cn.okdownlib;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import java.io.File;

import aar.zeffect.cn.okdownservice.bean.DownStatus;
import aar.zeffect.cn.okdownservice.utils.DownStr;
import aar.zeffect.cn.okdownservice.utils.DownUtils;

public class MainActivity extends Activity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(DownStr.ACTION_STATU));
        findViewById(R.id.startDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownUtils.addTask(mContext, "https://raw.githubusercontent.com/gitlabBestResource/bestapk/master/%E8%85%BE%E8%AE%AF%E8%A7%86%E9%A2%91VIP%E7%89%88v18.apk", new File(getExternalCacheDir(), "task1.temp"));
                DownUtils.addTask(mContext, "https://raw.githubusercontent.com/gitlabBestResource/bestapk/master/%E4%BC%98%E9%85%B7VIP%E7%89%88v60.apk", new File(getExternalCacheDir(), "task2.temp"));
                DownUtils.addTask(mContext, "https://raw.githubusercontent.com/gitlabBestResource/bestapk/master/%E8%8A%92%E6%9E%9CTV%20VIP%E7%89%88v11.apk", new File(getExternalCacheDir(), "task3.temp"));
                DownUtils.addTask(mContext, "https://raw.githubusercontent.com/gitlabBestResource/bestapk/master/%E7%88%B1%E5%A5%87%E8%89%BAVIP%E7%89%88v38.apk", new File(getExternalCacheDir(), "task4.temp"));
                DownUtils.addTask(mContext, "https://raw.githubusercontent.com/gitlabBestResource/bestapk/master/%E8%85%BE%E8%AE%AF%E8%A7%86%E9%A2%91VIP%E7%89%88v18.apk", new File(getExternalCacheDir(), "task5.temp"));
                DownUtils.addTask(mContext, "https://raw.githubusercontent.com/gitlabBestResource/bestapk/master/%E4%BC%98%E9%85%B7VIP%E7%89%88v60.apk", new File(getExternalCacheDir(), "task6.temp"));
                DownUtils.addTask(mContext, "https://raw.githubusercontent.com/gitlabBestResource/bestapk/master/%E8%8A%92%E6%9E%9CTV%20VIP%E7%89%88v11.apk", new File(getExternalCacheDir(), "task7.temp"));
                DownUtils.addTask(mContext, "https://raw.githubusercontent.com/gitlabBestResource/bestapk/master/%E7%88%B1%E5%A5%87%E8%89%BAVIP%E7%89%88v38.apk", new File(getExternalCacheDir(), "task8.temp"));
            }
        });

        findViewById(R.id.cancelOne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownUtils.cancelTask(mContext, "https://raw.githubusercontent.com/gitlabBestResource/bestapk/master/%E7%88%B1%E5%A5%87%E8%89%BAVIP%E7%89%88v38.apk", new File(getExternalCacheDir(), "task8.temp"));
            }
        });

        findViewById(R.id.cancelAllTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownUtils.cancelAllTask(mContext);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownStr.ACTION_STATU)) {
                DownStatus status = (DownStatus) intent.getSerializableExtra(DownStr.DATA);
                Log.e("zeffect", "下载信息：" + status.toString());
            }
        }
    };
}
