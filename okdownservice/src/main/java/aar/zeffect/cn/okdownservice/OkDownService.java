package aar.zeffect.cn.okdownservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import java.io.File;

import aar.zeffect.cn.okdownservice.bean.Task;
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
        if (action.equals(DownStr.ADD_TASK_ACTION)) {
            if (downImp == null) downImp = new DownImp(this);
            if (intent.hasExtra(DownStr.DATA)) {
                Object dataObject = intent.getSerializableExtra(DownStr.DATA);
                if (dataObject instanceof Task) {
                    downImp.addTask((Task) dataObject);
                }
            }
        } else if (action.equals(DownStr.CANCEL_TASK_ACTION)) {
            if (downImp != null && intent.hasExtra(DownStr.DATA)) {
                Object dataObject = intent.getSerializableExtra(DownStr.DATA);
                if (dataObject instanceof Task) {
                    downImp.cancel((Task) dataObject);
                }
            }
        } else if (action.equals(DownStr.CANCEL_ALL_TASK_ACTION)) {
            if (downImp != null) downImp.cancelAll();
        }
    }

}
