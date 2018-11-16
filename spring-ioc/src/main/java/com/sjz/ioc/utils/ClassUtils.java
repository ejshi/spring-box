package com.sjz.ioc.utils;

public class ClassUtils {

    public static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class loadClass(String className){

        try {
            return getDefaultClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            System.out.println(String.format("className(%s) not found ", className));
            e.printStackTrace();
        }
        return null;
    }
}
