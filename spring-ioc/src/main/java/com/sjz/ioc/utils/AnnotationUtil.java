package com.sjz.ioc.utils;

import com.sjz.ioc.annotations.Service;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shijun.
 * @date 2018/12/13 17:19
 * @description ${description}
 */
public class AnnotationUtil {

    private String packageName;

    private Set<Class<?>> classSet;

    private AnnotationUtil(String packageName) {
        this.packageName = packageName;
        try {
            this.classSet = Scanner.getClasses(packageName);
        } catch (Exception e) {
            e.printStackTrace();
            this.classSet = Collections.emptySet();
        }
    }

    private volatile static AnnotationUtil instance;

    public static AnnotationUtil getInstance(String packageName){
        if(instance == null){
            synchronized (AnnotationUtil.class){
                if(instance == null){
                    instance = new AnnotationUtil(packageName);
                }
            }
        }
        return instance;
    }

    /**
     * 获取注解的类
     * @param annoClass
     * @return
     */
    public Set<Class<?>> getAnnotation(Class<? extends Annotation> annoClass){
        return classSet.parallelStream().filter(cls -> cls.isAnnotationPresent(annoClass)).collect(Collectors.toSet());
    }

    public static void main(String[] args) {
        AnnotationUtil instance = AnnotationUtil.getInstance("com.sjz.ioc");

        Set<Class<?>> classSet = instance.getAnnotation(Service.class);

        for (Class<?> aClass : classSet) {

            System.out.println(aClass.getSimpleName() + "::" + aClass.getName());
        }
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<Class<?>> getClassSet() {
        return classSet;
    }

    public void setClassSet(Set<Class<?>> classSet) {
        this.classSet = classSet;
    }
}
