/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android_serialport_api.sample;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import android_serialport_api.utils.GPSRespUtil;
import android_serialport_api.utils.LogUtil;

public class MainMenu extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        providePermissions();
        final Button buttonSetup = (Button) findViewById(R.id.ButtonSetup);
        buttonSetup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, SerialPortPreferences.class));
            }
        });


        final Button buttonPoint = (Button) findViewById(R.id.point);
        buttonPoint.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, PointMainActivity.class));
            }
        });

        final Button buttonConsole = (Button) findViewById(R.id.ButtonConsole);
        buttonConsole.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, ConsoleActivity.class));
            }
        });

        final Button buttonLoopback = (Button) findViewById(R.id.ButtonLoopback);
        buttonLoopback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, LoopbackActivity.class));
            }
        });

        final Button button01010101 = (Button) findViewById(R.id.Button01010101);
        button01010101.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, Sending01010101Activity.class));
            }
        });

        final Button buttonAbout = (Button) findViewById(R.id.ButtonAbout);
        buttonAbout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
                builder.setTitle("About");
                builder.setMessage(R.string.about_msg);
                builder.show();
            }
        });

        final Button buttonQuit = (Button) findViewById(R.id.ButtonQuit);
        buttonQuit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainMenu.this.finish();
            }
        });

        String[] strtemp1 = "$GPRMC,094155.50,A,3959.9923624,N,11627.2951788,E,0.141,167.7,270821,0.0,E,A*3D".split(",");
       String bbb = strtemp1[7];
    }


    private void providePermissions() {

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE// ????????????
                , Manifest.permission.READ_EXTERNAL_STORAGE // ????????????
                , Manifest.permission.READ_PHONE_STATE//??????????????????
                , Manifest.permission.ACCESS_FINE_LOCATION//????????????
                , Manifest.permission.ACCESS_COARSE_LOCATION//WIFI??????
        };
        XXPermissions.with(MainMenu.this)
                // ?????????????????????
                //.permission(Permission.REQUEST_INSTALL_PACKAGES)
                // ?????????????????????
                //.permission(Permission.SYSTEM_ALERT_WINDOW)
                // ?????????????????????
                //.permission(Permission.NOTIFICATION_SERVICE)
                // ????????????????????????
                //.permission(Permission.WRITE_SETTINGS)
                // ??????????????????
                .permission(permissions)
                // ??????????????????
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            Toast.makeText(MainMenu.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                            LogUtil.d("MainMenu", Thread.currentThread().getName() + ",???????????????:" + BuildConfig.VERSION_NAME);

                        } else {
                            Toast.makeText(MainMenu.this, "?????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Toast.makeText(MainMenu.this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                            // ??????????????????????????????????????????????????????????????????
                            XXPermissions.startPermissionActivity(MainMenu.this, permissions);
                        } else {
                            Toast.makeText(MainMenu.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
