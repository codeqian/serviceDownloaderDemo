package net.codepig.servicedownloaderdemo;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载服务
 * Created by QZD on 2017/9/20.
 */

public class DownLoadService extends IntentService {
    private final String TAG="LOGCAT";
    private int fileLength, downloadLength;//文件大小
    private Handler handler = new Handler();
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
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

            File dirs = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");//文件保存地址
            if (!dirs.exists()) {// 检查文件夹是否存在，不存在则创建
                dirs.mkdir();
            }

            File file = new File(dirs, "boosj.apk");//输出文件名
            Log.d(TAG,"下载启动："+downloadUrl+" --to-- "+ file.getPath());
            // 开始下载
            downloadFile(downloadUrl, file);
            // 下载结束
            builder.setContentText("下载结束");
            manager.notify(30,builder.build());

            // 广播下载完成事件，通过广播调起对文件的处理。（就不多说了，看实际需要在app里接收广播就好了。）
            Intent sendIntent = new Intent("downloadComplete");
            sendIntent.putExtra("downloadFile", file.getPath());
            sendBroadcast(sendIntent);
            Log.d(TAG,"下载结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件下载
     * @param downloadUrl
     * @param file
     */
    private void downloadFile(String downloadUrl, File file){
        FileOutputStream _outputStream;//文件输出流
        try {
            _outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "找不到目录！");
            e.printStackTrace();
            return;
        }
        InputStream _inputStream = null;//文件输入流
        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection _downLoadCon = (HttpURLConnection) url.openConnection();
            _downLoadCon.setRequestMethod("GET");
            fileLength = Integer.valueOf(_downLoadCon.getHeaderField("Content-Length"));//文件大小
            _inputStream = _downLoadCon.getInputStream();
            int respondCode = _downLoadCon.getResponseCode();//服务器返回的响应码
            if (respondCode == 200) {
                handler.post(run);//更新下载进度
                byte[] buffer = new byte[1024*8];// 数据块，等下把读取到的数据储存在这个数组，这个东西的大小看需要定，不要太小。
                int len;
                while ((len = _inputStream.read(buffer)) != -1) {
                    _outputStream.write(buffer, 0, len);
                    downloadLength = downloadLength + len;
//                    Log.d(TAG, downloadLength + "/" + fileLength );
                }
            } else {
                Log.d(TAG, "respondCode:" + respondCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {//别忘了关闭流
                if (_outputStream != null) {
                    _outputStream.close();
                }
                if (_inputStream != null) {
                    _inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Runnable run = new Runnable() {
        public void run() {
            int _pec=(int) (downloadLength*100 / fileLength);
            builder.setProgress(100, _pec, false);//最大值、当前值、是否显示具体进度
            manager.notify(30,builder.build());
            handler.postDelayed(run, 1000);
        }
    };

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        handler.removeCallbacks(run);
        super.onDestroy();
    }

    public void sendprogressnotification(View view){
        builder = new NotificationCompat.Action.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle("下载文件").setContentText("下载中……");
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
