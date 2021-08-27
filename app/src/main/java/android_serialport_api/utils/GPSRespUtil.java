package android_serialport_api.utils;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * <p>文件描述：解析GPS的工具<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2021/8/24<p>
 * <p>更新时间：2021/8/24<p>
 * <p>版本号：<p>
 * <p>邮箱：jambestwick@126.com<p>
 */
public class GPSRespUtil implements Serializable {
    private static final long serialVersionUID = -3605916896608969049L;

    /**
     * 是否包含开头的$
     **/
    public static boolean hasHead(String data) {
        if (TextUtils.isEmpty(data)) {
            return false;
        }
        return data.contains("#") || data.contains("$");
    }

    public static boolean hasEnd(String data) {
        if (TextUtils.isEmpty(data)) {
            return false;
        }
        return data.contains("*");
    }

    public static boolean isFullResp(String data) {
        if (!hasHead(data)) {
            return false;
        }
        if (!hasEnd(data)) {
            return false;
        }
        return true;
    }

    public static boolean xorString(String origin) {
        String valueData = "";
        if (origin.contains("#")) {
            valueData = origin.substring(origin.indexOf("#") + 1, origin.indexOf("*"));
        }
        if (origin.contains("$")) {
            valueData = origin.substring(origin.indexOf("$") + 1, origin.indexOf("*"));
        }

        byte temp = 0;
        for (int i = 0; i < valueData.length(); i++) {

            temp ^= valueData.charAt(i);

        }
        String xorHex = ByteConvert.bytesToHex(new byte[]{temp});
        String end = origin.substring(origin.indexOf("*") + 1);
        return xorHex.equalsIgnoreCase(end);
    }


    static long CRC32_POLYNOMIAL = 0xEDB88320L;

    /* --------------------------------------------------------------------------
    Calculate a CRC value
    value: Value
    -------------------------------------------------------------------------- */
    private static long CalcCRC32Value(int value) {
        int i;
        long ulCRC;
        ulCRC = value;
        for (i = 8; i > 0; --i) {
            if ((ulCRC & 1) == 0)
                ulCRC = (ulCRC >> 1) ^ CRC32_POLYNOMIAL;
            else
                ulCRC >>= 1;
        }
        return ulCRC;
    }
/* --------------------------------------------------------------------------
Calculates the CRC-32 of a data block
ulCount: Number of bytes in the data block
ucBuff: Data block
-------------------------------------------------------------------------- */

    public static long CalcBlockCRC32(long ulCount, String ucBuff) {
        char ucHash = (char) ucBuff.hashCode();
        long ulTmp1;
        long ulTmp2;
        long ulCRC = 0;
        while (ulCount-- != 0) {
            ulTmp1 = (ulCRC >> 8) & 0x00FFFFFFL;
            ulTmp2 = CalcCRC32Value(((int) ulCRC ^ ucHash++) & 0xFF);
            ulCRC = ulTmp1 ^ ulTmp2;
        }
        return ulCRC;
    }

}
