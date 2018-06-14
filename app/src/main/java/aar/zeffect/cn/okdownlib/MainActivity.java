package aar.zeffect.cn.okdownlib;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;

import aar.zeffect.cn.okdownservice.bean.DownStatus;
import aar.zeffect.cn.okdownservice.utils.DownStr;
import aar.zeffect.cn.okdownservice.utils.DownUtils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(DownStr.ACTION_STATU));
        DownUtils.addTask(this, "http://pic.616pic.com/ys_b_img/00/66/73/9KnqqgZBFe.jpg", new File(getExternalCacheDir(), "task.temp"));
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
