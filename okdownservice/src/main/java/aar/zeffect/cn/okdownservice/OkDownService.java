package aar.zeffect.cn.okdownservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import java.io.File;

import aar.zeffect.cn.okdownservice.utils.DownStr;
import aar.zeffect.cn.okdownservice.utils.DownUtils;


/***
 *
 */
public class OkDownService extends Service {
    private DownImp downImp;

    @Override
    public void onCreate() {
        super.onCreate();
        downImp = new DownImp(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            parseAction(action, intent);
        }
        return START_STICKY;
    }


    private void parseAction(String action, Intent intent) {
        if (TextUtils.isEmpty(action)) return;
        if (action.equals(DownUtils.ADD_TASK_ACTION)) {
            if (downImp == null) downImp = new DownImp(this);
            String url = intent.getStringExtra(DownStr.URL);
            String savePath = intent.getStringExtra(DownStr.PATH);
            if (TextUtils.isEmpty(url) || TextUtils.isEmpty(savePath)) return;
            else downImp.addTask(url, new File(savePath));
        }
    }

}
