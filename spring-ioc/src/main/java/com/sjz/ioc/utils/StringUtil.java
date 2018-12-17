package com.sjz.ioc.utils;

/**
 * @author shijun.
 * @date 2018/12/13 17:11
 * @description 字符串操作类
 */
public class StringUtil {

    public static boolean isBlank(String text){
        if(null == text || "".equals(text)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
