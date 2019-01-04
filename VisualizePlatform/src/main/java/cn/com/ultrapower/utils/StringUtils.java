package cn.com.ultrapower.utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/3/16.
 */
public class StringUtils {

    public static boolean isNotBlank(String str) {
        return org.apache.commons.lang.StringUtils.isNotBlank(str);
    }

    public static boolean isBlank(String str) {
        return org.apache.commons.lang.StringUtils.isBlank(str);
    }
}
