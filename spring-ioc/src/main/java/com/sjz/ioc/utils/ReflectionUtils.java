package com.sjz.ioc.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {
    /**
     * 反射注入
     * @param field
     * @param obj
     * @param value
     * @throws IllegalAccessException
     */
    public static void injectField(Field field, Object obj ,Object value) throws IllegalAccessException {

        if(field != null){
            field.setAccessible(true);
            field.set(obj, value);
        }
    }
}
