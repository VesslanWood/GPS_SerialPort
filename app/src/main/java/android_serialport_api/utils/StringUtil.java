package android_serialport_api.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>文件描述：<p>
 * <p>作者：jambestwick<p>
 * <p>创建时间：2021/8/27<p>
 * <p>更新时间：2021/8/27<p>
 * <p>版本号：<p>
 * <p>邮箱：jambestwick@126.com<p>
 */

public class StringUtil {
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\\t|\\r|\\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

}
