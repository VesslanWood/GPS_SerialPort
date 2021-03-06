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

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import android_serialport_api.utils.ByteConvert;
import android_serialport_api.utils.GPSRespUtil;
import android_serialport_api.utils.LogUtil;
import android_serialport_api.utils.StringUtil;

public class ConsoleActivity extends SerialPortActivity implements CompoundButton.OnCheckedChangeListener {

    private static String TAG = ConsoleActivity.class.getName();

    TextView mReception;
    EditText Emission;
    Button Send, Clear;
    CheckBox hexSend;
    boolean isHex;
    private StringBuilder receiveSb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console);

//		setTitle("Loopback test");
        mReception = (TextView) findViewById(R.id.EditTextReception);
        MyClickListener listener = new MyClickListener();
        Send = (Button) findViewById(R.id.Send);
        Clear = (Button) findViewById(R.id.Clear);
        Send.setText("Send");
        Clear.setText("Clear");
        Clear.setOnClickListener(listener);
        Send.setOnClickListener(listener);
        Emission = (EditText) findViewById(R.id.EditTextEmission);
        hexSend = (CheckBox) findViewById(R.id.hex);
        hexSend.setOnCheckedChangeListener(this);

    }

    class MyClickListener implements OnClickListener {
        Boolean thread_flag = true, PWM_flag = true, LASER_flag;

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Send:
                    try {
                        String str = Emission.getText().toString();
                        if (isHex) {
                            //byte[] strByte = toByteArray(str);
                            byte[] strByte = new byte[5];
                            strByte[0] = (byte) 0XBE;
                            strByte[1] = (byte) 0XBE;
                            strByte[2] = (byte) 0XBE;
                            strByte[3] = (byte) 0XBE;
                            strByte[4] = (byte) 0XBE;

                            mOutputStream.write(strByte);
                            mOutputStream.write('\n');
                            mOutputStream.flush();
                            LogUtil.e("", ",????????????1:" + ByteConvert.bytesToHex(strByte));

                        } else {
                            //mOutputStream.write(Emission.getText().toString().getBytes());
                            byte[] strByte = new byte[5];
                            strByte[0] = (byte) 0XBE;
                            strByte[1] = (byte) 0XBE;
                            strByte[2] = (byte) 0XBE;
                            strByte[3] = (byte) 0XBE;
                            strByte[4] = (byte) 0XBE;

                            mOutputStream.write(strByte);
                            mOutputStream.write('\n');
                            mOutputStream.flush();
                            LogUtil.e("", ",????????????2:" + ByteConvert.bytesToHex(strByte));
                        }

                        //mOutputStream.write('\n');
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;

                case R.id.Clear:
                    mReception.setText("");
                    Emission.setText("");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected synchronized void onDataReceived(final byte[] buffer, final int size) {
        try {
            String[] s = new String(buffer, 0, size).split("\n");
            if (s.length <= 0) {
                return;
            }
            receiveSb.append(s[0]);
            if (GPSRespUtil.isFullResp(receiveSb.toString())) {
                String withOutFit = StringUtil.replaceBlank(receiveSb.toString());
                if (withOutFit.substring(withOutFit.indexOf("*") + 1).length() < 2) {
                    return;
                }
                if (withOutFit.contains("BESTPOSA")) {
                    LogUtil.d("", ",?????????" + withOutFit);
                } else {
                    LogUtil.d("", ",?????????" + withOutFit + ",????????????:" + GPSRespUtil.xorString(withOutFit));
                }
                receiveSb.delete(0, receiveSb.length());
            }
            if (s.length > 1) {
                receiveSb.append(s[1]);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    if (mReception != null) {
                        if (isHex) {
                            mReception.append(toHexString(buffer, size));
                        } else {
                            mReception.append(new String(buffer, 0, size));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "onDataReceived Exception:" + Log.getStackTraceString(e));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            isHex = true;
        } else {
            isHex = false;
        }
    }

    /**
     * ???String?????????byte[]??????
     *
     * @param arg ???????????????String??????
     * @return ????????????byte[]??????
     */
    private byte[] toByteArray(String arg) {
        if (arg != null) {
            /* 1.?????????String??????' '????????????String?????????char?????? */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }
            /* ???char??????????????????????????????????????????????????? */
            int EvenLength = (length % 2 == 0) ? length : length + 1;
            if (EvenLength != 0) {
                int[] data = new int[EvenLength];
                data[EvenLength - 1] = 0;
                for (int i = 0; i < length; i++) {
                    if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                        data[i] = NewArray[i] - '0';
                    } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                        data[i] = NewArray[i] - 'a' + 10;
                    } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                        data[i] = NewArray[i] - 'A' + 10;
                    }
                }
                /* ??? ??????char???????????????????????????16???????????? */
                byte[] byteArray = new byte[EvenLength / 2];
                for (int i = 0; i < EvenLength / 2; i++) {
                    byteArray[i] = (byte) (data[i * 2] * 16 + data[i * 2 + 1]);
                }
                return byteArray;
            }
        }
        return new byte[]{};
    }

    /**
     * ???byte[]???????????????String??????
     *
     * @param arg    ???????????????byte[]??????
     * @param length ???????????????????????????
     * @return ????????????String??????
     */
    private String toHexString(byte[] arg, int length) {
        String result = new String();
        if (arg != null) {
            for (int i = 0; i < length; i++) {
                result = result
                        + (Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])) + " ";
            }
            return result;
        }
        return "";
    }

}
