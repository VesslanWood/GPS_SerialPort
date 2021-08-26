package android_serialport_api.sample;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android_serialport_api.utils.GPSRespUtil;
import android_serialport_api.utils.TimeUtil;
import android_serialport_api.utils.wwcutils;

public class PointMainActivity extends SerialPortActivity {
    ProgressDialog progressDialog;
    private final static String TAG = "Point";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_main);
        progressDialog = new ProgressDialog(PointMainActivity.this);
        progressDialog.setTitle("正在打点");
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.editText);
                final String pointTxt = editText.getText().toString();
                if (TextUtils.isEmpty(pointTxt)) {
                    Toast.makeText(PointMainActivity.this, "请输入", Toast.LENGTH_LONG).show();
                    return;
                }
                if (startPoint) {
                    return;
                }
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pointGps.clear();
                        startPoint = true;
                        long start = System.currentTimeMillis();
                        long now = start;
                        while (startPoint && now - start < 10 * 1000) {

                            Log.d("www", ">>>" + now + ">>>" + start);
                            wwcutils.d(TAG, Thread.currentThread().getName() + ",>>>" + now + ">>>" + start);
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            now = System.currentTimeMillis();
                            if (pointGps.size() > 3) {
                                startPoint = false;
//                                break;
                            }
//                            if (now - start > 2000) {
//                                break;
//                            }
                        }
                        int count = pointGps.size();
                        wwcutils.e(TAG, Thread.currentThread().getName() + ",pointGps count =" + count + ",gga = " + gga);
                        if (count > 0 && ("4".equals(gga) || "5".equals(gga))) {
                            final String txtGga = gga;
                            double lat = 0;
                            double lon = 0;
                            try {
                                double latSum = 0;
                                double lonSum = 0;
                                for (GPSInfo gpsInfo : pointGps) {
                                    latSum += gpsInfo.latitude;
                                    lonSum += gpsInfo.longitude;
                                }
                                lat = latSum / count;
                                lon = lonSum / count;
                            } catch (Exception e) {
                                e.printStackTrace();
                                wwcutils.e(TAG, e.getStackTrace());
                            }

                            File file = new File(Environment
                                    .getExternalStorageDirectory().getAbsolutePath() + "/GPSPoint/");
                            wwcutils.e("pm", "file ===" + file.exists() + "===" + Environment
                                    .getExternalStorageDirectory().getAbsolutePath() + "/GPSPoint");
                            if (!file.exists()) {

                                boolean b = file.mkdirs();
                                wwcutils.e("pm", ">>>" + b);
                            }
                            File pointFile = new File(Environment
                                    .getExternalStorageDirectory().getAbsolutePath() + "/GPSPoint/" + "point.txt");
                            wwcutils.e("pm", "pointFile ===" + pointFile.exists());
                            if (!pointFile.exists()) {
                                try {
                                    pointFile.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }


                            String d = TimeUtil.date2Str(new Date(), TimeUtil.DEFAULT_TIME_FORMAT);
                            String msg = d + "," + pointTxt + "," + lon + "," + lat + "," + gga;
                            try {
                                FileWriter filerWriter = new FileWriter(pointFile, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
                                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                                bufWriter.write(msg);
                                bufWriter.newLine();
                                bufWriter.close();
                                filerWriter.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        progressDialog.dismiss();
                                        Toast.makeText(PointMainActivity.this, "打点成功", Toast.LENGTH_LONG).show();
                                        EditText editText = findViewById(R.id.editText);
                                        editText.setText("");
                                    } catch (Exception e) {
                                        wwcutils.e(TAG, e.getStackTrace());
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    AlertDialog alertDialog = new AlertDialog.Builder(PointMainActivity.this).create();
                                    alertDialog.setTitle("打点失败");
                                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    alertDialog.show();
                                }
                            });
                        }


                    }
                }).start();
            }
        });


        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File pointFile = new File(Environment
                        .getExternalStorageDirectory().getAbsolutePath() + "/GPSPoint/" + "point.txt");
                if (pointFile.exists()) {
                    pointFile.delete();
                }
                Toast.makeText(PointMainActivity.this, "清除成功", Toast.LENGTH_LONG).show();
            }
        });
    }

    private final StringBuilder receiveSb = new StringBuilder();

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        String s = new String(buffer, 0, size);
        receiveSb.append(s);
        if (GPSRespUtil.isFullResp(receiveSb.toString())) {
            wwcutils.e(TAG, Thread.currentThread().getName() + ",收←◆" + receiveSb.toString());
            parseGpsStr(receiveSb.toString());
            receiveSb.delete(0, receiveSb.length());
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private GPSInfo currentGps;
    private boolean startPoint;

    private List<GPSInfo> pointGps = new ArrayList<>();

    public static String parseLat(String lat, String type) {
        //纬度
        double latitude = Double.parseDouble(lat.substring(0, 2));
        latitude += Double.parseDouble(lat.substring(2)) / 60;
        if ("N".equals(type)) { //北纬
            return String.valueOf(latitude);
        } else { //南纬
            return "-" + String.valueOf(latitude);
        }
    }

    public static String parseLon(String lon, String type) {
        //经度
        double longitude = Double.parseDouble(lon.substring(0, 3));
        longitude += Double.parseDouble(lon.substring(3)) / 60;
        if ("E".equals(type)) {  //东经
            return String.valueOf(longitude);
        } else {  //西经
            return "-" + String.valueOf(longitude);
        }
    }

    private void parseGpsStr(String s) {

        final GPSInfo gpsInfo = new GPSInfo();
        boolean ggaGet = false;
        String[] strs = s.split("\n");//换行截取数据
        for (String str : strs) {
            if (str.startsWith("$GPGGA")) {
                try {
                    String[] strtemp1 = str.split(",");
                    if (strtemp1.length >= 7) {
                        gpsInfo.ggaType = strtemp1[6];
                    }

                    if (TextUtils.isEmpty(gpsInfo.ggaType)) {
                        gpsInfo.ggaType = "0";
                    }
                    if (strtemp1.length >= 6) {
                        gpsInfo.latitude = strtemp1[2].equals("") ? 0 : Double.parseDouble(parseLat(strtemp1[2], strtemp1[3]));
                        gpsInfo.longitude = strtemp1[4].equals("") ? 0 : Double.parseDouble(parseLon(strtemp1[4], strtemp1[5]));
                    } else {
                        gpsInfo.latitude = 0;
                        gpsInfo.longitude = 0;
                    }
                    ggaGet = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    wwcutils.e(TAG, Thread.currentThread().getName() + ",解析数据，parseGpsStr,Exception:" + Log.getStackTraceString(e));
                    ggaGet = false;
                }
//            } 、else if (str.startsWith("$GPRMC")) {
//                String[] strtemp1 = str.split(",");
//                gpsInfo.gpsStatus = strtemp1[2];
//                gpsInfo.speed = strtemp1[7].equals("") ? 0 : Double.parseDouble(strtemp1[7]) * 1.852;
//                gpsInfo.latitude = strtemp1[3].equals("") ? 0 : GPSTransforming(strtemp1[3]);
//                gpsInfo.longitude = strtemp1[5].equals("") ? 0 : GPSTransforming(strtemp1[5]);
            }
        }
        final long now = System.currentTimeMillis();


        if (!ggaGet) {
            return;
        }
//        if (now - lastUi < 200) {
//            wwcutils.e(TAG, "当前时间:" + TimeUtil.long2Str(now, TimeUtil.DEFAULT_TIME_FORMAT_MS) + ",上一次时间:" + TimeUtil.long2Str(lastUi, TimeUtil.DEFAULT_TIME_FORMAT_MS));
//            return;
//        }
        if (startPoint) {
            gga = "0";
            gga = gpsInfo.ggaType;
            if (gpsInfo.ggaType.equals("4")) {
                pointGps.clear();
                pointGps.add(gpsInfo);
                pointGps.add(gpsInfo);
                pointGps.add(gpsInfo);
                startPoint = false;
            } else if (gpsInfo.ggaType.equals("5")) {
                pointGps.add(gpsInfo);
            }
            //wwcutils.e(TAG, Thread.currentThread().getName() + ",解析的数据,startPoint = " + startPoint + ", gga = " + gga + ", pointGps = " + pointGps);
        }
        //wwcutils.e(TAG, Thread.currentThread().getName() + ",解析的数据,startPoint = " + startPoint + ", gpsInfo = " + gpsInfo);
        currentGps = gpsInfo;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lastUi = now;
                TextView g1 = findViewById(R.id.gps1);
                TextView g2 = findViewById(R.id.gps2);
                TextView g3 = findViewById(R.id.gps3);
                g1.setText("经度:" + gpsInfo.longitude);
                g2.setText("纬度:" + gpsInfo.latitude);
                g3.setText("GGA状态:" + gpsInfo.ggaType);


            }
        });

    }

    private long lastUi = 0;
    private String gga = "0";

    private static double GPSTransforming(String _Value) {
        double Ret = 0.0;
        String[] TempStr = _Value.split("\\.");
        String x = TempStr[0].substring(0, TempStr[0].length() - 2);
        String y = TempStr[0].substring(TempStr[0].length() - 2, 2);
        String z = TempStr[1].substring(0, 4);
        Ret = Double.parseDouble(x) + Double.parseDouble(y) / 60 + Double.parseDouble(z) / 600000;
        return Ret;
    }

    public static class GPSInfo {
        public double longitude;//经度
        public double latitude; //纬度
        public double speed;    //速度
        public String gpsStatus;//GPS状态 A=数据有效；V=数据无效
        public String ggaType;

        @Override
        public String toString() {
            return "GPSInfo>>> " +
                    "Longitude=" + longitude +
                    ", Latitude=" + latitude +
                    ", Speed=" + speed +
                    "km/h, GGAType='" + ggaType + '\'';
        }
    }
}