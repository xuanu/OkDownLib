# OkDownLib
下载功能 [![](https://jitpack.io/v/xuanu/OkDownLib.svg)](https://jitpack.io/#xuanu/OkDownLib)

> 使用[OkDownload](https://github.com/lingochamp/okdownload)实现的下载功能，方便自己多个模块共用。

1. [DownUtils](https://github.com/xuanu/OkDownLib/blob/master/okdownservice/src/main/java/aar/zeffect/cn/okdownservice/utils/DownUtils.java),下载工具类
```
默认跳过已下载资源，不分块。
addTask(url,file);
isDown(url,file);
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
