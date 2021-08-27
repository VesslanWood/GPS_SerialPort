package android_serialport_api.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    //文件夹目录
    private static final String PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/GPSPoint/crash_log/";
    //文件名
    private static final String FILE_NAME = "crash";
    //文件名后缀
    private static final String FILE_NAME_SUFFIX = ".trace";
    //上下文
    private Context mContext;
    private static final String TAG = CrashHandler.class.getName();
    private String ex;
    private String time;
    List<File> files = new ArrayList<>();

    //单例模式
    private static CrashHandler sInstance = new CrashHandler();

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    /**
     * 初始化方法
     *
     * @param context
     */
    public void init(Context context) {
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        //获取Context，方便内部使用
        mContext = context.getApplicationContext();
    }

    /**
     * 捕获异常回掉
     *
     * @param thread 当前线程
     * @param ex     异常信息
     */
    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
// 	  new Thread(new Runnable() {
//           @Override
//           public void run() {
        //导出异常信息到SD卡
        files.clear();
        LogUtil.e(TAG, "=========" + Log.getStackTraceString(ex));
        dumpExceptionToSDCard(ex);
        SystemClock.sleep(2000);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 导出异常信息到SD卡
     *
     * @param ex
     */
    private void dumpExceptionToSDCard(Throwable ex) {
        LogUtil.e(TAG, "==============================" + ex.toString());
        //System.out.println("=-="+ex.toString());
        this.ex = ex.toString();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        //创建文件夹
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //获取当前时
        time = TimeUtil.now_mill();
        //以当前时间创建log文件
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            //输出流操作
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //导出手机信息和异常信息
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            pw.println("发生异常时间：" + time);
            pw.println("应用版本：" + pi.versionName);
            pw.println("应用版本号：" + pi.versionCode);
            pw.println("android版本号：" + Build.VERSION.RELEASE);
            pw.println("android版本号API：" + Build.VERSION.SDK_INT);
            pw.println("手机制造商:" + Build.MANUFACTURER);
            pw.println("手机型号：" + Build.MODEL);
            pw.println("硬件芯片：" + Build.HARDWARE);

            ex.printStackTrace(pw);
            //System.out.println("=-======="+"发生异常时间：" + time+"应用版本：" + pi.versionName+"手机型号：" + Build.MODEL + "安卓版本号====" + Build.VERSION.RELEASE+"版本api===" + Build.VERSION.SDK_INT);

            //关闭输出流
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, Thread.currentThread().getName() + ",Exception :" + Log.getStackTraceString(e));
        }
        files.add(file);
    }

    public void deleteCrash(List<File> files) {
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).exists()) {
                files.get(i).delete();
            }
        }
    }
}

