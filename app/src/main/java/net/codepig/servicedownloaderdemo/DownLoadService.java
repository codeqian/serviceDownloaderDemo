package net.codepig.servicedownloaderdemo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * 下载服务
 * Created by QZD on 2017/9/20.
 */

public class DownLoadService extends IntentService {
    private final String TAG="LOGCAT";
    public DownLoadService() {
        super("DownLoadService");//这就是个name
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    protected void onHandleIntent(Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            String downloadUrl = bundle.getString("download_url");

            Log.d(TAG,"下载启动："+downloadUrl);
            Thread.sleep(1_000);
            int count=0;
            while(count>100){
                count++;
                Log.d(TAG,"线程运行中");
                Thread.sleep(50);
            }
            Log.d(TAG,"下载结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
