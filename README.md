# OkDownLib
下载功能 [![](https://jitpack.io/v/xuanu/OkDownLib.svg)](https://jitpack.io/#xuanu/OkDownLib)

> 使用[OkDownload](https://github.com/lingochamp/okdownload)实现的下载功能，方便自己多个模块共用。
> 可传入队列任务，全部完成才发送完成广播。

> 可通过Task添加任务，实现更多功能。[Task](https://github.com/xuanu/OkDownLib/blob/master/okdownservice/src/main/java/aar/zeffect/cn/okdownservice/bean/Task.java)
```
    private String url;//下载地址
    private String savePath;//保存地址
    private boolean wifiRequired = false;//使用WIFI下载
    private boolean showNotifi=false;//是否显示通知栏
    private Intent openIntent;//完成后的打开方式
    private String notifiTitle;//通知栏标题
    private String tag;//可以用tag标记单个任务或多个任务，可以在接收下载广播时用于区分哪个下载任务，可以通过tag来取消。存入md5(tag).info本地文件。

```

1. [DownUtils](https://github.com/xuanu/OkDownLib/blob/master/okdownservice/src/main/java/aar/zeffect/cn/okdownservice/utils/DownUtils.java),下载工具类
```
默认跳过已下载资源，不分块。
addTask(context,task);
addTasks(Context pTarget, ArrayList<Task> tasks, Task infoTask);//tag是肯定要传入的，用于发送进度广播时区分。infoTask可以用来存一些别的。参考单个任务。
cancelTask(context,task);
cancelAllTask();
cancelTag(context,string);//可以用tag标记单个任务或多个任务，通过tag来取消。存入md5(tag).info本地文件。
isCompleted(url,file);//是否完成
String statu2Str(DownStatus);//根据状态返回一个默认的字符，如下载中，已下载。
getStatus(intent);//从广播中直接拿到数据，拿不到返回null
```
2. 通过应用内广播接收回调

```
 LocalBroadcastManager.getInstance(mContext)
                    .sendBroadcast(new Intent(DownStr.ACTION_STATU).putExtra(DownStr.DATA, new DownStatus()
                            .setProgress(progress)
                            .setStatus(staus)
                            .setSavePath(task.getFile().getAbsolutePath())
                            .setUrl(task.getUrl())));
```
