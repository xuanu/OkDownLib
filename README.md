# OkDownLib
下载功能 [![](https://jitpack.io/v/xuanu/OkDownLib.svg)](https://jitpack.io/#xuanu/OkDownLib)

> 使用[OkDownload](https://github.com/lingochamp/okdownload)实现的下载功能，方便自己多个模块共用。

1. [DownUtils](https://github.com/xuanu/OkDownLib/blob/master/okdownservice/src/main/java/aar/zeffect/cn/okdownservice/utils/DownUtils.java),下载工具类
```
默认跳过已下载资源，不分块。
addTask(context,url,file);
addTask(context,task);
cancelTask(context,url,file);
cancelTask(context,task);
cancelAllTask(context);
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
